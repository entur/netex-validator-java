package org.entur.netex.validation.validator.jaxb;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.xml.bind.JAXBElement;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.index.impl.NetexEntitiesIndexImpl;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.test.jaxb.support.JAXBUtils;
import org.entur.netex.validation.validator.model.QuayId;
import org.entur.netex.validation.validator.model.ScheduledStopPointId;
import org.entur.netex.validation.validator.model.ServiceLinkId;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.QuayRefStructure;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ServiceLink;

class DefaultCommonDataRepositoryTest {

  public static final String TEST_REPORT_ID = "TEST_REPORT_ID";

  public static final String TEST_SCHEDULED_STOP_POINT_ID_1 = "TST:ScheduledStopPoint:1";

  public static final String TEST_SCHEDULED_STOP_POINT_REF_ID =
    "TST:ScheduledStopPointRef:1";

  public static final String TEST_FLEXIBLE_STOP_POINT_REF_ID =
    "TST:FlexibleStopPlaceRef:1";

  public static final String TEST_QUAY_ID_1 = "TST:Quay:1";

  public static final String TEST_SCHEDULED_STOP_POINT_ID_2 = "TST:ScheduledStopPoint:2";

  public static final String TEST_QUAY_ID_2 = "TST:Quay:2";

  public static final String TEST_SERVICE_LINK_ID = "TST:ServiceLink:1";

  @Test
  void testStopPlaceToFlexibleStopPlaceMapping() {
    DefaultCommonDataRepository repository = new DefaultCommonDataRepository();
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();

    netexEntitiesIndex
      .getFlexibleStopPlaceIdByStopPointRefIndex()
      .put(TEST_SCHEDULED_STOP_POINT_REF_ID, TEST_FLEXIBLE_STOP_POINT_REF_ID);
    repository.collect(TEST_REPORT_ID, netexEntitiesIndex);
    assertEquals(
      TEST_FLEXIBLE_STOP_POINT_REF_ID,
      repository.getFlexibleStopPlaceRefByStopPointRef(
        TEST_REPORT_ID,
        TEST_SCHEDULED_STOP_POINT_REF_ID
      )
    );
  }

  @Test
  void testStopPlaceToFlexibleStopPlaceRefMappingThrowsWhenNoValidationReport() {
    DefaultCommonDataRepository repository = new DefaultCommonDataRepository();
    assertThrows(
      NetexValidationException.class,
      () ->
        repository.getFlexibleStopPlaceRefByStopPointRef(
          TEST_REPORT_ID,
          TEST_SCHEDULED_STOP_POINT_REF_ID
        )
    );
  }

  @Test
  void testSharedScheduledStopPoints() {
    DefaultCommonDataRepository repository = new DefaultCommonDataRepository();
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();

    PassengerStopAssignment passengerStopAssignment = passengerStopAssignment(
      TEST_SCHEDULED_STOP_POINT_ID_1,
      TEST_QUAY_ID_1
    );

    netexEntitiesIndex
      .getPassengerStopAssignmentsByStopPointRefIndex()
      .put(TEST_SCHEDULED_STOP_POINT_ID_1, passengerStopAssignment);

    repository.collect(TEST_REPORT_ID, netexEntitiesIndex);

    assertTrue(repository.hasSharedScheduledStopPoints(TEST_REPORT_ID));

    ScheduledStopPointId scheduledStopPointId = new ScheduledStopPointId(
      TEST_SCHEDULED_STOP_POINT_ID_1
    );
    assertEquals(
      new QuayId(TEST_QUAY_ID_1),
      repository.quayIdForScheduledStopPoint(scheduledStopPointId, TEST_REPORT_ID)
    );

    repository.cleanUp(TEST_REPORT_ID);
    assertFalse(repository.hasSharedScheduledStopPoints(TEST_REPORT_ID));
    assertThrows(
      NetexValidationException.class,
      () -> repository.quayIdForScheduledStopPoint(scheduledStopPointId, TEST_REPORT_ID)
    );
  }

  @Test
  void testServiceLinks() {
    DefaultCommonDataRepository repository = new DefaultCommonDataRepository();
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();

    PassengerStopAssignment passengerStopAssignment1 = passengerStopAssignment(
      TEST_SCHEDULED_STOP_POINT_ID_1,
      TEST_QUAY_ID_1
    );
    PassengerStopAssignment passengerStopAssignment2 = passengerStopAssignment(
      TEST_SCHEDULED_STOP_POINT_ID_2,
      TEST_QUAY_ID_2
    );

    netexEntitiesIndex
      .getPassengerStopAssignmentsByStopPointRefIndex()
      .put(TEST_SCHEDULED_STOP_POINT_ID_1, passengerStopAssignment1);

    netexEntitiesIndex
      .getPassengerStopAssignmentsByStopPointRefIndex()
      .put(TEST_SCHEDULED_STOP_POINT_ID_2, passengerStopAssignment2);

    ServiceLink serviceLink = new ServiceLink()
      .withId(TEST_SERVICE_LINK_ID)
      .withFromPointRef(
        new ScheduledStopPointRefStructure().withRef(TEST_SCHEDULED_STOP_POINT_ID_1)
      )
      .withToPointRef(
        new ScheduledStopPointRefStructure().withRef(TEST_SCHEDULED_STOP_POINT_ID_2)
      );

    netexEntitiesIndex.getServiceLinkIndex().put(TEST_SERVICE_LINK_ID, serviceLink);
    repository.collect(TEST_REPORT_ID, netexEntitiesIndex);

    assertNotNull(
      repository.fromToScheduledStopPointIdForServiceLink(
        new ServiceLinkId(TEST_SERVICE_LINK_ID),
        TEST_REPORT_ID
      )
    );
  }

  private static PassengerStopAssignment passengerStopAssignment(
    String stopPointId,
    String quayId
  ) {
    JAXBElement<? extends QuayRefStructure> quayRef = JAXBUtils.createJaxbElement(
      new QuayRefStructure().withRef(quayId)
    );

    JAXBElement<ScheduledStopPointRefStructure> stopPointRef =
      JAXBUtils.createJaxbElement(
        new ScheduledStopPointRefStructure().withRef(stopPointId)
      );

    return new PassengerStopAssignment()
      .withScheduledStopPointRef(stopPointRef)
      .withQuayRef(quayRef);
  }
}

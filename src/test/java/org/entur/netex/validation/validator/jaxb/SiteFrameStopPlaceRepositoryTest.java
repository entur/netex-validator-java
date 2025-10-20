package org.entur.netex.validation.validator.jaxb;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.xml.bind.JAXBElement;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.index.impl.NetexEntitiesIndexImpl;
import org.entur.netex.validation.validator.model.QuayCoordinates;
import org.entur.netex.validation.validator.model.QuayId;
import org.entur.netex.validation.validator.model.StopPlaceId;
import org.entur.netex.validation.validator.model.TransportModeAndSubMode;
import org.entur.netex.validation.validator.model.TransportSubMode;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.*;

class SiteFrameStopPlaceRepositoryTest {

  private static final String STOP_PLACE_ID = "NSR:StopPlace:1";
  private static final String QUAY_ID = "NSR:Quay:1";
  private static final int QUAY_LATITUDE = 1;
  private static final int QUAY_LONGITUDE = 2;
  private static final String STOP_PLACE_NAME = "STOP_PLACE_NAME";
  private static final AllVehicleModesOfTransportEnumeration STOP_PLACE_TRANSPORT_MODE =
    AllVehicleModesOfTransportEnumeration.BUS;
  private static final BusSubmodeEnumeration STOP_PLACE_TRANSPORT_SUBMODE =
    BusSubmodeEnumeration.LOCAL_BUS;

  @Test
  void testHasStopPlaceId() {
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();
    netexEntitiesIndex.getStopPlaceIndex().put(STOP_PLACE_ID, List.of(new StopPlace()));

    JAXBValidationContext validationContext = createValidationContext(netexEntitiesIndex);
    SiteFrameStopPlaceRepository repository = new SiteFrameStopPlaceRepository(
      validationContext
    );

    assertTrue(repository.hasStopPlaceId(new StopPlaceId(STOP_PLACE_ID)));
  }

  @Test
  void testHasNotStopPlaceId() {
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();
    JAXBValidationContext validationContext = createValidationContext(netexEntitiesIndex);
    SiteFrameStopPlaceRepository repository = new SiteFrameStopPlaceRepository(
      validationContext
    );

    assertFalse(repository.hasStopPlaceId(new StopPlaceId(STOP_PLACE_ID)));
  }

  @Test
  void testHasQuayId() {
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();
    netexEntitiesIndex.getQuayIndex().put(QUAY_ID, List.of(new Quay()));

    JAXBValidationContext validationContext = createValidationContext(netexEntitiesIndex);
    SiteFrameStopPlaceRepository repository = new SiteFrameStopPlaceRepository(
      validationContext
    );

    assertTrue(repository.hasQuayId(new QuayId(QUAY_ID)));
  }

  @Test
  void testHasNotQuayId() {
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();

    JAXBValidationContext validationContext = createValidationContext(netexEntitiesIndex);
    SiteFrameStopPlaceRepository repository = new SiteFrameStopPlaceRepository(
      validationContext
    );

    assertFalse(repository.hasQuayId(new QuayId(QUAY_ID)));
  }

  @Test
  void testGetCoordinatesForQuayId() {
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();
    Quay quay = new Quay();
    SimplePoint_VersionStructure centroid = new SimplePoint_VersionStructure();
    centroid.setLocation(
      new LocationStructure()
        .withLatitude(BigDecimal.valueOf(QUAY_LATITUDE))
        .withLongitude(BigDecimal.valueOf(QUAY_LONGITUDE))
    );
    quay.setCentroid(centroid);
    netexEntitiesIndex.getQuayIndex().put(QUAY_ID, List.of(quay));

    JAXBValidationContext validationContext = createValidationContext(netexEntitiesIndex);
    SiteFrameStopPlaceRepository repository = new SiteFrameStopPlaceRepository(
      validationContext
    );

    assertEquals(
      new QuayCoordinates(QUAY_LONGITUDE, QUAY_LATITUDE),
      repository.getCoordinatesForQuayId(new QuayId(QUAY_ID))
    );
  }

  @Test
  void testGetCoordinatesForNonExistingQuayId() {
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();
    JAXBValidationContext validationContext = createValidationContext(netexEntitiesIndex);
    SiteFrameStopPlaceRepository repository = new SiteFrameStopPlaceRepository(
      validationContext
    );

    assertNull(repository.getCoordinatesForQuayId(new QuayId(QUAY_ID)));
  }

  @Test
  void testGetStopPlaceNameForQuayId() {
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();
    StopPlace stopPlace = new StopPlace();
    stopPlace.setName(new MultilingualString().withValue(STOP_PLACE_NAME));
    netexEntitiesIndex.getStopPlaceIndex().put(STOP_PLACE_ID, List.of(stopPlace));
    Quay quay = new Quay();
    netexEntitiesIndex.getQuayIndex().put(QUAY_ID, List.of(quay));
    netexEntitiesIndex.getStopPlaceIdByQuayIdIndex().put(QUAY_ID, STOP_PLACE_ID);

    JAXBValidationContext validationContext = createValidationContext(netexEntitiesIndex);
    SiteFrameStopPlaceRepository repository = new SiteFrameStopPlaceRepository(
      validationContext
    );

    assertEquals(
      STOP_PLACE_NAME,
      repository.getStopPlaceNameForQuayId(new QuayId(QUAY_ID))
    );
  }

  @Test
  void testGetStopPlaceNameForNonExistingQuayId() {
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();
    JAXBValidationContext validationContext = createValidationContext(netexEntitiesIndex);
    SiteFrameStopPlaceRepository repository = new SiteFrameStopPlaceRepository(
      validationContext
    );

    assertNull(repository.getStopPlaceNameForQuayId(new QuayId(QUAY_ID)));
  }

  @Test
  void testIsParentStop() {
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();
    StopPlace parentStopPlace = new StopPlace();
    StopPlace nonParentStopPlace = new StopPlace();
    String nonParentStopPlaceId = "NSR:StopPlace:2";
    parentStopPlace.setKeyList(
      new KeyListStructure()
        .withKeyValue(
          new KeyValueStructure().withKey("IS_PARENT_STOP_PLACE").withValue("true")
        )
    );
    netexEntitiesIndex.getStopPlaceIndex().put(STOP_PLACE_ID, List.of(parentStopPlace));
    netexEntitiesIndex
      .getStopPlaceIndex()
      .put(nonParentStopPlaceId, List.of(nonParentStopPlace));

    JAXBValidationContext validationContext = createValidationContext(netexEntitiesIndex);
    SiteFrameStopPlaceRepository repository = new SiteFrameStopPlaceRepository(
      validationContext
    );

    assertTrue(repository.isParentStop(new StopPlaceId(STOP_PLACE_ID)));
    assertFalse(repository.isParentStop(new StopPlaceId(nonParentStopPlaceId)));
  }

  @Test
  void testGetQuaysForStopPlaceId() {
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();
    StopPlace stopPlace = new StopPlace();
    Quay quay1 = new Quay().withId("NSR:Quay:1");
    Quay quay2 = new Quay().withId("NSR:Quay:2");
    stopPlace.setQuays(
      new Quays_RelStructure()
        .withQuayRefOrQuay(
          List.of(
            new JAXBElement<>(
              new QName("http://www.netex.org.uk/netex", "Quay"),
              Quay.class,
              quay1
            ),
            new JAXBElement<>(
              new QName("http://www.netex.org.uk/netex", "Quay"),
              Quay.class,
              quay2
            )
          )
        )
    );
    netexEntitiesIndex.getStopPlaceIndex().put(STOP_PLACE_ID, List.of(stopPlace));

    JAXBValidationContext validationContext = createValidationContext(netexEntitiesIndex);
    SiteFrameStopPlaceRepository repository = new SiteFrameStopPlaceRepository(
      validationContext
    );

    assertEquals(
      Set.of(quay1.getId(), quay2.getId()),
      repository.getQuaysForStopPlaceId(new StopPlaceId(STOP_PLACE_ID))
    );
  }

  @Test
  void testGetTransportModesForQuayId() {
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();
    StopPlace stopPlace = new StopPlace()
      .withTransportMode(SiteFrameStopPlaceRepositoryTest.STOP_PLACE_TRANSPORT_MODE)
      .withBusSubmode(BusSubmodeEnumeration.LOCAL_BUS);
    netexEntitiesIndex.getStopPlaceIndex().put(STOP_PLACE_ID, List.of(stopPlace));
    Quay quay = new Quay();
    netexEntitiesIndex.getQuayIndex().put(QUAY_ID, List.of(quay));
    netexEntitiesIndex.getStopPlaceIdByQuayIdIndex().put(QUAY_ID, STOP_PLACE_ID);

    JAXBValidationContext validationContext = createValidationContext(netexEntitiesIndex);
    SiteFrameStopPlaceRepository repository = new SiteFrameStopPlaceRepository(
      validationContext
    );

    assertEquals(
      new TransportModeAndSubMode(
        STOP_PLACE_TRANSPORT_MODE,
        new TransportSubMode(STOP_PLACE_TRANSPORT_SUBMODE.value())
      ),
      repository.getTransportModesForQuayId(new QuayId(QUAY_ID))
    );
  }

  @Test
  void testGetTransportModesForNonExistingQuayId() {
    NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();

    JAXBValidationContext validationContext = createValidationContext(netexEntitiesIndex);
    SiteFrameStopPlaceRepository repository = new SiteFrameStopPlaceRepository(
      validationContext
    );

    assertNull(repository.getTransportModesForQuayId(new QuayId(QUAY_ID)));
  }

  private static JAXBValidationContext createValidationContext(
    NetexEntitiesIndex netexEntitiesIndex
  ) {
    return new JAXBValidationContext(
      "REPORT_ID",
      netexEntitiesIndex,
      null,
      SiteFrameStopPlaceRepository::new,
      "codespace",
      "fileName",
      Map.of()
    );
  }
}

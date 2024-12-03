package org.entur.netex.validation.validator.jaxb;

import static org.entur.netex.validation.validator.jaxb.support.JAXBUtils.createJaxbElement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.entur.netex.index.impl.NetexEntitiesIndexImpl;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.id.IdVersion;
import org.entur.netex.validation.validator.model.TransportModeAndSubMode;
import org.entur.netex.validation.validator.model.TransportSubMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.BusSubmodeEnumeration;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.JourneyPatternRefStructure;
import org.rutebanken.netex.model.Line;
import org.rutebanken.netex.model.LineRefStructure;
import org.rutebanken.netex.model.RailSubmodeEnumeration;
import org.rutebanken.netex.model.Route;
import org.rutebanken.netex.model.RouteRefStructure;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.TransportSubmodeStructure;

class JAXBValidationContextTest {

  public static final String FILE_NAME = "netex.xml";
  public static final String OBJECT_ID = "ENT:LINE:1";
  private static final int LINE_NUMBER = 1;
  private static final int COLUMN_NUMBER = 2;
  private JourneyPattern journeyPattern;
  private ServiceJourney serviceJourney;
  private NetexEntitiesIndexImpl netexEntitiesIndex;

  @BeforeEach
  void setUp() {
    Line line = new Line()
      .withId("TST:Line:1")
      .withTransportMode(AllVehicleModesOfTransportEnumeration.BUS)
      .withTransportSubmode(
        new TransportSubmodeStructure()
          .withBusSubmode(BusSubmodeEnumeration.LOCAL_BUS)
      );

    Route route = new Route()
      .withId("TST:Route:1")
      .withLineRef(
        createJaxbElement(new LineRefStructure().withRef(line.getId()))
      );

    journeyPattern =
      new JourneyPattern()
        .withId("TST:JourneyPattern:1")
        .withRouteRef(new RouteRefStructure().withRef(route.getId()));

    serviceJourney =
      new ServiceJourney()
        .withId("TST:ServiceJourney:1")
        .withJourneyPatternRef(
          createJaxbElement(
            new JourneyPatternRefStructure().withRef(journeyPattern.getId())
          )
        );

    netexEntitiesIndex = new NetexEntitiesIndexImpl();
    netexEntitiesIndex.getLineIndex().put(line.getId(), line);
    netexEntitiesIndex.getRouteIndex().put(route.getId(), route);
    netexEntitiesIndex
      .getJourneyPatternIndex()
      .put(journeyPattern.getId(), journeyPattern);
    netexEntitiesIndex
      .getServiceJourneyIndex()
      .put(serviceJourney.getId(), serviceJourney);
  }

  @Test
  void testDataLocationMissingId() {
    JAXBValidationContext context = new JAXBValidationContext(
      null,
      null,
      null,
      null,
      null,
      FILE_NAME,
      Map.of()
    );

    DataLocation dataLocation = context.dataLocation(OBJECT_ID);
    assertNotNull(dataLocation);
    assertEquals(FILE_NAME, dataLocation.getFileName());
    assertEquals(OBJECT_ID, dataLocation.getObjectId());
  }

  @Test
  void testDataLocationExistingId() {
    IdVersion idVersion = new IdVersion(
      OBJECT_ID,
      "1",
      "Line",
      null,
      FILE_NAME,
      LINE_NUMBER,
      COLUMN_NUMBER
    );
    Map<String, IdVersion> localIdsMap = Map.of(OBJECT_ID, idVersion);
    JAXBValidationContext context = new JAXBValidationContext(
      null,
      null,
      null,
      null,
      null,
      FILE_NAME,
      localIdsMap
    );

    DataLocation dataLocation = context.dataLocation(OBJECT_ID);
    assertNotNull(dataLocation);
    assertEquals(FILE_NAME, dataLocation.getFileName());
    assertEquals(OBJECT_ID, dataLocation.getObjectId());
    assertEquals(LINE_NUMBER, dataLocation.getLineNumber());
    assertEquals(COLUMN_NUMBER, dataLocation.getColumNumber());
  }

  @Test
  void transportModeAndSubModeDefinedOnLineFromJourneyPattern() {
    JAXBValidationContext context = new JAXBValidationContext(
      null,
      netexEntitiesIndex,
      null,
      null,
      null,
      FILE_NAME,
      Map.of()
    );

    TransportModeAndSubMode transportModeAndSubMode =
      context.transportModeAndSubMode(journeyPattern);
    assertNotNull(transportModeAndSubMode);
    assertEquals(
      AllVehicleModesOfTransportEnumeration.BUS,
      transportModeAndSubMode.mode()
    );
    assertEquals(
      new TransportSubMode(BusSubmodeEnumeration.LOCAL_BUS.value()),
      transportModeAndSubMode.subMode()
    );
  }

  @Test
  void transportModeAndSubModeDefinedOnLineFromServiceJourney() {
    JAXBValidationContext context = new JAXBValidationContext(
      null,
      netexEntitiesIndex,
      null,
      null,
      null,
      FILE_NAME,
      Map.of()
    );

    TransportModeAndSubMode transportModeAndSubMode =
      context.transportModeAndSubMode(serviceJourney);
    assertNotNull(transportModeAndSubMode);
    assertEquals(
      AllVehicleModesOfTransportEnumeration.BUS,
      transportModeAndSubMode.mode()
    );
    assertEquals(
      new TransportSubMode(BusSubmodeEnumeration.LOCAL_BUS.value()),
      transportModeAndSubMode.subMode()
    );
  }

  @Test
  void transportModeAndSubModeDefinedOnServiceJourneyFromServiceJourney() {
    serviceJourney.withTransportMode(
      AllVehicleModesOfTransportEnumeration.RAIL
    );
    serviceJourney.withTransportSubmode(
      new TransportSubmodeStructure()
        .withRailSubmode(RailSubmodeEnumeration.LOCAL)
    );

    JAXBValidationContext context = new JAXBValidationContext(
      null,
      netexEntitiesIndex,
      null,
      null,
      null,
      FILE_NAME,
      Map.of()
    );

    TransportModeAndSubMode transportModeAndSubMode =
      context.transportModeAndSubMode(serviceJourney);
    assertNotNull(transportModeAndSubMode);
    assertEquals(
      AllVehicleModesOfTransportEnumeration.RAIL,
      transportModeAndSubMode.mode()
    );
    assertEquals(
      new TransportSubMode(RailSubmodeEnumeration.LOCAL.value()),
      transportModeAndSubMode.subMode()
    );
  }
}

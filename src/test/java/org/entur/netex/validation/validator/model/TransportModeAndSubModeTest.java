package org.entur.netex.validation.validator.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.BusSubmodeEnumeration;
import org.rutebanken.netex.model.RailSubmodeEnumeration;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TransportSubmodeStructure;

class TransportModeAndSubModeTest {

  @Test
  void testMissingTransportModeAndSubModeFromStopPlace() {
    StopPlace stopPlace = new StopPlace();
    TransportModeAndSubMode transportModeAndSubMode =
      TransportModeAndSubMode.of(stopPlace);
    assertNull(transportModeAndSubMode);
  }

  @Test
  void testMissingTransportSubModeFromStopPlace() {
    StopPlace stopPlace = new StopPlace();
    stopPlace.withTransportMode(AllVehicleModesOfTransportEnumeration.RAIL);
    TransportModeAndSubMode transportModeAndSubMode =
      TransportModeAndSubMode.of(stopPlace);
    assertNotNull(transportModeAndSubMode);
    assertEquals(TransportSubMode.MISSING, transportModeAndSubMode.subMode());
  }

  @Test
  void testCreateTransportModeAndSubModeFromStopPlace() {
    StopPlace stopPlace = new StopPlace();
    stopPlace
      .withTransportMode(AllVehicleModesOfTransportEnumeration.RAIL)
      .withRailSubmode(RailSubmodeEnumeration.LOCAL);
    TransportModeAndSubMode transportModeAndSubMode =
      TransportModeAndSubMode.of(stopPlace);
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

  @Test
  void testCreateTransportModeAndSubModeFromStructure() {
    TransportSubmodeStructure submode = new TransportSubmodeStructure()
      .withBusSubmode(BusSubmodeEnumeration.LOCAL_BUS);
    TransportModeAndSubMode transportModeAndSubMode =
      TransportModeAndSubMode.of(
        AllVehicleModesOfTransportEnumeration.BUS,
        submode
      );
    assertNotNull(transportModeAndSubMode);
  }

  @Test
  void testMissingTransportModeAndSubModeFromStructure() {
    TransportSubmodeStructure submode = new TransportSubmodeStructure()
      .withBusSubmode(BusSubmodeEnumeration.LOCAL_BUS);
    assertNull(TransportModeAndSubMode.of(null, submode));
  }

  @Test
  void testUnknownTransportSubModeForMode() {
    TransportSubmodeStructure submode = new TransportSubmodeStructure()
      .withBusSubmode(BusSubmodeEnumeration.LOCAL_BUS);
    TransportModeAndSubMode transportModeAndSubMode =
      TransportModeAndSubMode.of(
        AllVehicleModesOfTransportEnumeration.RAIL,
        submode
      );
    assertNotNull(transportModeAndSubMode);
    assertEquals(TransportSubMode.MISSING, transportModeAndSubMode.subMode());
  }
}

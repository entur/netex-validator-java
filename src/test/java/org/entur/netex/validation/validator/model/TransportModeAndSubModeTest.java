package org.entur.netex.validation.validator.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.RailSubmodeEnumeration;
import org.rutebanken.netex.model.StopPlace;

class TransportModeAndSubModeTest {

  @Test
  void testMissingTransportModeAndSubModeFromStopPlace() {
    StopPlace stopPlace = new StopPlace();
    TransportModeAndSubMode transportModeAndSubMode =
      TransportModeAndSubMode.of(stopPlace);
    assertNull(transportModeAndSubMode);
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
}

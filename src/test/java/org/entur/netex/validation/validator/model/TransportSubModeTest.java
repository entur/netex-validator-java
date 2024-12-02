package org.entur.netex.validation.validator.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.RailSubmodeEnumeration;
import org.rutebanken.netex.model.SelfDriveSubmodeEnumeration;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TransportSubmodeStructure;

class TransportSubModeTest {

  @Test
  void testMissingStopPlace() {
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(null);
    assertFalse(transportSubMode.isPresent());
  }

  @Test
  void testMissingTransportModeAndSubModeOnStopPlace() {
    StopPlace stopPlace = new StopPlace();
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(
      stopPlace
    );
    assertFalse(transportSubMode.isPresent());
  }

  @Test
  void testMissingTransportSubModeOnStopPlace() {
    StopPlace stopPlace = new StopPlace();
    stopPlace.withTransportMode(AllVehicleModesOfTransportEnumeration.RAIL);
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(
      stopPlace
    );
    assertFalse(transportSubMode.isPresent());
  }

  @Test
  void testTransportModeAndSubModeOnStopPlace() {
    StopPlace stopPlace = new StopPlace();
    stopPlace
      .withTransportMode(AllVehicleModesOfTransportEnumeration.RAIL)
      .withRailSubmode(RailSubmodeEnumeration.LOCAL);
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(
      stopPlace
    );
    assertTrue(transportSubMode.isPresent());
    assertEquals(
      RailSubmodeEnumeration.LOCAL.value(),
      transportSubMode.get().name()
    );
  }

  @Test
  void testMissingTransportModeAndSubModeFromStructure() {
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(
      null,
      null
    );
    assertFalse(transportSubMode.isPresent());
  }

  @Test
  void testMissingTransportSubModeFromStructure() {
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(
      AllVehicleModesOfTransportEnumeration.RAIL,
      null
    );
    assertFalse(transportSubMode.isPresent());
  }

  @Test
  void testUnknownTransportSubMode() {
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(
      AllVehicleModesOfTransportEnumeration.RAIL,
      new TransportSubmodeStructure()
        .withSelfDriveSubmode(SelfDriveSubmodeEnumeration.ALL_VEHICLES)
    );
    assertFalse(transportSubMode.isPresent());
  }

  @Test
  void testTransportModeAndSubModeFromStructure() {
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(
      AllVehicleModesOfTransportEnumeration.RAIL,
      new TransportSubmodeStructure()
        .withRailSubmode(RailSubmodeEnumeration.LOCAL)
    );
    assertTrue(transportSubMode.isPresent());
    assertEquals(
      RailSubmodeEnumeration.LOCAL.value(),
      transportSubMode.get().name()
    );
  }
}

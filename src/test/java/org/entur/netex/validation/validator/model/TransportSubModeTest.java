package org.entur.netex.validation.validator.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.entur.netex2.model.AllPublicTransportModesEnumeration;
import org.entur.netex2.model.RailSubmodeEnumeration;
import org.entur.netex2.model.SelfDriveSubmodeEnumeration;
import org.entur.netex2.model.StopPlace;
import org.entur.netex2.model.TransportSubmodeStructure;
import org.junit.jupiter.api.Test;

class TransportSubModeTest {

  @Test
  void testMissingStopPlace() {
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(null);
    assertFalse(transportSubMode.isPresent());
  }

  @Test
  void testMissingTransportModeAndSubModeOnStopPlace() {
    StopPlace stopPlace = new StopPlace();
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(stopPlace);
    assertFalse(transportSubMode.isPresent());
  }

  @Test
  void testMissingTransportSubModeOnStopPlace() {
    StopPlace stopPlace = new StopPlace();
    stopPlace.withTransportMode(AllPublicTransportModesEnumeration.RAIL);
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(stopPlace);
    assertFalse(transportSubMode.isPresent());
  }

  @Test
  void testTransportModeAndSubModeOnStopPlace() {
    StopPlace stopPlace = new StopPlace();
    stopPlace
      .withTransportMode(AllPublicTransportModesEnumeration.RAIL)
      .withRailSubmode(RailSubmodeEnumeration.LOCAL);
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(stopPlace);
    assertTrue(transportSubMode.isPresent());
    assertEquals(RailSubmodeEnumeration.LOCAL.value(), transportSubMode.get().name());
  }

  @Test
  void testMissingTransportModeAndSubModeFromStructure() {
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(null, null);
    assertFalse(transportSubMode.isPresent());
  }

  @Test
  void testMissingTransportSubModeFromStructure() {
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(
      AllPublicTransportModesEnumeration.RAIL,
      null
    );
    assertFalse(transportSubMode.isPresent());
  }

  @Test
  void testUnknownTransportSubMode() {
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(
      AllPublicTransportModesEnumeration.RAIL,
      new TransportSubmodeStructure()
        .withSelfDriveSubmode(SelfDriveSubmodeEnumeration.ALL_VEHICLES)
    );
    assertFalse(transportSubMode.isPresent());
  }

  @Test
  void testTransportModeAndSubModeFromStructure() {
    Optional<TransportSubMode> transportSubMode = TransportSubMode.of(
      AllPublicTransportModesEnumeration.RAIL,
      new TransportSubmodeStructure().withRailSubmode(RailSubmodeEnumeration.LOCAL)
    );
    assertTrue(transportSubMode.isPresent());
    assertEquals(RailSubmodeEnumeration.LOCAL.value(), transportSubMode.get().name());
  }
}

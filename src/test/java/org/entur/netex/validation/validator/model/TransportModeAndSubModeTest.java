package org.entur.netex.validation.validator.model;

import static org.junit.jupiter.api.Assertions.*;

import org.entur.netex2.model.AllPublicTransportModesEnumeration;
import org.entur.netex2.model.BusSubmodeEnumeration;
import org.entur.netex2.model.RailSubmodeEnumeration;
import org.entur.netex2.model.StopPlace;
import org.entur.netex2.model.TransportSubmodeStructure;
import org.junit.jupiter.api.Test;

class TransportModeAndSubModeTest {

  @Test
  void testMissingTransportModeAndSubModeFromStopPlace() {
    StopPlace stopPlace = new StopPlace();
    TransportModeAndSubMode transportModeAndSubMode = TransportModeAndSubMode.of(
      stopPlace
    );
    assertNull(transportModeAndSubMode);
  }

  @Test
  void testMissingTransportSubModeFromStopPlace() {
    StopPlace stopPlace = new StopPlace();
    stopPlace.withTransportMode(AllPublicTransportModesEnumeration.RAIL);
    TransportModeAndSubMode transportModeAndSubMode = TransportModeAndSubMode.of(
      stopPlace
    );
    assertNotNull(transportModeAndSubMode);
    assertEquals(TransportSubMode.MISSING, transportModeAndSubMode.subMode());
  }

  @Test
  void testCreateTransportModeAndSubModeFromStopPlace() {
    StopPlace stopPlace = new StopPlace();
    stopPlace
      .withTransportMode(AllPublicTransportModesEnumeration.RAIL)
      .withRailSubmode(RailSubmodeEnumeration.LOCAL);
    TransportModeAndSubMode transportModeAndSubMode = TransportModeAndSubMode.of(
      stopPlace
    );
    assertNotNull(transportModeAndSubMode);
    assertEquals(AllPublicTransportModesEnumeration.RAIL, transportModeAndSubMode.mode());
    assertEquals(
      new TransportSubMode(RailSubmodeEnumeration.LOCAL.value()),
      transportModeAndSubMode.subMode()
    );
  }

  @Test
  void testCreateTransportModeAndSubModeFromStructure() {
    TransportSubmodeStructure submode = new TransportSubmodeStructure()
      .withBusSubmode(BusSubmodeEnumeration.LOCAL_BUS);
    TransportModeAndSubMode transportModeAndSubMode = TransportModeAndSubMode.of(
      AllPublicTransportModesEnumeration.BUS,
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
    TransportModeAndSubMode transportModeAndSubMode = TransportModeAndSubMode.of(
      AllPublicTransportModesEnumeration.RAIL,
      submode
    );
    assertNotNull(transportModeAndSubMode);
    assertEquals(TransportSubMode.MISSING, transportModeAndSubMode.subMode());
  }
}

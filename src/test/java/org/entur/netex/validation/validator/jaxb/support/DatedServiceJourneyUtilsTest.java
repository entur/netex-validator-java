package org.entur.netex.validation.validator.jaxb.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.entur.netex2.model.DatedServiceJourney;
import org.entur.netex2.model.ObjectFactory;
import org.entur.netex2.model.ReplacedJourneys_RelStructure;
import org.entur.netex2.model.VehicleJourneyRefStructure;
import org.junit.jupiter.api.Test;

class DatedServiceJourneyUtilsTest {

  private static final String ORIGINAL_DSJ_ID = "TST:DatedServiceJourney:1";

  @Test
  void noReplacedJourneys() {
    assertNull(
      DatedServiceJourneyUtils.originalDatedServiceJourneyRef(new DatedServiceJourney())
    );
  }

  @Test
  void emptyReplacedJourneys() {
    DatedServiceJourney datedServiceJourney = new DatedServiceJourney()
      .withReplacedJourneys(new ReplacedJourneys_RelStructure());
    assertNull(
      DatedServiceJourneyUtils.originalDatedServiceJourneyRef(datedServiceJourney)
    );
  }

  @Test
  void replacedJourney() {
    ObjectFactory objectFactory = new ObjectFactory();
    DatedServiceJourney datedServiceJourney = new DatedServiceJourney()
      .withReplacedJourneys(
        new ReplacedJourneys_RelStructure()
          .withDatedVehicleJourneyRefOrNormalDatedVehicleJourneyRef(
            objectFactory.createDatedVehicleJourneyRef(
              new VehicleJourneyRefStructure().withRef(ORIGINAL_DSJ_ID)
            )
          )
      );
    assertEquals(
      ORIGINAL_DSJ_ID,
      DatedServiceJourneyUtils.originalDatedServiceJourneyRef(datedServiceJourney)
    );
  }
}

package org.entur.netex.validation.validator.jaxb.support;

import jakarta.xml.bind.JAXBElement;
import javax.annotation.Nullable;
import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.VehicleJourneyRefStructure;

public class DatedServiceJourneyUtils {

  private DatedServiceJourneyUtils() {}

  /**
   * Return the NeTEx id of the original DatedServiceJourney of a given DatedServiceJourney.
   */
  @Nullable
  public static String originalDatedServiceJourneyRef(
    DatedServiceJourney datedServiceJourney
  ) {
    return datedServiceJourney
      .getReplacedJourneys()
      .getDatedVehicleJourneyRefOrNormalDatedVehicleJourneyRef()
      .stream()
      .map(JAXBElement::getValue)
      .map(VehicleJourneyRefStructure::getRef)
      .findFirst()
      .orElse(null);
  }
}

package org.entur.netex.validation.validator.jaxb.support;

import jakarta.annotation.Nullable;
import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.DatedServiceJourneyRefStructure;
import org.rutebanken.netex.model.VersionOfObjectRefStructure;

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
      .getJourneyRef()
      .stream()
      .map(JAXBElement::getValue)
      .filter(DatedServiceJourneyRefStructure.class::isInstance)
      .map(DatedServiceJourneyRefStructure.class::cast)
      .map(VersionOfObjectRefStructure::getRef)
      .findFirst()
      .orElse(null);
  }
}

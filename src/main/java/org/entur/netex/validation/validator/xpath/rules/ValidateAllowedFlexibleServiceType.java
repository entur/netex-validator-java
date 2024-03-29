package org.entur.netex.validation.validator.xpath.rules;

import org.rutebanken.netex.model.FlexibleServiceEnumeration;

/**
 * Validate the flexible service type against the Nordic NeTEx profile.
 */
public class ValidateAllowedFlexibleServiceType extends ValidateNotExist {

  private static final String VALID_FLEXIBLE_SERVICE_TYPES =
    "'" +
    String.join(
      "','",
      FlexibleServiceEnumeration.DYNAMIC_PASSING_TIMES.value(),
      FlexibleServiceEnumeration.FIXED_HEADWAY_FREQUENCY.value(),
      FlexibleServiceEnumeration.FIXED_PASSING_TIMES.value(),
      FlexibleServiceEnumeration.NOT_FLEXIBLE.value()
    ) +
    "'";

  public static final String MESSAGE =
    "Illegal FlexibleServiceType on ServiceJourney";

  public ValidateAllowedFlexibleServiceType() {
    super(
      "vehicleJourneys/ServiceJourney/FlexibleServiceProperties/FlexibleServiceType[not(. = (" +
      VALID_FLEXIBLE_SERVICE_TYPES +
      "))]",
      MESSAGE,
      "FLEXIBLE_LINE_9"
    );
  }
}

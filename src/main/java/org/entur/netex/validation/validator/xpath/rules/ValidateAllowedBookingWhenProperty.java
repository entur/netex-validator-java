package org.entur.netex.validation.validator.xpath.rules;

import org.rutebanken.netex.model.PurchaseWhenEnumeration;

/**
 * Validate the "booking when" properties against the Nordic NeTEx profile.
 */
public class ValidateAllowedBookingWhenProperty extends ValidateNotExist {

  private static final String VALID_BOOKING_WHEN_PROPERTIES =
    "'" +
    String.join(
      "','",
      PurchaseWhenEnumeration.DAY_OF_TRAVEL_ONLY.value(),
      PurchaseWhenEnumeration.UNTIL_PREVIOUS_DAY.value(),
      PurchaseWhenEnumeration.ADVANCE_AND_DAY_OF_TRAVEL.value()
    ) +
    "'";

  private static final String MESSAGE = "Illegal value for BookWhen";

  public ValidateAllowedBookingWhenProperty(String context) {
    super(
      context + "/BookWhen[not(. = (" + VALID_BOOKING_WHEN_PROPERTIES + "))]",
      MESSAGE,
      "BOOKING_3"
    );
  }
}

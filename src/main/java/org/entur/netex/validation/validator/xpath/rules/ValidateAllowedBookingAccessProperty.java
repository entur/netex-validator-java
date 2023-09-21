package org.entur.netex.validation.validator.xpath.rules;

import org.rutebanken.netex.model.BookingAccessEnumeration;

/**
 * Validate the booking access properties against the Nordic NeTEx profile.
 */
public class ValidateAllowedBookingAccessProperty extends ValidateNotExist {

  private static final String VALID_BOOKING_ACCESS_PROPERTIES =
    "'" +
    String.join(
      "','",
      BookingAccessEnumeration.PUBLIC.value(),
      BookingAccessEnumeration.AUTHORISED_PUBLIC.value(),
      BookingAccessEnumeration.STAFF.value()
    ) +
    "'";

  private static final String MESSAGE = "Illegal value for BookingAccess";

  public ValidateAllowedBookingAccessProperty(String context) {
    super(
      context +
      "/BookingAccess[not(. = (" +
      VALID_BOOKING_ACCESS_PROPERTIES +
      "))]",
      MESSAGE,
      "BOOKING_1"
    );
  }
}

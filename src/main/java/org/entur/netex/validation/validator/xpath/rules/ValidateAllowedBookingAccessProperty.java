package org.entur.netex.validation.validator.xpath.rules;

import org.entur.netex.validation.validator.Severity;
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

  public ValidateAllowedBookingAccessProperty(String context) {
    super(
      context + "/BookingAccess[not(. = (" + VALID_BOOKING_ACCESS_PROPERTIES + "))]",
      "BOOKING_1",
      "Booking illegal BookingAccess",
      "Illegal value for BookingAccess",
      Severity.ERROR
    );
  }
}

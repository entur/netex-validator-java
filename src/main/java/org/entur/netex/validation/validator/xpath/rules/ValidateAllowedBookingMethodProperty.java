package org.entur.netex.validation.validator.xpath.rules;

import org.entur.netex.validation.validator.Severity;
import org.rutebanken.netex.model.BookingMethodEnumeration;

/**
 * Validate the booking method properties against the Nordic NeTEx profile.
 */
public class ValidateAllowedBookingMethodProperty extends ValidateNotExist {

  private static final String VALID_BOOKING_METHOD_PROPERTIES =
    "'" +
    String.join(
      "','",
      BookingMethodEnumeration.CALL_DRIVER.value(),
      BookingMethodEnumeration.CALL_OFFICE.value(),
      BookingMethodEnumeration.ONLINE.value(),
      BookingMethodEnumeration.OTHER.value(),
      BookingMethodEnumeration.PHONE_AT_STOP.value(),
      BookingMethodEnumeration.TEXT.value()
    ) +
    "'";

  public ValidateAllowedBookingMethodProperty(String context) {
    super(
      context +
      "/BookingMethods[tokenize(.,' ')[not(. = (" +
      VALID_BOOKING_METHOD_PROPERTIES +
      "))]]",
      "BOOKING_2",
      "Booking illegal BookingMethod",
      "Illegal value for BookingMethod",
      Severity.ERROR
    );
  }
}

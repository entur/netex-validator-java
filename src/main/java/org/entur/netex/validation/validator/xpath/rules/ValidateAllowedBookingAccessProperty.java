package org.entur.netex.validation.validator.xpath.rules;

import org.entur.netex.validation.validator.ValidationReportEntrySeverity;
import org.rutebanken.netex.model.BookingMethodEnumeration;

/**
 * Validate the booking access properties against the Nordic NeTEx profile.
 */
public class ValidateAllowedBookingAccessProperty extends ValidateNotExist {

    private static final String VALID_BOOKING_ACCESS_PROPERTIES = "'" + String.join("','",
            BookingMethodEnumeration.CALL_DRIVER.value(),
            BookingMethodEnumeration.CALL_OFFICE.value(),
            BookingMethodEnumeration.ONLINE.value(),
            BookingMethodEnumeration.OTHER.value(),
            BookingMethodEnumeration.PHONE_AT_STOP.value(),
            BookingMethodEnumeration.TEXT.value())
            + "'";

    private static final String MESSAGE = "Illegal value for BookingAccess";

    public ValidateAllowedBookingAccessProperty(String context) {
        super(context + "/BookingAccess[not(. = (" + VALID_BOOKING_ACCESS_PROPERTIES + "))]", MESSAGE, "BOOKING_1", ValidationReportEntrySeverity.ERROR);
    }
}

package org.entur.netex.validation.validator.xpath.rules;

import org.entur.netex.validation.validator.ValidationReportEntrySeverity;

/**
 * Validate the transport sub-mode against the Nordic NeTEx profile.
 */
public class ValidatedAllowedTransportSubMode extends ValidateNotExist {

    private static final String VALID_TRANSPORT_SUBMODES = "'" + String.join("','",
            "internationalCoach",
            "nationalCoach",
            "touristCoach",
            "airportLinkBus",
            "localTram",
            "cityTram",
            "international",
            "metro",
            "domesticFlight",
            "highSpeedPassengerService",
            "telecabin",
            "funicular",
            "expressBus",
            "interregionalRail",
            "helicopterService",
            "highSpeedVehicleService",
            "localBus",
            "local",
            "internationalFlight",
            "internationalCarFerry",
            "nightBus",
            "longDistance",
            "internationalPassengerFerry",
            "railReplacementBus",
            "nightRail",
            "airportLinkRail",
            "localCarFerry",
            "regionalBus",
            "regionalRail",
            "localPassengerFerry",
            "schoolBus",
            "touristRailway",
            "nationalCarFerry",
            "shuttleBus",
            "sightseeingService",
            "sightseeingBus",
            "unknown")
            + "'";

    private static final String MESSAGE = "Illegal TransportSubMode";

    public ValidatedAllowedTransportSubMode() {
        super("lines/*[self::Line or self::FlexibleLine]/TransportSubmode[not(. = (" + VALID_TRANSPORT_SUBMODES + "))]", MESSAGE, "TRANSPORT_SUB_MODE", ValidationReportEntrySeverity.ERROR);
    }
}

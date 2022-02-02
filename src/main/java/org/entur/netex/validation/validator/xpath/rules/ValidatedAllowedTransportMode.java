package org.entur.netex.validation.validator.xpath.rules;

/**
 * Validate the transport mode against the Nordic NeTEx profile.
 */
public class ValidatedAllowedTransportMode extends ValidateNotExist {

    private static final String VALID_TRANSPORT_MODES = "'" + String.join("','",
            "coach",
            "bus",
            "tram",
            "rail",
            "metro",
            "air",
            "water",
            "cableway",
            "funicular",
            "unknown")
            + "'";

    private static final String MESSAGE = "Illegal TransportMode";

    public ValidatedAllowedTransportMode() {
        super("lines/*[self::Line or self::FlexibleLine]/TransportMode[not(. = (" + VALID_TRANSPORT_MODES + "))]", MESSAGE, "TRANSPORT_MODE");
    }
}

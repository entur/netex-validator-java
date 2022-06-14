package org.entur.netex.validation.validator.xpath.rules;

/**
 * Validate the transport mode against the Nordic NeTEx profile.
 */
public class ValidatedAllowedTransportMode extends ValidateNotExist {

    public static final String DEFAULT_VALID_TRANSPORT_MODES = "'" + String.join("','",
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

    public ValidatedAllowedTransportMode(String validTransportModes) {
        super("lines/*[self::Line or self::FlexibleLine]/TransportMode[not(. = (" + validTransportModes + "))]", MESSAGE, "TRANSPORT_MODE");
    }

}

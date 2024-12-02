package org.entur.netex.validation.validator.xpath.rules;

/**
 * Validate the transport mode against the Nordic NeTEx profile.
 */
public class ValidateAllowedTransportMode extends ValidateNotExist {

  public static final String DEFAULT_VALID_TRANSPORT_MODES =
    "'" +
    String.join(
      "','",
      "coach",
      "bus",
      "tram",
      "rail",
      "metro",
      "air",
      "taxi",
      "water",
      "cableway",
      "funicular",
      "unknown"
    ) +
    "'";

  private static final String MESSAGE = "Illegal TransportMode";

  public ValidateAllowedTransportMode() {
    this(DEFAULT_VALID_TRANSPORT_MODES);
  }

  public ValidateAllowedTransportMode(String validTransportModes) {
    super(
      "lines/*[self::Line or self::FlexibleLine]/TransportMode[not(. = (" +
      validTransportModes +
      "))]",
      MESSAGE,
      "TRANSPORT_MODE"
    );
  }
}

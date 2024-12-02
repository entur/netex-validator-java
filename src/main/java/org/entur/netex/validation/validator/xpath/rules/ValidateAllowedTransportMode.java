package org.entur.netex.validation.validator.xpath.rules;

import org.entur.netex.validation.validator.Severity;

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

  /**
   * Validate that the transport mode set on the given contex path (either Line, FlexibleLine or ServiceJourney) is valid
   * for the default set of transport modes.
   */
  public ValidateAllowedTransportMode(
    String contexPath,
    String code,
    String message
  ) {
    this(contexPath, code, message, DEFAULT_VALID_TRANSPORT_MODES);
  }

  /**
   * Validate that the transport mode set on the container entity (either Line, FlexibleLine or ServiceJourney) is valid
   * for the given set of transport modes.
   */
  public ValidateAllowedTransportMode(
    String contexPath,
    String code,
    String message,
    String validTransportModes
  ) {
    super(
      contexPath + "/TransportMode[not(. = (" + validTransportModes + "))]",
      message,
      code,
      Severity.ERROR
    );
  }
}

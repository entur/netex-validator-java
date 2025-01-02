package org.entur.netex.validation.validator.xpath.rules;

import org.entur.netex.validation.validator.Severity;

public class ValidateAllowedTransportModeOnLine
  extends ValidateAllowedTransportMode {

  public ValidateAllowedTransportModeOnLine() {
    super(
      "lines/*[self::Line or self::FlexibleLine]",
      "TRANSPORT_MODE_ON_LINE",
      "Line Illegal TransportMode",
      "Illegal TransportMode on Line: %s",
      Severity.ERROR
    );
  }
}

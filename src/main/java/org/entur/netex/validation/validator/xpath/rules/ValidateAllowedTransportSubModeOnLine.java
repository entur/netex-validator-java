package org.entur.netex.validation.validator.xpath.rules;

import org.entur.netex.validation.validator.Severity;

public class ValidateAllowedTransportSubModeOnLine
  extends ValidateAllowedTransportSubMode {

  public ValidateAllowedTransportSubModeOnLine() {
    super(
      "lines/*[self::Line or self::FlexibleLine]",
      "TRANSPORT_SUB_MODE_ON_LINE",
      "Line Illegal TransportSubMode",
      "Illegal TransportSubMode on Line: %s",
      Severity.ERROR
    );
  }
}

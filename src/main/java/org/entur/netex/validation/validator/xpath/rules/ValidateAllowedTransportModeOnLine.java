package org.entur.netex.validation.validator.xpath.rules;

public class ValidateAllowedTransportModeOnLine
  extends ValidateAllowedTransportMode {

  private static final String CODE = "TRANSPORT_MODE_ON_LINE";
  private static final String MESSAGE = "Illegal TransportMode on Line";

  public ValidateAllowedTransportModeOnLine() {
    super("lines/*[self::Line or self::FlexibleLine]", CODE, MESSAGE);
  }
}

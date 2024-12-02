package org.entur.netex.validation.validator.xpath.rules;

public class ValidateAllowedTransportSubModeOnLine
  extends ValidateAllowedTransportSubMode {

  private static final String CODE = "TRANSPORT_SUB_MODE_ON_LINE";
  private static final String MESSAGE = "Illegal TransportSubMode on Line";

  public ValidateAllowedTransportSubModeOnLine() {
    super("lines/*[self::Line or self::FlexibleLine]", CODE, MESSAGE);
  }
}

package org.entur.netex.validation.validator.xpath.rules;

import org.entur.netex.validation.validator.Severity;

public class ValidateAllowedTransportSubModeOnServiceJourney
  extends ValidateAllowedTransportSubMode {

  private static final String CODE = "TRANSPORT_SUB_MODE_ON_SERVICE_JOURNEY";
  private static final String MESSAGE =
    "Illegal TransportSubMode on ServiceJourney";

  public ValidateAllowedTransportSubModeOnServiceJourney() {
    super("vehicleJourneys/ServiceJourney", CODE, MESSAGE, Severity.WARNING);
  }
}

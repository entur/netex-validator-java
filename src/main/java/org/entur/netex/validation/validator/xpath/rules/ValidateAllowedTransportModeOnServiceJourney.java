package org.entur.netex.validation.validator.xpath.rules;

public class ValidateAllowedTransportModeOnServiceJourney
  extends ValidateAllowedTransportMode {

  private static final String CODE = "TRANSPORT_MODE_ON_SERVICE_JOURNEY";
  private static final String MESSAGE =
    "Illegal TransportMode on ServiceJourney";

  public ValidateAllowedTransportModeOnServiceJourney() {
    super("vehicleJourneys/ServiceJourney", CODE, MESSAGE);
  }
}

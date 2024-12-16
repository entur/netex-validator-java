package org.entur.netex.validation.validator.xpath.rules;

import org.entur.netex.validation.validator.Severity;

public class ValidateAllowedTransportSubModeOnServiceJourney
  extends ValidateAllowedTransportSubMode {

  public ValidateAllowedTransportSubModeOnServiceJourney() {
    super(
      "vehicleJourneys/ServiceJourney",
      "TRANSPORT_SUB_MODE_ON_SERVICE_JOURNEY",
      "Service Journey Illegal TransportSubMode",
      "Illegal TransportSubMode on ServiceJourney: %s",
      Severity.WARNING
    );
  }
}

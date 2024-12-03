package org.entur.netex.validation.validator.xpath.rules;

import org.entur.netex.validation.validator.Severity;

public class ValidateAllowedTransportModeOnServiceJourney
  extends ValidateAllowedTransportMode {

  public ValidateAllowedTransportModeOnServiceJourney() {
    super(
      "vehicleJourneys/ServiceJourney",
      "TRANSPORT_MODE_ON_SERVICE_JOURNEY",
      "Service Journey Illegal TransportMode",
      "Illegal TransportMode on ServiceJourney: %s",
      Severity.WARNING
    );
  }
}

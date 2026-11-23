package org.entur.netex.validation.validator.xpath.rules;

import static org.rutebanken.netex.model.BusSubmodeEnumeration.SCHOOL_BUS;

import org.entur.netex.validation.validator.Severity;
import org.rutebanken.netex.model.PublicationEnumeration;

/**
 * Validate that a ServiceJourney with publication="restricted" is a school bus service.
 * The transport submode is read on the ServiceJourney if it overrides it, otherwise on the Line.
 */
public class ValidateRestrictedPublicationOnServiceJourney extends ValidateNotExist {

  public static final String CODE_SERVICE_JOURNEY_18 = "SERVICE_JOURNEY_18";

  private static final String LINE_TRANSPORT_SUBMODE =
    "//ServiceFrame/lines/*[self::Line or self::FlexibleLine]/TransportSubmode";

  public ValidateRestrictedPublicationOnServiceJourney() {
    super(
      "vehicleJourneys/ServiceJourney[@publication = '" +
      PublicationEnumeration.RESTRICTED.value() +
      "' and not((if (TransportSubmode) then TransportSubmode else " +
      LINE_TRANSPORT_SUBMODE +
      ")/BusSubmode = '" +
      SCHOOL_BUS.value() +
      "')]",
      CODE_SERVICE_JOURNEY_18,
      "ServiceJourney restricted publication on non-school bus",
      "Restricted publication is allowed only on ServiceJourneys with transport submode " +
      SCHOOL_BUS.value(),
      Severity.WARNING
    );
  }
}

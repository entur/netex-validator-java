package org.entur.netex.validation.validator.xpath.rules;

import static org.rutebanken.netex.model.AirSubmodeEnumeration.*;
import static org.rutebanken.netex.model.BusSubmodeEnumeration.*;
import static org.rutebanken.netex.model.CoachSubmodeEnumeration.*;
import static org.rutebanken.netex.model.FunicularSubmodeEnumeration.*;
import static org.rutebanken.netex.model.MetroSubmodeEnumeration.*;
import static org.rutebanken.netex.model.RailSubmodeEnumeration.*;
import static org.rutebanken.netex.model.TaxiSubmodeEnumeration.*;
import static org.rutebanken.netex.model.TelecabinSubmodeEnumeration.*;
import static org.rutebanken.netex.model.TramSubmodeEnumeration.*;
import static org.rutebanken.netex.model.WaterSubmodeEnumeration.*;

/**
 * Validate the transport sub-mode against the Nordic NeTEx profile.
 */
public class ValidateAllowedTransportSubMode extends ValidateNotExist {

  public static final String DEFAULT_VALID_TRANSPORT_SUBMODES =
    "'" +
    String.join(
      "','",
      // Coach
      INTERNATIONAL_COACH.value(),
      NATIONAL_COACH.value(),
      TOURIST_COACH.value(),
      // Tram
      LOCAL_TRAM.value(),
      CITY_TRAM.value(),
      // Bus
      AIRPORT_LINK_BUS.value(),
      EXPRESS_BUS.value(),
      LOCAL_BUS.value(),
      NIGHT_BUS.value(),
      RAIL_REPLACEMENT_BUS.value(),
      REGIONAL_BUS.value(),
      SCHOOL_BUS.value(),
      SIGHTSEEING_BUS.value(),
      SHUTTLE_BUS.value(),
      // Metro
      METRO.value(),
      // Water
      HIGH_SPEED_PASSENGER_SERVICE.value(),
      HIGH_SPEED_VEHICLE_SERVICE.value(),
      // Telecabin
      TELECABIN.value(),
      // Funicular
      FUNICULAR.value(),
      // Rail
      INTERNATIONAL.value(),
      INTERREGIONAL_RAIL.value(),
      LOCAL.value(),
      LONG_DISTANCE.value(),
      NIGHT_RAIL.value(),
      AIRPORT_LINK_RAIL.value(),
      REGIONAL_RAIL.value(),
      TOURIST_RAILWAY.value(),
      // Air
      HELICOPTER_SERVICE.value(),
      DOMESTIC_FLIGHT.value(),
      INTERNATIONAL_FLIGHT.value(),
      // Water
      INTERNATIONAL_CAR_FERRY.value(),
      INTERNATIONAL_PASSENGER_FERRY.value(),
      LOCAL_CAR_FERRY.value(),
      LOCAL_PASSENGER_FERRY.value(),
      NATIONAL_CAR_FERRY.value(),
      SIGHTSEEING_SERVICE.value(),
      // Taxi
      CHARTER_TAXI.value(),
      COMMUNAL_TAXI.value(),
      // All
      "unknown"
    ) +
    "'";

  private static final String MESSAGE = "Illegal TransportSubMode";

  public ValidateAllowedTransportSubMode() {
    this(DEFAULT_VALID_TRANSPORT_SUBMODES);
  }

  public ValidateAllowedTransportSubMode(String validTransportSubModes) {
    super(
      "lines/*[self::Line or self::FlexibleLine]/TransportSubmode[not(. = (" +
      validTransportSubModes +
      "))]",
      MESSAGE,
      "TRANSPORT_SUB_MODE"
    );
  }
}

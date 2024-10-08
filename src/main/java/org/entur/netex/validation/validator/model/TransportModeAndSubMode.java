package org.entur.netex.validation.validator.model;

import org.entur.netex.validation.exception.NetexValidationException;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.StopPlace;

/**
 * A pair of mode and sub-mode.
 */
public record TransportModeAndSubMode(
  AllVehicleModesOfTransportEnumeration mode,
  TransportSubMode subMode
) {
  public static TransportModeAndSubMode of(StopPlace stopPlace) {
    return new TransportModeAndSubMode(
      stopPlace.getTransportMode(),
      TransportSubMode.of(stopPlace).orElse(null)
    );
  }

  @Override
  public String toString() {
    return (
      (mode != null ? mode.value() : "") +
      (subMode != null ? "§" + subMode.name() : "")
    );
  }

  public static TransportModeAndSubMode fromString(
    String stopPlaceTransportModeAndSubMode
  ) {
    if (stopPlaceTransportModeAndSubMode != null) {
      String[] split = stopPlaceTransportModeAndSubMode.split("§");
      if (split.length == 1) {
        return new TransportModeAndSubMode(
          AllVehicleModesOfTransportEnumeration.fromValue(split[0]),
          null
        );
      } else if (split.length == 2) {
        return new TransportModeAndSubMode(
          AllVehicleModesOfTransportEnumeration.fromValue(split[0]),
          new TransportSubMode(split[1])
        );
      } else {
        throw new NetexValidationException(
          "Invalid stopPlaceTransportModeAndSubMode string: " +
          stopPlaceTransportModeAndSubMode
        );
      }
    }
    return null;
  }
}

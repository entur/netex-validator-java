package org.entur.netex.validation.validator.model;

import java.util.Optional;
import org.entur.netex.validation.exception.NetexValidationException;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TransportSubmodeStructure;

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

  public static TransportModeAndSubMode of(
    AllVehicleModesOfTransportEnumeration transportMode,
    TransportSubmodeStructure submodeStructure
  ) {
    if (transportMode == null) {
      return null;
    }
    return new TransportModeAndSubMode(
      transportMode,
      TransportSubMode.of(transportMode, submodeStructure).orElse(null)
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
    String submodeStructureTransportModeAndSubMode
  ) {
    if (submodeStructureTransportModeAndSubMode != null) {
      String[] split = submodeStructureTransportModeAndSubMode.split("§");
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
          "Invalid submodeStructureTransportModeAndSubMode string: " +
          submodeStructureTransportModeAndSubMode
        );
      }
    }
    return null;
  }
}

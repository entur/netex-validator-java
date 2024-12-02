package org.entur.netex.validation.validator.model;

import java.util.Objects;
import javax.annotation.Nullable;
import org.entur.netex.validation.exception.NetexValidationException;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TransportSubmodeStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A pair of mode and sub-mode.
 */
public record TransportModeAndSubMode(
  AllVehicleModesOfTransportEnumeration mode,
  TransportSubMode subMode
) {
  private static final Logger LOGGER = LoggerFactory.getLogger(
    TransportModeAndSubMode.class
  );

  public TransportModeAndSubMode {
    Objects.requireNonNull(mode, "Transport mode cannot be null");
    Objects.requireNonNull(mode, "Transport submode cannot be null");
  }

  @Nullable
  public static TransportModeAndSubMode of(StopPlace stopPlace) {
    AllVehicleModesOfTransportEnumeration transportMode =
      stopPlace.getTransportMode();
    if (transportMode == null) {
      return null;
    }
    return TransportSubMode
      .of(stopPlace)
      .map(submode -> new TransportModeAndSubMode(transportMode, submode))
      .orElse(null);
  }

  @Nullable
  public static TransportModeAndSubMode of(
    AllVehicleModesOfTransportEnumeration transportMode,
    TransportSubmodeStructure submodeStructure
  ) {
    if (transportMode == null) {
      return null;
    }
    return TransportSubMode
      .of(transportMode, submodeStructure)
      .map(submode -> new TransportModeAndSubMode(transportMode, submode))
      .orElseGet(() -> {
        LOGGER.warn(
          "Cannot map the transport submode. This should have been caught in prior validation steps"
        );
        return new TransportModeAndSubMode(
          transportMode,
          TransportSubMode.MISSING
        );
      });
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

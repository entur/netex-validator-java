package org.entur.netex.validation.validator.model;

import java.util.Objects;
import javax.annotation.Nullable;
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
      .orElse(
        new TransportModeAndSubMode(transportMode, TransportSubMode.MISSING)
      );
  }
}

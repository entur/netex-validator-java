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
  public TransportModeAndSubMode {
    Objects.requireNonNull(mode, "Transport mode cannot be null");
    Objects.requireNonNull(mode, "Transport submode cannot be null");
  }

  /**
   * Return the transport mode and sub-mode for the given stop place.
   * Return null if the transport mode is null.
   * The submode can be missing, in which case it is mapped to TransportSubMode.MISSING.
   */
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
      .orElse(
        new TransportModeAndSubMode(transportMode, TransportSubMode.MISSING)
      );
  }

  /**
   * Return the transport mode and sub-mode for the corresponding NeTEx structures.
   * Return null if the transport mode is null
   * The submode can be missing, in which case it is mapped to TransportSubMode.MISSING.
   */
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

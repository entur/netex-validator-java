package org.entur.netex.validation.validator.model;

import java.util.Objects;
import java.util.Optional;
import org.entur.netex.validation.exception.NetexValidationException;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TransportSubmodeStructure;

/**
 * A NeTEx transport sub-mode.
 */
public record TransportSubMode(String name) {
  public static final TransportSubMode MISSING = new TransportSubMode("");

  public TransportSubMode {
    Objects.requireNonNull(name, "Transport submode cannot be null");
  }

  public static Optional<TransportSubMode> of(StopPlace stopPlace) {
    if (stopPlace == null || stopPlace.getTransportMode() == null) {
      return Optional.empty();
    }
    String subModeName =
      switch (stopPlace.getTransportMode()) {
        case AIR -> stopPlace.getAirSubmode() == null
          ? null
          : stopPlace.getAirSubmode().value();
        case BUS -> stopPlace.getBusSubmode() == null
          ? null
          : stopPlace.getBusSubmode().value();
        case COACH -> stopPlace.getCoachSubmode() == null
          ? null
          : stopPlace.getCoachSubmode().value();
        case METRO -> stopPlace.getMetroSubmode() == null
          ? null
          : stopPlace.getMetroSubmode().value();
        case RAIL -> stopPlace.getRailSubmode() == null
          ? null
          : stopPlace.getRailSubmode().value();
        case TRAM -> stopPlace.getTramSubmode() == null
          ? null
          : stopPlace.getTramSubmode().value();
        case WATER -> stopPlace.getWaterSubmode() == null
          ? null
          : stopPlace.getWaterSubmode().value();
        case CABLEWAY -> stopPlace.getTelecabinSubmode() == null
          ? null
          : stopPlace.getTelecabinSubmode().value();
        case FUNICULAR -> stopPlace.getFunicularSubmode() == null
          ? null
          : stopPlace.getFunicularSubmode().value();
        case SNOW_AND_ICE -> stopPlace.getSnowAndIceSubmode() == null
          ? null
          : stopPlace.getSnowAndIceSubmode().value();
        default -> throw new NetexValidationException(
          "Unsupported Transport mode in stop place, while getting sub transport mode: " +
          stopPlace
        );
      };
    return Optional.ofNullable(
      subModeName == null ? null : new TransportSubMode(subModeName)
    );
  }

  public static Optional<TransportSubMode> of(
    AllVehicleModesOfTransportEnumeration transportMode,
    TransportSubmodeStructure subModeStructure
  ) {
    if (transportMode == null || subModeStructure == null) {
      return Optional.empty();
    }
    String subModeName =
      switch (transportMode) {
        case AIR -> subModeStructure.getAirSubmode() == null
          ? null
          : subModeStructure.getAirSubmode().value();
        case BUS -> subModeStructure.getBusSubmode() == null
          ? null
          : subModeStructure.getBusSubmode().value();
        case COACH -> subModeStructure.getCoachSubmode() == null
          ? null
          : subModeStructure.getCoachSubmode().value();
        case METRO -> subModeStructure.getMetroSubmode() == null
          ? null
          : subModeStructure.getMetroSubmode().value();
        case RAIL -> subModeStructure.getRailSubmode() == null
          ? null
          : subModeStructure.getRailSubmode().value();
        case TAXI -> subModeStructure.getTaxiSubmode() == null
          ? null
          : subModeStructure.getTaxiSubmode().value();
        case TRAM -> subModeStructure.getTramSubmode() == null
          ? null
          : subModeStructure.getTramSubmode().value();
        case WATER -> subModeStructure.getWaterSubmode() == null
          ? null
          : subModeStructure.getWaterSubmode().value();
        case CABLEWAY -> subModeStructure.getTelecabinSubmode() == null
          ? null
          : subModeStructure.getTelecabinSubmode().value();
        case FUNICULAR -> subModeStructure.getFunicularSubmode() == null
          ? null
          : subModeStructure.getFunicularSubmode().value();
        case SNOW_AND_ICE -> subModeStructure.getSnowAndIceSubmode() == null
          ? null
          : subModeStructure.getSnowAndIceSubmode().value();
        default -> null;
      };

    if (subModeName == null) {
      return Optional.empty();
    }
    return Optional.of(new TransportSubMode(subModeName));
  }
}

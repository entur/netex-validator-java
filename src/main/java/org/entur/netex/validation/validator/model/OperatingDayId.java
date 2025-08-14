package org.entur.netex.validation.validator.model;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.entur.netex.validation.exception.NetexValidationException;
import org.rutebanken.netex.model.*;

/**
 * The NeTEx id of OperatingDay.
 */
public record OperatingDayId(String id) implements ActiveDatesId {
  public OperatingDayId {
    Objects.requireNonNull(id, "OperatingDay id should not be null");
    if (!isValid(id)) {
      throw new NetexValidationException("Invalid OperatingDay id: " + id);
    }
  }

  public static OperatingDayId of(OperatingDay operatingDay) {
    return Optional
      .ofNullable(operatingDay)
      .map(EntityStructure::getId)
      .map(OperatingDayId::new)
      .orElse(null);
  }

  public static OperatingDayId ofFromOperatingDayRef(OperatingPeriod operatingPeriod) {
    return of(operatingPeriod, OperatingPeriod_VersionStructure::getFromOperatingDayRef);
  }

  public static OperatingDayId ofToOperatingDayRef(OperatingPeriod operatingPeriod) {
    return of(operatingPeriod, OperatingPeriod_VersionStructure::getToOperatingDayRef);
  }

  public static OperatingDayId of(
    OperatingPeriod operatingPeriod,
    Function<OperatingPeriod, OperatingDayRefStructure> getOperatingDayRef
  ) {
    return Optional
      .ofNullable(operatingPeriod)
      .map(getOperatingDayRef)
      .map(VersionOfObjectRefStructure::getRef)
      .map(OperatingDayId::new)
      .orElse(null);
  }

  public static OperatingDayId of(DayTypeAssignment dayTypeAssignment) {
    return Optional
      .ofNullable(dayTypeAssignment.getOperatingDayRef())
      .map(VersionOfObjectRefStructure::getRef)
      .map(OperatingDayId::new)
      .orElse(null);
  }

  public static OperatingDayId ofNullable(String id) {
    return id == null ? null : new OperatingDayId(id);
  }

  public static boolean isValid(String id) {
    return id != null && id.contains(":OperatingDay:");
  }

  @Override
  public String toString() {
    return id();
  }
}

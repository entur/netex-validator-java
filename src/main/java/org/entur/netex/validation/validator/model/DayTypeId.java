package org.entur.netex.validation.validator.model;

import jakarta.xml.bind.JAXBElement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.entur.netex.validation.exception.NetexValidationException;
import org.rutebanken.netex.model.*;

/**
 * The NeTEx id of DayType.
 */
public record DayTypeId(String id) {
  public DayTypeId {
    Objects.requireNonNull(id, "DayType id should not be null");
    if (!isValid(id)) {
      throw new NetexValidationException("Invalid dayType id: " + id);
    }
  }

  public static List<DayTypeId> of(ServiceJourney serviceJourney) {
    return Optional
      .of(serviceJourney)
      .map(ServiceJourney::getDayTypes)
      .map(DayTypeRefs_RelStructure::getDayTypeRef)
      .map(dayTypeRefs ->
        dayTypeRefs
          .stream()
          .map(dayTypeRef ->
            Optional
              .ofNullable(dayTypeRef.getValue())
              .map(VersionOfObjectRefStructure::getRef)
              .map(DayTypeId::new)
              .orElse(null)
          )
          .filter(Objects::nonNull)
          .toList()
      )
      .orElse(List.of());
  }

  public static DayTypeId of(DayTypeAssignment dayTypeAssignment) {
    return Optional
      .ofNullable(dayTypeAssignment)
      .map(DayTypeAssignment::getDayTypeRef)
      .map(JAXBElement::getValue)
      .map(VersionOfObjectRefStructure::getRef)
      .filter(DayTypeId::isValid)
      .map(DayTypeId::new)
      .orElse(null);
  }

  public static DayTypeId ofValidId(DayType dayType) {
    return Optional
      .of(dayType)
      .map(DayType::getId)
      .map(DayTypeId::ofValidId)
      .orElse(null);
  }

  public static DayTypeId ofValidId(String id) {
    return isValid(id) ? new DayTypeId(id) : null;
  }

  public static DayTypeId ofNullable(String id) {
    return id == null ? null : new DayTypeId(id);
  }

  public static boolean isValid(String id) {
    return id != null && id.contains(":DayType:");
  }

  @Override
  public String toString() {
    return id();
  }
}

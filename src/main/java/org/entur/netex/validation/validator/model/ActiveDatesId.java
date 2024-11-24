package org.entur.netex.validation.validator.model;

import org.entur.netex.validation.exception.NetexValidationException;

public sealed interface ActiveDatesId permits DayTypeId, OperatingDayId {
  static ActiveDatesId of(String typeId) {
    if (DayTypeId.isValid(typeId)) {
      return new DayTypeId(typeId);
    } else if (OperatingDayId.isValid(typeId)) {
      return new OperatingDayId(typeId);
    } else {
      throw new NetexValidationException("Invalid active dates id: " + typeId);
    }
  }
}

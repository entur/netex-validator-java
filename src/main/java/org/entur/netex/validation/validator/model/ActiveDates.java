package org.entur.netex.validation.validator.model;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ActiveDates(DayTypeId dayTypeId, List<LocalDate> dates) {
  @Override
  public String toString() {
    return (
      dayTypeId.toString() +
      "§" +
      dates.stream().map(LocalDate::toString).collect(Collectors.joining(","))
    );
  }

  public static ActiveDates fromString(String activeDates) {
    String[] parts = activeDates.split("§");
    return new ActiveDates(
      DayTypeId.ofValidId(parts[0]),
      Stream.of(parts[1].split(",")).map(LocalDate::parse).collect(toList())
    );
  }

  public boolean isValid() {
    return dayTypeId != null && dates != null && !dates.isEmpty();
  }
}

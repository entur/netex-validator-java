package org.entur.netex.validation.validator.model;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ActiveDates(List<LocalDate> dates) {
  @Override
  public String toString() {
    return (dates.stream().map(LocalDate::toString).collect(Collectors.joining(",")));
  }

  public static ActiveDates fromString(String activeDates) {
    return new ActiveDates(
      Stream.of(activeDates.split(",")).map(LocalDate::parse).collect(toList())
    );
  }

  public boolean isValid() {
    return dates != null && !dates.isEmpty();
  }
}

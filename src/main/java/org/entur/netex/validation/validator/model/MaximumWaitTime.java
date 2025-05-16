package org.entur.netex.validation.validator.model;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import org.entur.netex.validation.exception.NetexValidationException;

public record MaximumWaitTime(Duration duration) {
  /**
   * Constructor that validates the duration is not null.
   */
  public MaximumWaitTime {
    Objects.requireNonNull(
      duration,
      "Invalid maximumWaitTime: should not be null"
    );
  }

  /**
   * Creates a MaximumWaitTime from a duration string.
   *
   * @param durationCharSequence A string representing duration
   * @return A new MaximumWaitTime instance
   * @throws NetexValidationException If the string cannot be parsed as a valid duration
   */
  public static MaximumWaitTime of(String durationCharSequence) {
    Objects.requireNonNull(
      durationCharSequence,
      "Duration string should not be null"
    );
    try {
      Duration duration = Duration.parse(durationCharSequence);
      return new MaximumWaitTime(duration);
    } catch (DateTimeParseException dateTimeParseException) {
      throw new NetexValidationException(
        "Invalid maximumWaitTime: " + durationCharSequence,
        dateTimeParseException
      );
    }
  }

  /**
   * Provides a string representation in ISO-8601 format.
   */
  @Override
  public String toString() {
    return duration.toString();
  }
}

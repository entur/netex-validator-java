package org.entur.netex.validation.validator.model;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.Optional;
import org.rutebanken.netex.model.TimetabledPassingTime;

/**
 * The NeTEx id of ScheduledStopPoint.
 * Arrival and departure times of TimetabledPassingTime for the ScheduledStopPoint.
 */
public record ServiceJourneyStop(
  ScheduledStopPointId scheduledStopPointId,
  LocalTime arrivalTime,
  LocalTime departureTime,
  int arrivalDayOffset,
  int departureDayOffset
) {
  public static ServiceJourneyStop of(
    ScheduledStopPointId scheduledStopPointId,
    TimetabledPassingTime timetabledPassingTime
  ) {
    return new ServiceJourneyStop(
      scheduledStopPointId,
      timetabledPassingTime.getArrivalTime(),
      timetabledPassingTime.getDepartureTime(),
      Optional
        .ofNullable(timetabledPassingTime.getArrivalDayOffset())
        .map(BigInteger::intValue)
        .orElse(0),
      Optional
        .ofNullable(timetabledPassingTime.getDepartureDayOffset())
        .map(BigInteger::intValue)
        .orElse(0)
    );
  }

  public boolean isValid() {
    return (
      scheduledStopPointId != null &&
      (arrivalTime != null || departureTime != null) &&
      arrivalDayOffset >= 0 &&
      departureDayOffset >= 0
    );
  }

  public static ServiceJourneyStop fixMissingTimeValues(
    ServiceJourneyStop serviceJourneyStop
  ) {
    boolean fixArrivalTime =
      serviceJourneyStop.arrivalTime() == null &&
      serviceJourneyStop.departureTime() != null;
    boolean fixDepartureTime =
      serviceJourneyStop.departureTime() == null &&
      serviceJourneyStop.arrivalTime() != null;

    if (fixArrivalTime || fixDepartureTime) {
      return new ServiceJourneyStop(
        serviceJourneyStop.scheduledStopPointId(),
        fixArrivalTime
          ? serviceJourneyStop.departureTime()
          : serviceJourneyStop.arrivalTime(),
        fixDepartureTime
          ? serviceJourneyStop.arrivalTime()
          : serviceJourneyStop.departureTime(),
        fixArrivalTime
          ? serviceJourneyStop.departureDayOffset()
          : serviceJourneyStop.arrivalDayOffset(),
        fixDepartureTime
          ? serviceJourneyStop.arrivalDayOffset()
          : serviceJourneyStop.departureDayOffset()
      );
    }
    return serviceJourneyStop;
  }
}

package org.entur.netex.validation.validator.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.entur.netex.validation.exception.NetexValidationException;
import org.junit.jupiter.api.Test;

class ServiceJourneyStopTest {

  @Test
  void testFixArrivalTime() {
    ScheduledStopPointId stopPointId = new ScheduledStopPointId(
      "TST:ScheduledStopPoint:1"
    );
    LocalTime departureTime = LocalTime.of(10, 0);
    int departureDayOffset = 1;

    ServiceJourneyStop input = new ServiceJourneyStop(
      stopPointId,
      null, // Missing arrival time
      departureTime,
      0,
      departureDayOffset
    );

    ServiceJourneyStop result = ServiceJourneyStop.fixMissingTimeValues(input);

    assertNotNull(result.arrivalTime());
    assertEquals(departureTime, result.arrivalTime());
    assertEquals(departureDayOffset, result.arrivalDayOffset());
    assertEquals(departureTime, result.departureTime());
    assertEquals(departureDayOffset, result.departureDayOffset());
  }

  @Test
  void testFixDepartureTime() {
    ScheduledStopPointId stopPointId = new ScheduledStopPointId(
      "TST:ScheduledStopPoint:2"
    );
    LocalTime arrivalTime = LocalTime.of(9, 30);
    int arrivalDayOffset = 1;

    ServiceJourneyStop input = new ServiceJourneyStop(
      stopPointId,
      arrivalTime,
      null, // Missing departure time
      arrivalDayOffset,
      0
    );

    ServiceJourneyStop result = ServiceJourneyStop.fixMissingTimeValues(input);

    assertNotNull(result.departureTime());
    assertEquals(arrivalTime, result.departureTime());
    assertEquals(arrivalDayOffset, result.departureDayOffset());
    assertEquals(arrivalTime, result.arrivalTime());
    assertEquals(arrivalDayOffset, result.arrivalDayOffset());
  }

  @Test
  void testNoChangesWhenTimesAreComplete() {
    ScheduledStopPointId stopPointId = new ScheduledStopPointId(
      "TST:ScheduledStopPoint:3"
    );
    LocalTime arrivalTime = LocalTime.of(8, 0);
    LocalTime departureTime = LocalTime.of(8, 15);
    int arrivalDayOffset = 0;
    int departureDayOffset = 0;

    ServiceJourneyStop input = new ServiceJourneyStop(
      stopPointId,
      arrivalTime,
      departureTime,
      arrivalDayOffset,
      departureDayOffset
    );

    assertEquals(input, ServiceJourneyStop.fixMissingTimeValues(input));
  }

  @Test
  void testNoChangesWhenBothTimesAreMissing() {
    ScheduledStopPointId stopPointId = new ScheduledStopPointId(
      "TST:ScheduledStopPoint:4"
    );

    ServiceJourneyStop input = new ServiceJourneyStop(
      stopPointId,
      null, // Missing arrival time
      null, // Missing departure time
      0,
      0
    );

    assertEquals(input, ServiceJourneyStop.fixMissingTimeValues(input));
  }

  @Test
  void testToStringWithAllFields() {
    ScheduledStopPointId stopPointId = new ScheduledStopPointId(
      "TST:ScheduledStopPoint:1"
    );
    LocalTime arrivalTime = LocalTime.of(9, 0);
    LocalTime departureTime = LocalTime.of(9, 30);
    int arrivalDayOffset = 1;
    int departureDayOffset = 2;

    ServiceJourneyStop stop = new ServiceJourneyStop(
      stopPointId,
      arrivalTime,
      departureTime,
      arrivalDayOffset,
      departureDayOffset
    );

    assertEquals(
      "scheduledStopPointId(TST:ScheduledStopPoint:1),arrival(09:00§1),departure(09:30§2)",
      stop.toString()
    );
  }

  @Test
  void testToStringWithMissingArrivalTime() {
    ScheduledStopPointId stopPointId = new ScheduledStopPointId(
      "TST:ScheduledStopPoint:2"
    );
    LocalTime departureTime = LocalTime.of(10, 0);
    int departureDayOffset = 0;

    ServiceJourneyStop stop = new ServiceJourneyStop(
      stopPointId,
      null,
      departureTime,
      0,
      departureDayOffset
    );

    assertEquals(
      "scheduledStopPointId(TST:ScheduledStopPoint:2),departure(10:00§0)",
      stop.toString()
    );
  }

  @Test
  void testToStringWithMissingDepartureTime() {
    ScheduledStopPointId stopPointId = new ScheduledStopPointId(
      "TST:ScheduledStopPoint:3"
    );
    LocalTime arrivalTime = LocalTime.of(8, 0);
    int arrivalDayOffset = 0;

    ServiceJourneyStop stop = new ServiceJourneyStop(
      stopPointId,
      arrivalTime,
      null,
      arrivalDayOffset,
      0
    );

    assertEquals(
      "scheduledStopPointId(TST:ScheduledStopPoint:3),arrival(08:00§0)",
      stop.toString()
    );
  }

  @Test
  void testFromStringWithAllFields() {
    String input =
      "scheduledStopPointId(TST:ScheduledStopPoint:1),arrival(09:00§1),departure(09:30§2)";

    ServiceJourneyStop result = ServiceJourneyStop.fromString(input);

    assertEquals(
      new ScheduledStopPointId("TST:ScheduledStopPoint:1"),
      result.scheduledStopPointId()
    );
    assertEquals(LocalTime.of(9, 0), result.arrivalTime());
    assertEquals(1, result.arrivalDayOffset());
    assertEquals(LocalTime.of(9, 30), result.departureTime());
    assertEquals(2, result.departureDayOffset());
  }

  @Test
  void testFromStringWithMissingArrivalTime() {
    String input =
      "scheduledStopPointId(TST:ScheduledStopPoint:2),departure(10:00§0)";

    ServiceJourneyStop result = ServiceJourneyStop.fromString(input);

    assertEquals(
      new ScheduledStopPointId("TST:ScheduledStopPoint:2"),
      result.scheduledStopPointId()
    );
    assertNull(result.arrivalTime());
    assertEquals(0, result.arrivalDayOffset());
    assertEquals(LocalTime.of(10, 0), result.departureTime());
    assertEquals(0, result.departureDayOffset());
  }

  @Test
  void testFromStringWithMissingDepartureTime() {
    String input =
      "scheduledStopPointId(TST:ScheduledStopPoint:3),arrival(08:00§0)";

    ServiceJourneyStop result = ServiceJourneyStop.fromString(input);

    assertEquals(
      new ScheduledStopPointId("TST:ScheduledStopPoint:3"),
      result.scheduledStopPointId()
    );
    assertEquals(LocalTime.of(8, 0), result.arrivalTime());
    assertEquals(0, result.arrivalDayOffset());
    assertNull(result.departureTime());
    assertEquals(0, result.departureDayOffset());
  }

  @Test
  void testFromStringWithInvalidInput() {
    String input = "invalidInput";

    NetexValidationException exception = assertThrows(
      NetexValidationException.class,
      () -> ServiceJourneyStop.fromString(input)
    );
    assertEquals(
      "Invalid passing time info: invalidInput",
      exception.getMessage()
    );
  }

  @Test
  void testFromStringWithNullInput() {
    ServiceJourneyStop result = ServiceJourneyStop.fromString(null);

    assertNull(result);
  }
}

package org.entur.netex.validation.validator.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.TimetabledPassingTime;

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
      departureDayOffset,
      true,
      true
    );

    ServiceJourneyStop result = ServiceJourneyStop.fixMissingTimeValues(input);

    assertNotNull(result.arrivalTime());
    assertEquals(departureTime, result.arrivalTime());
    assertEquals(departureDayOffset, result.arrivalDayOffset());
    assertEquals(departureTime, result.departureTime());
    assertEquals(departureDayOffset, result.departureDayOffset());
    assertTrue(result.isForAlighting());
    assertTrue(result.isForBoarding());
  }

  @Test
  void testServiceJourneyStopAlightingAndBoarding() {
    ScheduledStopPointId stopPointId = new ScheduledStopPointId(
      "TST:ScheduledStopPoint:1"
    );

    TimetabledPassingTime timetabledPassingTime = new TimetabledPassingTime()
      .withArrivalTime(LocalTime.of(10, 0))
      .withDepartureTime(LocalTime.of(11, 0));

    ServiceJourneyStop serviceJourneyStop1 = ServiceJourneyStop.of(
      stopPointId,
      timetabledPassingTime,
      null,
      null
    );

    assertTrue(serviceJourneyStop1.isForAlighting());
    assertTrue(serviceJourneyStop1.isForBoarding());

    ServiceJourneyStop serviceJourneyStop2 = ServiceJourneyStop.of(
      stopPointId,
      timetabledPassingTime,
      true,
      true
    );

    assertTrue(serviceJourneyStop2.isForAlighting());
    assertTrue(serviceJourneyStop2.isForBoarding());

    ServiceJourneyStop serviceJourneyStop3 = ServiceJourneyStop.of(
      stopPointId,
      timetabledPassingTime,
      false,
      false
    );

    assertFalse(serviceJourneyStop3.isForAlighting());
    assertFalse(serviceJourneyStop3.isForBoarding());
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
      0,
      true,
      true
    );

    ServiceJourneyStop result = ServiceJourneyStop.fixMissingTimeValues(input);

    assertNotNull(result.departureTime());
    assertEquals(arrivalTime, result.departureTime());
    assertEquals(arrivalDayOffset, result.departureDayOffset());
    assertEquals(arrivalTime, result.arrivalTime());
    assertEquals(arrivalDayOffset, result.arrivalDayOffset());
    assertTrue(result.isForAlighting());
    assertTrue(result.isForBoarding());
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
      departureDayOffset,
      true,
      true
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
      0,
      true,
      true
    );

    assertEquals(input, ServiceJourneyStop.fixMissingTimeValues(input));
  }
}

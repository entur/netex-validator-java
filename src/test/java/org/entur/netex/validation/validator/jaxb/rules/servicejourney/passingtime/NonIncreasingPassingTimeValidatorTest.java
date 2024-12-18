package org.entur.netex.validation.validator.jaxb.rules.servicejourney.passingtime;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.validation.test.jaxb.support.NetexEntitiesTestFactory;
import org.entur.netex.validation.test.jaxb.support.TestCommonDataRepository;
import org.entur.netex.validation.test.jaxb.support.TestStopPlaceRepository;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.jaxb.JAXBValidationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;

class NonIncreasingPassingTimeValidatorTest {

  private static final String TEST_CODESPACE = "ENT";
  private static final String TEST_LINE_XML_FILE = "line.xml";
  private static final String VALIDATION_REPORT_ID = "Test1122";

  private static final int NUMBER_OF_STOP_POINTS_IN_JOURNEY_PATTERN = 4;

  private NonIncreasingPassingTimeValidator validator;
  private NetexEntitiesTestFactory netexEntitiesTestFactory;
  private List<NetexEntitiesTestFactory.CreateTimetabledPassingTime> timetabledPassingTimes;
  private List<LocalTime> departureTimes;
  private List<ScheduledStopPointRefStructure> scheduledStopPointRefs;

  @BeforeEach
  void setup() {
    validator = new NonIncreasingPassingTimeValidator();

    netexEntitiesTestFactory = new NetexEntitiesTestFactory();

    NetexEntitiesTestFactory.CreateJourneyPattern createJourneyPattern =
      netexEntitiesTestFactory.createJourneyPattern();

    NetexEntitiesTestFactory.CreateServiceJourney createServiceJourney =
      netexEntitiesTestFactory.createServiceJourney(createJourneyPattern);

    scheduledStopPointRefs =
      IntStream
        .rangeClosed(1, NUMBER_OF_STOP_POINTS_IN_JOURNEY_PATTERN)
        .mapToObj(NetexEntitiesTestFactory::createScheduledStopPointRef)
        .toList();

    List<NetexEntitiesTestFactory.CreateStopPointInJourneyPattern> stopPointInJourneyPatterns =
      IntStream
        .rangeClosed(1, NUMBER_OF_STOP_POINTS_IN_JOURNEY_PATTERN)
        .mapToObj(index ->
          createJourneyPattern
            .createStopPointInJourneyPattern(index)
            .withScheduledStopPointRef(scheduledStopPointRefs.get(index - 1))
        )
        .toList();

    departureTimes =
      IntStream
        .rangeClosed(1, NUMBER_OF_STOP_POINTS_IN_JOURNEY_PATTERN)
        .mapToObj(index -> LocalTime.of(5, index * 5))
        .toList();

    timetabledPassingTimes =
      IntStream
        .rangeClosed(1, NUMBER_OF_STOP_POINTS_IN_JOURNEY_PATTERN)
        .mapToObj(index ->
          createServiceJourney
            .createTimetabledPassingTime(
              index,
              stopPointInJourneyPatterns.get(index - 1)
            )
            .withDepartureTime(departureTimes.get(index - 1))
        )
        .toList();
  }

  @Test
  void testValidateServiceJourneyWithRegularStop() {
    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();
    assertNoIssue(netexEntitiesIndex);
  }

  @Test
  void testValidateServiceJourneyWithRegularStopMissingTime() {
    // remove arrival time and departure time for the first passing time
    timetabledPassingTimes.get(0).withDepartureTime(null).withArrivalTime(null);

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();
    assertIssue(
      netexEntitiesIndex,
      NonIncreasingPassingTimeValidator.RULE_INCOMPLETE_TIME
    );
  }

  @Test
  void testValidateServiceJourneyWithRegularStopInconsistentTime() {
    // set arrival time after departure time for the first passing time
    timetabledPassingTimes
      .get(0)
      .withArrivalTime(departureTimes.get(0).plusMinutes(1));

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();
    assertIssue(
      netexEntitiesIndex,
      NonIncreasingPassingTimeValidator.RULE_INCONSISTENT_TIME
    );
  }

  @Test
  void testValidateServiceJourneyWithAreaStop() {
    // remove arrival time and departure time and add flex window
    timetabledPassingTimes
      .get(0)
      .withDepartureTime(null)
      .withArrivalTime(null)
      .withEarliestDepartureTime(LocalTime.MIDNIGHT)
      .withLatestArrivalTime(LocalTime.MIDNIGHT.plusMinutes(1));

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();
    netexEntitiesIndex
      .getFlexibleStopPlaceIdByStopPointRefIndex()
      .put(scheduledStopPointRefs.get(0).getRef(), "");

    assertNoIssue(netexEntitiesIndex);
  }

  @Test
  void testValidateServiceJourneyWithAreaStopMissingTimeWindow() {
    // remove arrival time and departure time and add flex window
    timetabledPassingTimes.get(0).withDepartureTime(null).withArrivalTime(null);

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();

    netexEntitiesIndex
      .getFlexibleStopPlaceIdByStopPointRefIndex()
      .put(scheduledStopPointRefs.get(0).getRef(), "");

    assertIssue(
      netexEntitiesIndex,
      NonIncreasingPassingTimeValidator.RULE_INCOMPLETE_TIME
    );
  }

  @Test
  void testValidateServiceJourneyWithAreaStopInconsistentTimeWindow() {
    // remove arrival time and departure time and add flex window
    timetabledPassingTimes
      .get(0)
      .withDepartureTime(null)
      .withArrivalTime(null)
      .withEarliestDepartureTime(LocalTime.MIDNIGHT.plusMinutes(1))
      .withLatestArrivalTime(LocalTime.MIDNIGHT);

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();

    netexEntitiesIndex
      .getFlexibleStopPlaceIdByStopPointRefIndex()
      .put(scheduledStopPointRefs.get(0).getRef(), "");

    assertIssue(
      netexEntitiesIndex,
      NonIncreasingPassingTimeValidator.RULE_INCONSISTENT_TIME
    );
  }

  @Test
  void testValidateServiceJourneyWithRegularStopFollowedByRegularStopNonIncreasingTime() {
    // remove arrival time and departure time and add flex window on second stop
    timetabledPassingTimes
      .get(1)
      .withArrivalTime(departureTimes.get(0).minusMinutes(1));

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();

    assertIssue(
      netexEntitiesIndex,
      NonIncreasingPassingTimeValidator.RULE_NON_INCREASING_TIME
    );
  }

  /**
   * This test makes sure all passing times are complete and consistent, before it checks for
   * increasing times.
   */
  @Test
  void testValidateWithRegularStopFollowedByRegularStopWithMissingTime() {
    // Set arrivalTime AFTER departure time (not valid)
    timetabledPassingTimes.get(1).withArrivalTime(null).withDepartureTime(null);

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();

    assertIssue(
      netexEntitiesIndex,
      NonIncreasingPassingTimeValidator.RULE_INCOMPLETE_TIME
    );
  }

  @Test
  void testValidateServiceJourneyWithRegularStopFollowedByStopArea() {
    // remove arrival time and departure time and add flex window on second stop
    timetabledPassingTimes
      .get(1)
      .withDepartureTime(null)
      .withArrivalTime(null)
      .withEarliestDepartureTime(departureTimes.get(1))
      .withLatestArrivalTime(departureTimes.get(1).plusMinutes(1));

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();

    netexEntitiesIndex
      .getFlexibleStopPlaceIdByStopPointRefIndex()
      .put(scheduledStopPointRefs.get(1).getRef(), "");

    assertNoIssue(netexEntitiesIndex);
  }

  @Test
  void testValidateServiceJourneyWithRegularStopFollowedByStopAreaNonIncreasingTime() {
    // remove arrival time and departure time and add flex window with decreasing time on second stop
    timetabledPassingTimes
      .get(1)
      .withEarliestDepartureTime(departureTimes.get(0).minusMinutes(1))
      .withArrivalTime(null)
      .withDepartureTime(null);

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();

    netexEntitiesIndex
      .getFlexibleStopPlaceIdByStopPointRefIndex()
      .put(scheduledStopPointRefs.get(0).getRef(), "");

    assertIssue(
      netexEntitiesIndex,
      NonIncreasingPassingTimeValidator.RULE_INCOMPLETE_TIME
    );
  }

  @Test
  void testValidateServiceJourneyWithStopAreaFollowedByRegularStop() {
    // remove arrival time and departure time and add flex window on first stop
    timetabledPassingTimes
      .get(0)
      .withEarliestDepartureTime(departureTimes.get(0))
      .withLatestArrivalTime(departureTimes.get(0))
      .withArrivalTime(null)
      .withDepartureTime(null);

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();

    netexEntitiesIndex
      .getFlexibleStopPlaceIdByStopPointRefIndex()
      .put(scheduledStopPointRefs.get(0).getRef(), "");

    assertNoIssue(netexEntitiesIndex);
  }

  @Test
  void testValidateServiceJourneyWithStopAreaFollowedByStopArea() {
    timetabledPassingTimes
      .get(0)
      .withEarliestDepartureTime(departureTimes.get(0))
      .withLatestArrivalTime(departureTimes.get(0))
      .withArrivalTime(null)
      .withDepartureTime(null);

    timetabledPassingTimes
      .get(1)
      .withEarliestDepartureTime(departureTimes.get(1))
      .withLatestArrivalTime(departureTimes.get(1).plusMinutes(1))
      .withArrivalTime(null)
      .withDepartureTime(null);

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();

    netexEntitiesIndex
      .getFlexibleStopPlaceIdByStopPointRefIndex()
      .put(scheduledStopPointRefs.get(0).getRef(), "");

    netexEntitiesIndex
      .getFlexibleStopPlaceIdByStopPointRefIndex()
      .put(scheduledStopPointRefs.get(1).getRef(), "");

    assertNoIssue(netexEntitiesIndex);
  }

  @Test
  void testValidateServiceJourneyWithStopAreaFollowedByStopAreaNonIncreasingTime() {
    // remove arrival time and departure time and add flex window on first stop and second stop
    // and add decreasing time on second stop
    timetabledPassingTimes
      .get(0)
      .withEarliestDepartureTime(departureTimes.get(0))
      .withLatestArrivalTime(departureTimes.get(0))
      .withArrivalTime(null)
      .withDepartureTime(null);

    timetabledPassingTimes
      .get(1)
      .withEarliestDepartureTime(departureTimes.get(1).minusMinutes(1))
      .withLatestArrivalTime(departureTimes.get(1).plusMinutes(1))
      .withArrivalTime(null)
      .withDepartureTime(null);

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();

    netexEntitiesIndex
      .getFlexibleStopPlaceIdByStopPointRefIndex()
      .put(scheduledStopPointRefs.get(0).getRef(), "");

    assertIssue(
      netexEntitiesIndex,
      NonIncreasingPassingTimeValidator.RULE_INCOMPLETE_TIME
    );
  }

  @Test
  void testValidateServiceJourneyWithStopAreaFollowedByRegularStopNonIncreasingTime() {
    // remove arrival time and departure time and add flex window on first stop
    // and add decreasing time on second stop
    timetabledPassingTimes
      .get(0)
      .withEarliestDepartureTime(departureTimes.get(0))
      .withLatestArrivalTime(departureTimes.get(0))
      .withArrivalTime(null)
      .withDepartureTime(null);

    timetabledPassingTimes
      .get(1)
      .withArrivalTime(departureTimes.get(0).minusMinutes(1))
      .withDepartureTime(null);

    NetexEntitiesIndex netexEntitiesIndex = netexEntitiesTestFactory.create();

    netexEntitiesIndex
      .getFlexibleStopPlaceIdByStopPointRefIndex()
      .put(scheduledStopPointRefs.get(0).getRef(), "");

    assertIssue(
      netexEntitiesIndex,
      NonIncreasingPassingTimeValidator.RULE_NON_INCREASING_TIME
    );
  }

  private void assertIssue(
    NetexEntitiesIndex netexEntitiesIndex,
    ValidationRule rule
  ) {
    JAXBValidationContext validationContext = createValidationContext(
      netexEntitiesIndex
    );
    List<ValidationIssue> validationIssues = validator.validate(
      validationContext
    );

    Assertions.assertEquals(1, validationIssues.size());
    Assertions.assertEquals(rule, validationIssues.get(0).rule());
  }

  private void assertNoIssue(NetexEntitiesIndex netexEntitiesIndex) {
    JAXBValidationContext validationContext = createValidationContext(
      netexEntitiesIndex
    );
    List<ValidationIssue> validationIssues = validator.validate(
      validationContext
    );

    Assertions.assertTrue(validationIssues.isEmpty());
  }

  private static JAXBValidationContext createValidationContext(
    NetexEntitiesIndex netexEntitiesIndex
  ) {
    return new JAXBValidationContext(
      VALIDATION_REPORT_ID,
      netexEntitiesIndex,
      TestCommonDataRepository.of(NUMBER_OF_STOP_POINTS_IN_JOURNEY_PATTERN),
      v ->
        TestStopPlaceRepository.ofLocalBusStops(
          NUMBER_OF_STOP_POINTS_IN_JOURNEY_PATTERN
        ),
      TEST_CODESPACE,
      TEST_LINE_XML_FILE,
      Map.of()
    );
  }
}

package org.entur.netex.validation.validator.jaxb.support;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.entur.netex.validation.test.jaxb.support.JAXBUtils;
import org.entur.netex.validation.validator.model.ScheduledStopPointId;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.PointInLinkSequence_VersionedChildStructure;
import org.rutebanken.netex.model.PointsInJourneyPattern_RelStructure;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

class NetexUtilsTest {

  public static final String TEST_SCHEDULED_STOP_POINT_ID =
    "TST:ScheduledStopPoint:1";
  public static final String TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID =
    "TST:StopPointInJourneyPattern:1";

  @Test
  void testEmptyJourneyPattern() {
    JourneyPattern journeyPattern = new JourneyPattern();
    Map<String, ScheduledStopPointId> stringScheduledStopPointIdMap =
      NetexUtils.scheduledStopPointIdByStopPointId(journeyPattern);
    assertNotNull(stringScheduledStopPointIdMap);
  }

  @Test
  void testScheduledStopPointIdByStopPointId() {
    JourneyPattern journeyPattern = journeyPattern();
    Map<String, ScheduledStopPointId> scheduledStopPointIdByStopPointId =
      NetexUtils.scheduledStopPointIdByStopPointId(journeyPattern);
    assertNotNull(scheduledStopPointIdByStopPointId);
    assertEquals(
      Map.of(
        TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID,
        new ScheduledStopPointId(TEST_SCHEDULED_STOP_POINT_ID)
      ),
      scheduledStopPointIdByStopPointId
    );
  }

  @Test
  void testSStopPointId() {
    JourneyPattern journeyPattern = journeyPattern();
    StopPointInJourneyPattern stopPointInJourneyPattern =
      NetexUtils.stopPointInJourneyPattern(
        TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID,
        journeyPattern
      );
    assertNotNull(stopPointInJourneyPattern);
    assertEquals(
      TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID,
      stopPointInJourneyPattern.getId()
    );
  }

  private static JourneyPattern journeyPattern() {
    JourneyPattern journeyPattern = new JourneyPattern();
    PointsInJourneyPattern_RelStructure pointsInJourneyPattern =
      new PointsInJourneyPattern_RelStructure();
    PointInLinkSequence_VersionedChildStructure point1 =
      new StopPointInJourneyPattern()
        .withId(TEST_STOP_POINT_IN_JOURNEY_PATTERN_ID)
        .withScheduledStopPointRef(
          JAXBUtils.createJaxbElement(
            new ScheduledStopPointRefStructure()
              .withRef(TEST_SCHEDULED_STOP_POINT_ID)
          )
        );
    Collection<PointInLinkSequence_VersionedChildStructure> points = List.of(
      point1
    );
    pointsInJourneyPattern.withPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern(
      points
    );

    journeyPattern.withPointsInSequence(pointsInJourneyPattern);
    return journeyPattern;
  }
}

package org.entur.netex.validation.validator.xpath.rules;

import static org.entur.netex.validation.test.xpath.support.XPathTestSupport.parseDocument;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.Test;

class ValidateInconsistentNumberOfTimetablePassingTimesTest {

  private static final String NETEX_FRAGMENT_SERVICE_FRAME_AND_TIMETABLE_FRAME =
    """
        <frames xmlns="http://www.netex.org.uk/netex">
          <ServiceFrame>
                   <lines>
                      <FlexibleLine version="46" id="ENT:FlexibleLine:1">
                      </FlexibleLine>
                  </lines>
                  <journeyPatterns>
                      <JourneyPattern version="1" id="ENT:JourneyPattern:1">
                          <pointsInSequence>
                                <StopPointInJourneyPattern order="1" version="32" id="ENT:StopPointInJourneyPattern:1">
                                    <ScheduledStopPointRef ref="ENT:ScheduledStopPoint:1"/>
                                    <ForAlighting>false</ForAlighting>
                                    <ForBoarding>true</ForBoarding>
                                    <DestinationDisplayRef ref="ENT:DestinationDisplay:1"/>
                                </StopPointInJourneyPattern>
                                ${STOP_POINT_IN_JOURNEY_PATTERN}
                                <StopPointInJourneyPattern order="3" version="1" id="ENT:StopPointInJourneyPattern:3">
                                    <ScheduledStopPointRef ref="ENT:ScheduledStopPoint:3"/>
                                    <ForAlighting>true</ForAlighting>
                                    <ForBoarding>false</ForBoarding>
                                </StopPointInJourneyPattern>
                          </pointsInSequence>
                      </JourneyPattern>
                  </journeyPatterns>
          </ServiceFrame>
        
        
          <TimetableFrame version="1" id="ENT:TimetableFrame:1">
              <vehicleJourneys>
                  <ServiceJourney version="13" id="ENT:ServiceJourney:1">
                      <Name>indre-hele-spesialdager lørdager</Name>
                      <dayTypes>
                           <DayTypeRef ref="ENT:DayType:1"/>
                      </dayTypes>
                      <JourneyPatternRef ref="ENT:JourneyPattern:1" version="1"/>
                      <passingTimes>
                            <TimetabledPassingTime version="0" id="ENT:TimetabledPassingTime:1">
                              <StopPointInJourneyPatternRef ref="ENT:StopPointInJourneyPattern:1" version="32"/>
                              <LatestArrivalTime>15:00:00</LatestArrivalTime>
                              <EarliestDepartureTime>10:00:00</EarliestDepartureTime>
                          </TimetabledPassingTime>
                          <TimetabledPassingTime version="0" id="ENT:TimetabledPassingTime:2">
                              <StopPointInJourneyPatternRef ref="ENT:StopPointInJourneyPattern:2" version="1"/>
                              <LatestArrivalTime>15:00:00</LatestArrivalTime>
                              <EarliestDepartureTime>10:00:00</EarliestDepartureTime>
                          </TimetabledPassingTime>
                      </passingTimes>
                  </ServiceJourney>
              </vehicleJourneys>
          </TimetableFrame>
        </frames>
        """;

  @Test
  void testMissingTimetablePassingTimesMatch() {
    ValidateInconsistentNumberOfTimetablePassingTimes validator =
      new ValidateInconsistentNumberOfTimetablePassingTimes();

    XdmNode xdmItems = parseDocument(
      NETEX_FRAGMENT_SERVICE_FRAME_AND_TIMETABLE_FRAME.replace(
        "${STOP_POINT_IN_JOURNEY_PATTERN}",
        "<StopPointInJourneyPattern/>"
      )
    );
    XdmNode timetableFrame = xdmItems
      .children("TimetableFrame")
      .iterator()
      .next();
    XPathRuleValidationContext xpathValidationContext =
      TestValidationContextBuilder.ofDocument(timetableFrame).build();
    List<ValidationIssue> validationIssues = validator.validate(
      xpathValidationContext
    );
    assertEquals(1, validationIssues.size());
  }

  @Test
  void testMissingTimetablePassingTimesNoMatch() {
    ValidateInconsistentNumberOfTimetablePassingTimes validator =
      new ValidateInconsistentNumberOfTimetablePassingTimes();

    XdmNode xdmItems = parseDocument(
      NETEX_FRAGMENT_SERVICE_FRAME_AND_TIMETABLE_FRAME.replace(
        "${STOP_POINT_IN_JOURNEY_PATTERN}",
        ""
      )
    );
    XdmNode timetableFrame = xdmItems
      .children("TimetableFrame")
      .iterator()
      .next();
    XPathRuleValidationContext xpathValidationContext =
      TestValidationContextBuilder.ofDocument(timetableFrame).build();
    List<ValidationIssue> validationIssues = validator.validate(
      xpathValidationContext
    );
    assertTrue(validationIssues.isEmpty());
  }
}

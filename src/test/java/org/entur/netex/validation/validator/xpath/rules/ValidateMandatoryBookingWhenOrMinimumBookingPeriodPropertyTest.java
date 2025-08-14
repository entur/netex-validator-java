package org.entur.netex.validation.validator.xpath.rules;

import java.util.List;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidateMandatoryBookingWhenOrMinimumBookingPeriodPropertyTest {

  private static final String NETEX_FRAGMENT =
    """
  <frames xmlns="http://www.netex.org.uk/netex">
      <ServiceFrame>
           <lines>
              <FlexibleLine version="46" id="BRA:FlexibleLine:9204411c-bf86-4b6a-b8fa-5c40b8702213">
               ${BOOK_WHEN}
              </FlexibleLine>
          </lines>
          <journeyPatterns>
              <JourneyPattern version="1" id="BRA:JourneyPattern:58fe9e56-1799-4e09-af8e-5091199d2319">
                  <pointsInSequence>
                      <StopPointInJourneyPattern order="1" version="32" id="BRA:StopPointInJourneyPattern:eaa48c3c-aee6-4b24-a721-ee3b05303f27">
                          <ScheduledStopPointRef ref="BRA:ScheduledStopPoint:69136f28-5575-4b68-b6db-7229a42af8ee"/>
                          <ForAlighting>false</ForAlighting>
                          <ForBoarding>true</ForBoarding>
                          <DestinationDisplayRef ref="BRA:DestinationDisplay:24ce4e13-be81-443a-9d65-f2c4b7cb4c76"/>
                      ${BOOKING_ARRANGEMENT}
                      </StopPointInJourneyPattern>
                      <StopPointInJourneyPattern order="2" version="1" id="BRA:StopPointInJourneyPattern:1f8eb0f1-76df-467b-ad35-8ca4136dbcec">
                          <ScheduledStopPointRef ref="BRA:ScheduledStopPoint:2e059449-3c88-4447-987a-90a41556a8a7"/>
                          <ForAlighting>true</ForAlighting>
                          <ForBoarding>false</ForBoarding>
                      ${BOOKING_ARRANGEMENT}
                      </StopPointInJourneyPattern>
                  </pointsInSequence>
              </JourneyPattern>
          </journeyPatterns>
    </ServiceFrame>
  
    <TimetableFrame version="1" id="BRA:TimetableFrame:1">
          <vehicleJourneys>
              <ServiceJourney version="13" id="BRA:ServiceJourney:0caf533c-d8ce-41d0-980d-46c7c86d6895">
                  <Name>indre-hele-spesialdager lørdager</Name>
                  <dayTypes>
                      <DayTypeRef ref="BRA:DayType:3e5e36b6-6131-4950-b498-60c91ac4117f"/>
                  </dayTypes>
                  <JourneyPatternRef ref="BRA:JourneyPattern:58fe9e56-1799-4e09-af8e-5091199d2319" version="1"/>
                  ${FLEXIBLE_PROPERTIES}
                  <passingTimes>
                      <TimetabledPassingTime version="0" id="BRA:TimetabledPassingTime:d3e69c70-21b7-45f9-a5ed-81f766cd6c73">
                          <StopPointInJourneyPatternRef ref="BRA:StopPointInJourneyPattern:eaa48c3c-aee6-4b24-a721-ee3b05303f27" version="32"/>
                          <LatestArrivalTime>15:00:00</LatestArrivalTime>
                          <EarliestDepartureTime>10:00:00</EarliestDepartureTime>
                      </TimetabledPassingTime>
                      <TimetabledPassingTime version="0" id="BRA:TimetabledPassingTime:202ab057-1013-430e-97b6-002b3d6a001b">
                          <StopPointInJourneyPatternRef ref="BRA:StopPointInJourneyPattern:1f8eb0f1-76df-467b-ad35-8ca4136dbcec" version="1"/>
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
  void testBookWhenMissingInLineAndStopPointAndServiceJourney() {
    ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty validateMandatoryBookingWhenOrMinimumBookingPeriodProperty =
      new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty();
    String flexibleLineWithMissingBookWhen = NETEX_FRAGMENT
      .replace("${BOOK_WHEN}", "")
      .replace("${BOOKING_ARRANGEMENT}", "")
      .replace("${FLEXIBLE_PROPERTIES}", "");
    XPathRuleValidationContext xpathRuleValidationContext = TestValidationContextBuilder
      .ofNetexFragment(flexibleLineWithMissingBookWhen)
      .build();

    List<ValidationIssue> validationIssues =
      validateMandatoryBookingWhenOrMinimumBookingPeriodProperty.validate(
        xpathRuleValidationContext
      );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertEquals(
      validateMandatoryBookingWhenOrMinimumBookingPeriodProperty.rule(),
      validationIssues.get(0).rule()
    );
  }

  @Test
  void testBookWhenInLine() {
    ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty validateMandatoryBookingWhenOrMinimumBookingPeriodProperty =
      new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty();
    String flexibleLineWithBookWhen = NETEX_FRAGMENT
      .replace("${BOOK_WHEN}", "<BookWhen>advanceAndDayOfTravel</BookWhen>")
      .replace("${BOOKING_ARRANGEMENT}", "")
      .replace("${FLEXIBLE_PROPERTIES}", "");
    XPathRuleValidationContext xpathRuleValidationContext = TestValidationContextBuilder
      .ofNetexFragment(flexibleLineWithBookWhen)
      .build();

    List<ValidationIssue> validationIssues =
      validateMandatoryBookingWhenOrMinimumBookingPeriodProperty.validate(
        xpathRuleValidationContext
      );
    Assertions.assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testBookWhenInJourneyPattern() {
    ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty validateMandatoryBookingWhenOrMinimumBookingPeriodProperty =
      new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty();
    String journeyPatternWithBookingArrangement = NETEX_FRAGMENT
      .replace("${BOOK_WHEN}", "")
      .replace(
        "${BOOKING_ARRANGEMENT}",
        "<BookingArrangements><BookWhen>advanceAndDayOfTravel</BookWhen></BookingArrangements>"
      )
      .replace("${FLEXIBLE_PROPERTIES}", "");
    XPathRuleValidationContext xpathRuleValidationContext = TestValidationContextBuilder
      .ofNetexFragment(journeyPatternWithBookingArrangement)
      .build();

    List<ValidationIssue> validationIssues =
      validateMandatoryBookingWhenOrMinimumBookingPeriodProperty.validate(
        xpathRuleValidationContext
      );
    Assertions.assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testBookWhenInServiceJourney() {
    ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty validateMandatoryBookingWhenOrMinimumBookingPeriodProperty =
      new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty();
    String serviceJourneyWithFlexibleProperties = NETEX_FRAGMENT
      .replace("${BOOK_WHEN}", "")
      .replace("${BOOKING_ARRANGEMENT}", "")
      .replace(
        "${FLEXIBLE_PROPERTIES}",
        "<FlexibleServiceProperties><BookWhen>advanceAndDayOfTravel</BookWhen></FlexibleServiceProperties>"
      );
    XPathRuleValidationContext xpathRuleValidationContext = TestValidationContextBuilder
      .ofNetexFragment(serviceJourneyWithFlexibleProperties)
      .build();

    List<ValidationIssue> validationIssues =
      validateMandatoryBookingWhenOrMinimumBookingPeriodProperty.validate(
        xpathRuleValidationContext
      );
    Assertions.assertTrue(validationIssues.isEmpty());
  }
}

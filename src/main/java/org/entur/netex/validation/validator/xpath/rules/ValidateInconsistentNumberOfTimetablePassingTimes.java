package org.entur.netex.validation.validator.xpath.rules;

import org.entur.netex.validation.validator.Severity;

/**
 * Validate that the number of passing times in a ServiceJourney matches the number of StopPoints in the JourneyPattern.
 */
public class ValidateInconsistentNumberOfTimetablePassingTimes
  extends ValidateNotExist {

  static final String CODE_SERVICE_JOURNEY_15 = "SERVICE_JOURNEY_15";

  public ValidateInconsistentNumberOfTimetablePassingTimes() {
    super(
      "for $a in vehicleJourneys/ServiceJourney return if(count(//ServiceFrame/journeyPatterns/*[@id = $a/JourneyPatternRef/@ref]/pointsInSequence/StopPointInJourneyPattern) != count($a/passingTimes/TimetabledPassingTime)) then $a else ()",
      CODE_SERVICE_JOURNEY_15,
      "ServiceJourney missing some passing times",
      "ServiceJourney does not specify passing time for all StopPointInJourneyPattern",
      Severity.ERROR
    );
  }
}

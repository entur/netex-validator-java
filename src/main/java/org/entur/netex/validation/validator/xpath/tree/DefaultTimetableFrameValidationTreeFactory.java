package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedBookingAccessProperty;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedBookingMethodProperty;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedBookingWhenProperty;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedBuyWhenProperty;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedFlexibleServiceType;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedTransportModeOnServiceJourney;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedTransportSubModeOnServiceJourney;
import org.entur.netex.validation.validator.xpath.rules.ValidateAtLeastOne;
import org.entur.netex.validation.validator.xpath.rules.ValidateDuplicatedTimetabledPassingTimeId;
import org.entur.netex.validation.validator.xpath.rules.ValidateInconsistentNumberOfTimetablePassingTimes;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

/**
 * Construct a validation tree builder for TimetableFrames.
 */
public class DefaultTimetableFrameValidationTreeFactory
  implements ValidationTreeFactory {

  public static final String CODE_SERVICE_JOURNEY_1 = "SERVICE_JOURNEY_1";
  public static final String CODE_SERVICE_JOURNEY_2 = "SERVICE_JOURNEY_2";
  public static final String CODE_SERVICE_JOURNEY_3 = "SERVICE_JOURNEY_3";
  public static final String CODE_SERVICE_JOURNEY_4 = "SERVICE_JOURNEY_4";
  public static final String CODE_SERVICE_JOURNEY_5 = "SERVICE_JOURNEY_5";
  public static final String CODE_SERVICE_JOURNEY_6 = "SERVICE_JOURNEY_6";
  public static final String CODE_SERVICE_JOURNEY_7 = "SERVICE_JOURNEY_7";
  public static final String CODE_SERVICE_JOURNEY_8 = "SERVICE_JOURNEY_8";
  public static final String CODE_SERVICE_JOURNEY_9 = "SERVICE_JOURNEY_9";
  public static final String CODE_SERVICE_JOURNEY_10 = "SERVICE_JOURNEY_10";
  public static final String CODE_SERVICE_JOURNEY_11 = "SERVICE_JOURNEY_11";
  public static final String CODE_SERVICE_JOURNEY_12 = "SERVICE_JOURNEY_12";
  public static final String CODE_SERVICE_JOURNEY_13 = "SERVICE_JOURNEY_13";
  public static final String CODE_SERVICE_JOURNEY_14 = "SERVICE_JOURNEY_14";
  public static final String CODE_SERVICE_JOURNEY_16 = "SERVICE_JOURNEY_16";
  public static final String CODE_DATED_SERVICE_JOURNEY_1 =
    "DATED_SERVICE_JOURNEY_1";
  public static final String CODE_DATED_SERVICE_JOURNEY_2 =
    "DATED_SERVICE_JOURNEY_2";
  public static final String CODE_DATED_SERVICE_JOURNEY_3 =
    "DATED_SERVICE_JOURNEY_3";
  public static final String CODE_DATED_SERVICE_JOURNEY_4 =
    "DATED_SERVICE_JOURNEY_4";
  public static final String CODE_DATED_SERVICE_JOURNEY_5 =
    "DATED_SERVICE_JOURNEY_5";
  public static final String CODE_DEAD_RUN_1 = "DEAD_RUN_1";
  public static final String CODE_DEAD_RUN_2 = "DEAD_RUN_2";
  public static final String CODE_DEAD_RUN_3 = "DEAD_RUN_3";
  public static final String CODE_FLEXIBLE_SERVICE_1 = "FLEXIBLE_SERVICE_1";
  public static final String CODE_FLEXIBLE_SERVICE_2 = "FLEXIBLE_SERVICE_2";
  public static final String CODE_FLEXIBLE_SERVICE_3 = "FLEXIBLE_SERVICE_3";
  public static final String CODE_FLEXIBLE_SERVICE_4 = "FLEXIBLE_SERVICE_4";
  public static final String CODE_INTERCHANGE_1 = "INTERCHANGE_1";
  public static final String CODE_INTERCHANGE_2 = "INTERCHANGE_2";
  public static final String CODE_INTERCHANGE_3 = "INTERCHANGE_3";

  @Override
  public ValidationTreeBuilder builder() {
    return new ValidationTreeBuilder("Timetable Frame", "TimetableFrame")
      .withRuleForLineFile(
        new ValidateAtLeastOne(
          "vehicleJourneys/ServiceJourney",
          CODE_SERVICE_JOURNEY_1,
          "ServiceJourney must exist",
          "There should be at least one ServiceJourney",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney/calls",
          CODE_SERVICE_JOURNEY_2,
          "ServiceJourney illegal element Call",
          "Element Call not allowed",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(new ValidateAllowedTransportModeOnServiceJourney())
      .withRuleForLineFile(
        new ValidateAllowedTransportSubModeOnServiceJourney()
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney[not(passingTimes)]",
          CODE_SERVICE_JOURNEY_3,
          "ServiceJourney missing element PassingTimes",
          "The ServiceJourney does not specify any TimetabledPassingTimes",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[not(DepartureTime or EarliestDepartureTime) and not(ArrivalTime or LatestArrivalTime)]",
          CODE_SERVICE_JOURNEY_4,
          "ServiceJourney missing arrival and departure",
          "TimetabledPassingTime contains neither DepartureTime/EarliestDepartureTime nor ArrivalTime/LatestArrivalTime",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney[not(passingTimes/TimetabledPassingTime[1]/DepartureTime) and not(passingTimes/TimetabledPassingTime[1]/EarliestDepartureTime)]",
          CODE_SERVICE_JOURNEY_5,
          "ServiceJourney missing departure times",
          "All TimetabledPassingTime except last call must have DepartureTime",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney[count(passingTimes/TimetabledPassingTime[last()]/ArrivalTime) = 0 and count(passingTimes/TimetabledPassingTime[last()]/LatestArrivalTime) = 0]",
          CODE_SERVICE_JOURNEY_6,
          "ServiceJourney missing arrival time for last stop",
          "Last TimetabledPassingTime must have ArrivalTime",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[DepartureTime = ArrivalTime]",
          CODE_SERVICE_JOURNEY_7,
          "ServiceJourney identical arrival and departure",
          "ArrivalTime is identical to DepartureTime",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[not(@id)]",
          CODE_SERVICE_JOURNEY_8,
          "ServiceJourney missing id on TimetabledPassingTime",
          "Missing id on TimetabledPassingTime",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[not(@version)]",
          CODE_SERVICE_JOURNEY_9,
          "ServiceJourney missing version on TimetabledPassingTime",
          "Missing version on TimetabledPassingTime",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(new ValidateDuplicatedTimetabledPassingTimeId())
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney[not(JourneyPatternRef)]",
          CODE_SERVICE_JOURNEY_10,
          "ServiceJourney missing reference to JourneyPattern",
          "The ServiceJourney does not refer to a JourneyPattern",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney[(TransportMode and not(TransportSubmode))  or (not(TransportMode) and TransportSubmode)]",
          CODE_SERVICE_JOURNEY_11,
          "ServiceJourney invalid overriding of transport modes",
          "If overriding Line TransportMode or TransportSubmode on a ServiceJourney, both elements must be present",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney[not(OperatorRef) and not(//ServiceFrame/lines/*[self::Line or self::FlexibleLine]/OperatorRef)]",
          CODE_SERVICE_JOURNEY_12,
          "ServiceJourney missing OperatorRef",
          "Missing OperatorRef on ServiceJourney (not defined on Line)",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney[not(dayTypes/DayTypeRef) and not(@id=//TimetableFrame/vehicleJourneys/DatedServiceJourney/ServiceJourneyRef/@ref)]",
          CODE_SERVICE_JOURNEY_13,
          "ServiceJourney missing reference to calendar data",
          "The ServiceJourney does not refer to DayTypes nor DatedServiceJourneys",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney[dayTypes/DayTypeRef and @id=//TimetableFrame/vehicleJourneys/DatedServiceJourney/ServiceJourneyRef/@ref]",
          CODE_SERVICE_JOURNEY_14,
          "ServiceJourney duplicated reference to calendar data",
          "The ServiceJourney references both DayTypes and DatedServiceJourneys",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateInconsistentNumberOfTimetablePassingTimes()
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney[@id = preceding-sibling::ServiceJourney/@id]",
          CODE_SERVICE_JOURNEY_16,
          "ServiceJourney multiple versions",
          "ServiceJourney is repeated with a different version",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/DatedServiceJourney[not(OperatingDayRef)]",
          CODE_DATED_SERVICE_JOURNEY_1,
          "DatedServiceJourney missing OperatingDayRef",
          "Missing OperatingDayRef on DatedServiceJourney",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/DatedServiceJourney[not(ServiceJourneyRef)]",
          CODE_DATED_SERVICE_JOURNEY_2,
          "DatedServiceJourney missing ServiceJourneyRef",
          "Missing ServiceJourneyRef on DatedServiceJourney",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/DatedServiceJourney[count(ServiceJourneyRef) > 1]",
          CODE_DATED_SERVICE_JOURNEY_3,
          "DatedServiceJourney multiple ServiceJourneyRef",
          "Multiple ServiceJourneyRef on DatedServiceJourney",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/DatedServiceJourney[@id = preceding-sibling::DatedServiceJourney/@id]",
          CODE_DATED_SERVICE_JOURNEY_4,
          "DatedServiceJourney multiple versions",
          "DatedServiceJourney is repeated with a different version",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/DatedServiceJourney/DatedServiceJourneyRef[@ref = preceding-sibling::DatedServiceJourneyRef/@ref]",
          CODE_DATED_SERVICE_JOURNEY_5,
          "DatedServiceJourney multiple references to the same DatedServiceJourney",
          "Multiple references from a DatedServiceJourney to the same DatedServiceJourney",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/DeadRun[not(passingTimes)]",
          CODE_DEAD_RUN_1,
          "DeadRun missing PassingTime references",
          "The Dead run does not reference passing times",
          Severity.INFO
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/DeadRun[not(JourneyPatternRef)]",
          CODE_DEAD_RUN_2,
          "DeadRun missing JourneyPattern references",
          "The Dead run does not reference a journey pattern",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/DeadRun[not(dayTypes/DayTypeRef)]",
          CODE_DEAD_RUN_3,
          "DeadRun missing DayType references",
          "The Dead run does not reference day types",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney/FlexibleServiceProperties[not(@id)]",
          CODE_FLEXIBLE_SERVICE_1,
          "FlexibleService missing Id on FlexibleServiceProperties",
          "Missing id on FlexibleServiceProperties",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney/FlexibleServiceProperties[not(@version)]",
          CODE_FLEXIBLE_SERVICE_2,
          "FlexibleService missing version on FlexibleServiceProperties",
          "Missing version on FlexibleServiceProperties",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(new ValidateAllowedFlexibleServiceType())
      .withRuleForLineFile(
        new ValidateAllowedBookingWhenProperty(
          "vehicleJourneys/ServiceJourney/FlexibleServiceProperties"
        )
      )
      .withRuleForLineFile(
        new ValidateAllowedBuyWhenProperty(
          "vehicleJourneys/ServiceJourney/FlexibleServiceProperties"
        )
      )
      .withRuleForLineFile(
        new ValidateAllowedBookingMethodProperty(
          "vehicleJourneys/ServiceJourney/FlexibleServiceProperties"
        )
      )
      .withRuleForLineFile(
        new ValidateAllowedBookingAccessProperty(
          "vehicleJourneys/ServiceJourney/FlexibleServiceProperties"
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney/FlexibleServiceProperties[BookWhen and MinimumBookingPeriod]",
          CODE_FLEXIBLE_SERVICE_3,
          "FlexibleService illegal use of both BookWhen and MinimumBookingPeriod",
          "Only one of BookWhen or MinimumBookingPeriod should be specified on FlexibleServiceProperties",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "vehicleJourneys/ServiceJourney/FlexibleServiceProperties[(BookWhen and not(LatestBookingTime)) or (not(BookWhen) and LatestBookingTime)]",
          CODE_FLEXIBLE_SERVICE_4,
          "FlexibleService BookWhen without LatestBookingTime or LatestBookingTime without BookWhen",
          "BookWhen must be used together with LatestBookingTime on FlexibleServiceProperties",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "journeyInterchanges/ServiceJourneyInterchange[Advertised or Planned]",
          CODE_INTERCHANGE_1,
          "Interchange invalid properties",
          "The 'Planned' and 'Advertised' properties of an Interchange should not be specified",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "journeyInterchanges/ServiceJourneyInterchange[Guaranteed='true' and  (MaximumWaitTime='PT0S' or MaximumWaitTime='PT0M') ]",
          CODE_INTERCHANGE_2,
          "Interchange unexpected MaximumWaitTime",
          "Guaranteed Interchange should not have a maximum wait time value of zero",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "journeyInterchanges/ServiceJourneyInterchange[MaximumWaitTime > xs:dayTimeDuration('PT1H')]",
          CODE_INTERCHANGE_3,
          "Interchange excessive MaximumWaitTime",
          "The maximum waiting time after planned departure for the interchange consumer journey (MaximumWaitTime) should not be longer than one hour",
          Severity.WARNING
        )
      );
  }
}

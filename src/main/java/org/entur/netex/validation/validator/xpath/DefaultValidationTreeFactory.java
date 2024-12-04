package org.entur.netex.validation.validator.xpath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.rules.*;

/**
 * Build the tree of XPath validation rules.
 */
public class DefaultValidationTreeFactory implements ValidationTreeFactory {

  public static final String SITE_FRAME = "SiteFrame";
  public static final String RESOURCE_FRAME = "ResourceFrame";

  @Override
  public ValidationTree buildValidationTree() {
    ValidationTree validationTree = new ValidationTree(
      "PublicationDelivery",
      "/"
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "PublicationDelivery//*[@version != 'any' and number(@version) != number(@version)]",
        "VERSION_NON_NUMERIC",
        "Non-numeric NeTEx version",
        "Non-numeric NeTEx version",
        Severity.WARNING
      )
    );
    validationTree.addSubTree(getCommonFileValidationTree());
    validationTree.addSubTree(getLineFileValidationTree());
    return validationTree;
  }

  protected ValidationTree getCommonFileValidationTree() {
    ValidationTree commonFileValidationTree = new ValidationTree(
      "Common file",
      "/",
      XPathRuleValidationContext::isCommonFile
    );
    commonFileValidationTree.addSubTree(
      getCompositeFrameValidationTreeForCommonFile()
    );
    commonFileValidationTree.addSubTree(
      getSingleFramesValidationTreeForCommonFile()
    );

    return commonFileValidationTree;
  }

  protected ValidationTree getSingleFramesValidationTreeForCommonFile() {
    ValidationTree validationTree = new ValidationTree(
      "Single frames in common file",
      "PublicationDelivery/dataObjects",
      validationContext ->
        validationContext
          .getNetexXMLParser()
          .selectNodeSet("CompositeFrame", validationContext.getXmlNode())
          .isEmpty()
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        SITE_FRAME,
        "SITE_FRAME_IN_COMMON_FILE",
        "CompositeFrame unexpected SiteFrame",
        "Unexpected element SiteFrame. It will be ignored",
        Severity.WARNING
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "TimetableFrame",
        "TIMETABLE_FRAME_IN_COMMON_FILE",
        "TimetableFrame illegal in Common file",
        "Timetable frame not allowed in common files",
        Severity.ERROR
      )
    );

    validationTree.addValidationRule(
      new ValidateAtLeastOne(
        "ServiceFrame[validityConditions] | ServiceCalendarFrame[validityConditions]",
        "VALIDITY_CONDITIONS_IN_COMMON_FILE_1",
        "ValidityConditions missing in ServiceFrame or ServiceCalendarFrame",
        "Neither ServiceFrame nor ServiceCalendarFrame defines ValidityConditions",
        Severity.ERROR
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "ResourceFrame[not(validityConditions) and count(//ResourceFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_COMMON_FILE_2",
        "ValidityConditions missing in ResourceFrames",
        "Multiple ResourceFrames without validity conditions",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "ServiceFrame[not(validityConditions) and count(//ServiceFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_COMMON_FILE_3",
        "ValidityConditions missing in ServiceFrames",
        "Multiple ServiceFrames without validity conditions",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "ServiceCalendarFrame[not(validityConditions) and count(//ServiceCalendarFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_COMMON_FILE_4",
        "ValidityConditions missing in ServiceCalendarFrames",
        "Multiple ServiceCalendarFrames without validity conditions",
        Severity.ERROR
      )
    );

    validationTree.addSubTree(getResourceFrameValidationTree(RESOURCE_FRAME));
    validationTree.addSubTree(
      getServiceFrameValidationTreeForCommonFile("ServiceFrame")
    );
    validationTree.addSubTree(
      getServiceCalendarFrameValidationTree("ServiceCalendarFrame")
    );
    validationTree.addSubTree(
      getVehicleScheduleFrameValidationTree("VehicleScheduleFrame")
    );
    validationTree.addSubTree(
      getSiteFrameValidationTreeForCommonFile(SITE_FRAME)
    );

    return validationTree;
  }

  protected ValidationTree getCompositeFrameValidationTreeForCommonFile() {
    ValidationTree compositeFrameValidationTree = new ValidationTree(
      "Composite frame in common file",
      "PublicationDelivery/dataObjects/CompositeFrame"
    );

    compositeFrameValidationTree.addValidationRules(
      getCompositeFrameBaseValidationRules()
    );
    compositeFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "frames/TimetableFrame",
        "COMPOSITE_TIMETABLE_FRAME_IN_COMMON_FILE",
        "CompositeFrame illegal Timetable",
        "Timetable frame not allowed in common files",
        Severity.ERROR
      )
    );

    compositeFrameValidationTree.addSubTree(
      getResourceFrameValidationTree("frames/ResourceFrame")
    );
    compositeFrameValidationTree.addSubTree(
      getServiceCalendarFrameValidationTree("frames/ServiceCalendarFrame")
    );
    compositeFrameValidationTree.addSubTree(
      getVehicleScheduleFrameValidationTree("frames/VehicleScheduleFrame")
    );
    compositeFrameValidationTree.addSubTree(
      getServiceFrameValidationTreeForCommonFile("frames/ServiceFrame")
    );
    compositeFrameValidationTree.addSubTree(
      getSiteFrameValidationTreeForCommonFile("frames/SiteFrame")
    );

    return compositeFrameValidationTree;
  }

  protected ValidationTree getServiceFrameValidationTreeForCommonFile(
    String path
  ) {
    ValidationTree serviceFrameValidationTree = new ValidationTree(
      "Service frame in common file",
      path
    );
    serviceFrameValidationTree.addValidationRules(
      getServiceFrameBaseValidationRules()
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/Line",
        "SERVICE_FRAME_IN_COMMON_FILE_1",
        "ServiceFrame unexpected element Line",
        "Line not allowed in common files",
        Severity.ERROR
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "routes/Route",
        "SERVICE_FRAME_IN_COMMON_FILE_2",
        "ServiceFrame unexpected element Route",
        "Route not allowed in common files",
        Severity.ERROR
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern | journeyPatterns/ServiceJourneyPattern",
        "SERVICE_FRAME_IN_COMMON_FILE_3",
        "ServiceFrame unexpected element JourneyPattern",
        "JourneyPattern not allowed in common files",
        Severity.ERROR
      )
    );

    serviceFrameValidationTree.addSubTree(getNoticesValidationTree());

    return serviceFrameValidationTree;
  }

  protected ValidationTree getSiteFrameValidationTreeForCommonFile(
    String path
  ) {
    return new ValidationTree("Site frame in common file", path);
  }

  protected ValidationTree getLineFileValidationTree() {
    ValidationTree lineFileValidationTree = new ValidationTree(
      "Line file",
      "/",
      Predicate.not(XPathRuleValidationContext::isCommonFile)
    );
    lineFileValidationTree.addSubTree(
      getCompositeFrameValidationTreeForLineFile()
    );
    lineFileValidationTree.addSubTree(
      getSingleFramesValidationTreeForLineFile()
    );
    return lineFileValidationTree;
  }

  protected ValidationTree getCompositeFrameValidationTreeForLineFile() {
    ValidationTree compositeFrameValidationTree = new ValidationTree(
      "Composite frame in line file",
      "PublicationDelivery/dataObjects/CompositeFrame"
    );

    compositeFrameValidationTree.addValidationRules(
      getCompositeFrameBaseValidationRules()
    );

    compositeFrameValidationTree.addValidationRule(
      new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty("frames/")
    );
    compositeFrameValidationTree.addValidationRule(
      new ValidateMandatoryBookingProperty("BookingMethods", "frames/")
    );
    compositeFrameValidationTree.addValidationRule(
      new ValidateMandatoryBookingProperty("BookingContact", "frames/")
    );

    compositeFrameValidationTree.addSubTree(
      getResourceFrameValidationTree("frames/ResourceFrame")
    );
    compositeFrameValidationTree.addSubTree(
      getServiceCalendarFrameValidationTree("frames/ServiceCalendarFrame")
    );
    compositeFrameValidationTree.addSubTree(
      getVehicleScheduleFrameValidationTree("frames/VehicleScheduleFrame")
    );

    compositeFrameValidationTree.addSubTree(
      getServiceFrameValidationTreeForLineFile("frames/ServiceFrame")
    );
    compositeFrameValidationTree.addSubTree(
      getTimetableFrameValidationTree("frames/TimetableFrame")
    );

    return compositeFrameValidationTree;
  }

  protected ValidationTree getSingleFramesValidationTreeForLineFile() {
    ValidationTree validationTree = new ValidationTree(
      "Single frames in line file",
      "PublicationDelivery/dataObjects",
      validationContext ->
        validationContext
          .getNetexXMLParser()
          .selectNodeSet("CompositeFrame", validationContext.getXmlNode())
          .isEmpty()
    );

    validationTree.addValidationRule(
      new ValidateExactlyOne(
        RESOURCE_FRAME,
        "RESOURCE_FRAME_IN_LINE_FILE",
        "ResourceFrame must be exactly one",
        "Exactly one ResourceFrame should be present",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        SITE_FRAME,
        "SITE_FRAME_IN_LINE_FILE",
        "SiteFrame unexpected SiteFrame in Line file",
        "Unexpected element SiteFrame. It will be ignored",
        Severity.WARNING
      )
    );
    validationTree.addValidationRule(
      new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty("")
    );
    validationTree.addValidationRule(
      new ValidateMandatoryBookingProperty("BookingMethods", "frames/")
    );
    validationTree.addValidationRule(
      new ValidateMandatoryBookingProperty("BookingContact", "frames/")
    );

    validationTree.addValidationRule(
      new ValidateAtLeastOne(
        "ServiceFrame[validityConditions] | ServiceCalendarFrame[validityConditions] | TimetableFrame[validityConditions]",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_1",
        "ValidityConditions missing in all frames",
        "Neither ServiceFrame, ServiceCalendarFrame nor TimetableFrame defines ValidityConditions",
        Severity.ERROR
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "ServiceFrame[not(validityConditions) and count(//ServiceFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_2",
        "ValidityConditions missing in ServiceFrames",
        "Multiple frames of same type without validity conditions",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "ServiceCalendarFrame[not(validityConditions) and count(//ServiceCalendarFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_3",
        "ValidityConditions missing in ServiceCalendarFrames",
        "Multiple frames of same type without validity conditions",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "TimetableFrame[not(validityConditions) and count(//TimetableFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_4",
        "ValidityConditions missing in TimeTableFrames",
        "Multiple frames of same type without validity conditions",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "VehicleScheduleFrame[not(validityConditions) and count(//VehicleScheduleFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_5",
        "ValidityConditions missing in VehicleScheduleFrame",
        "Multiple frames of same type without validity conditions",
        Severity.ERROR
      )
    );

    validationTree.addSubTree(getResourceFrameValidationTree(RESOURCE_FRAME));
    validationTree.addSubTree(
      getServiceCalendarFrameValidationTree("ServiceCalendarFrame")
    );
    validationTree.addSubTree(
      getTimetableFrameValidationTree("TimetableFrame")
    );
    validationTree.addSubTree(
      getVehicleScheduleFrameValidationTree("VehicleScheduleFrame")
    );

    validationTree.addSubTree(
      getServiceFrameValidationTreeForLineFile("ServiceFrame")
    );

    return validationTree;
  }

  protected ValidationTree getServiceFrameValidationTreeForLineFile(
    String path
  ) {
    ValidationTree serviceFrameValidationTree = new ValidationTree(
      "Service frame in line file",
      path
    );
    serviceFrameValidationTree.addValidationRules(
      getServiceFrameBaseValidationRules()
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateExactlyOne(
        "lines/*[self::Line or self::FlexibleLine]",
        "LINE_1",
        "Line missing Line or FlexibleLine",
        "There must be either Lines or Flexible Lines",
        Severity.ERROR
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine][not(Name) or normalize-space(Name) = '']",
        "LINE_2",
        "Line missing Name",
        "Missing Name on Line",
        Severity.ERROR
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine][not(PublicCode) or normalize-space(PublicCode) = '']",
        "LINE_3",
        "Line missing PublicCode",
        "Missing PublicCode on Line",
        Severity.WARNING
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine][not(TransportMode)]",
        "LINE_4",
        "Line missing TransportMode",
        "Missing TransportMode on Line",
        Severity.ERROR
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine][not(TransportSubmode)]",
        "LINE_5",
        "Line missing TransportSubmode",
        "Missing TransportSubmode on Line",
        Severity.WARNING
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine]/routes/Route",
        "LINE_6",
        "Line with incorrect use of Route",
        "Routes should not be defined within a Line or FlexibleLine",
        Severity.ERROR
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine][not(RepresentedByGroupRef)]",
        "LINE_7",
        "Line missing Network or GroupOfLines",
        "A Line must refer to a GroupOfLines or a Network through element RepresentedByGroupRef",
        Severity.ERROR
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine]/*[self::Presentation or self::AlternativePresentation]/*[self::Colour or self::TextColour][text()][string-length(text())!=6]",
        "LINE_8",
        "Invalid color coding length on Presentation",
        "Line colour should be encoded with 6 hexadecimal digits",
        Severity.WARNING
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine]/*[self::Presentation or self::AlternativePresentation]/*[self::Colour or self::TextColour][text()][not(matches(text(),'[0-9A-Fa-f]{6}'))]",
        "LINE_9",
        "Invalid color coding value on Presentation",
        "Line colour should be encoded with valid hexadecimal digits",
        Severity.WARNING
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateAllowedTransportModeOnLine()
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateAllowedTransportSubModeOnLine()
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/FlexibleLine[not(FlexibleLineType)]",
        "FLEXIBLE_LINE_1",
        "FlexibleLine missing FlexibleLineType",
        "Missing FlexibleLineType on FlexibleLine",
        Severity.ERROR
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/FlexibleLine[BookWhen and MinimumBookingPeriod]",
        "FLEXIBLE_LINE_10",
        "FlexibleLine illegal use of both BookWhen and MinimumBookingPeriod",
        "Only one of BookWhen or MinimumBookingPeriod should be specified on FlexibleLine",
        Severity.WARNING
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/FlexibleLine[(BookWhen and not(LatestBookingTime)) or (not(BookWhen) and LatestBookingTime)]",
        "FLEXIBLE_LINE_11",
        "FlexibleLine BookWhen without LatestBookingTime or LatestBookingTime without BookWhen",
        "BookWhen must be used together with LatestBookingTime on FlexibleLine",
        Severity.WARNING
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateAllowedFlexibleLineType()
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateAllowedBookingWhenProperty("lines/FlexibleLine")
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateAllowedBuyWhenProperty("lines/FlexibleLine")
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateAllowedBookingMethodProperty("lines/FlexibleLine")
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateAllowedBookingAccessProperty("lines/FlexibleLine")
    );

    serviceFrameValidationTree.addValidationRule(
      (
        new ValidateAtLeastOne(
          "routes/Route",
          "ROUTE_1",
          "Route missing",
          "There should be at least one Route",
          Severity.ERROR
        )
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "routes/Route[not(Name) or normalize-space(Name) = '']",
        "ROUTE_2",
        "Route missing Name",
        "Missing Name on Route",
        Severity.ERROR
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "routes/Route[not(LineRef) and not(FlexibleLineRef)]",
        "ROUTE_3",
        "Route missing LineRef",
        "Missing lineRef on Route",
        Severity.ERROR
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "routes/Route[not(pointsInSequence)]",
        "ROUTE_4",
        "Route missing pointsInSequence",
        "Missing pointsInSequence on Route",
        Severity.ERROR
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "routes/Route/DirectionRef",
        "ROUTE_5",
        "Route illegal DirectionRef",
        "DirectionRef not allowed on Route (use DirectionType)",
        Severity.WARNING
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "routes/Route/pointsInSequence/PointOnRoute[@order = preceding-sibling::PointOnRoute/@order]",
        "ROUTE_6",
        "Route duplicated order",
        "Several points on route have the same order",
        Severity.WARNING
      )
    );

    serviceFrameValidationTree.addValidationRule(
      (
        new ValidateNotExist(
          "journeyPatterns/ServiceJourneyPattern",
          "JOURNEY_PATTERN_1",
          "JourneyPattern illegal element ServiceJourneyPattern",
          "ServiceJourneyPattern not allowed",
          Severity.ERROR
        )
      )
    );
    serviceFrameValidationTree.addValidationRule(
      (
        new ValidateAtLeastOne(
          "journeyPatterns/JourneyPattern",
          "JOURNEY_PATTERN_2",
          "JourneyPattern missing JourneyPattern",
          "No JourneyPattern defined in the Service Frame",
          Severity.ERROR
        )
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern[not(RouteRef)]",
        "JOURNEY_PATTERN_3",
        "JourneyPattern missing RouteRef",
        "Missing RouteRef on JourneyPattern",
        Severity.ERROR
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern[1][not(DestinationDisplayRef)]",
        "JOURNEY_PATTERN_4",
        "JourneyPattern missing DestinationDisplayRef on first stop point",
        "Missing DestinationDisplayRef on first StopPointInJourneyPattern",
        Severity.ERROR
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern[last()][DestinationDisplayRef]",
        "JOURNEY_PATTERN_5",
        "JourneyPattern illegal DestinationDisplayRef on last stop point",
        "DestinationDisplayRef not allowed on last StopPointInJourneyPattern",
        Severity.WARNING
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern[ForAlighting = 'false' and ForBoarding = 'false']",
        "JOURNEY_PATTERN_6",
        "JourneyPattern stop point without boarding or alighting",
        "StopPointInJourneyPattern neither allows boarding nor alighting",
        Severity.WARNING
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern[DestinationDisplayRef/@ref = preceding-sibling::StopPointInJourneyPattern[1]/DestinationDisplayRef/@ref and number(@order) >  number(preceding-sibling::StopPointInJourneyPattern[1]/@order)]",
        "JOURNEY_PATTERN_7",
        "JourneyPattern illegal repetition of DestinationDisplay",
        "StopPointInJourneyPattern declares reference to the same DestinationDisplay as previous StopPointInJourneyPattern",
        Severity.ERROR
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateAllowedBookingWhenProperty(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateAllowedBuyWhenProperty(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateAllowedBookingMethodProperty(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateAllowedBookingAccessProperty(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements"
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements[BookWhen and MinimumBookingPeriod]",
        "JOURNEY_PATTERN_8",
        "JourneyPattern  illegal use of both BookWhen and MinimumBookingPeriod",
        "Only one of BookWhen or MinimumBookingPeriod should be specified on StopPointInJourneyPattern",
        Severity.WARNING
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements[(BookWhen and not(LatestBookingTime)) or (not(BookWhen) and LatestBookingTime)]",
        "JOURNEY_PATTERN_9",
        "JourneyPattern  BookWhen without LatestBookingTime or LatestBookingTime without BookWhen",
        "BookWhen must be used together with LatestBookingTime on StopPointInJourneyPattern",
        Severity.WARNING
      )
    );

    serviceFrameValidationTree.addSubTree(getNoticesValidationTree());
    serviceFrameValidationTree.addSubTree(getNoticeAssignmentsValidationTree());
    return serviceFrameValidationTree;
  }

  protected ValidationTree getTimetableFrameValidationTree(String path) {
    ValidationTree validationTree = new ValidationTree("Timetable frame", path);

    validationTree.addValidationRule(
      new ValidateAtLeastOne(
        "vehicleJourneys/ServiceJourney",
        "SERVICE_JOURNEY_1",
        "ServiceJourney must exist",
        "There should be at least one ServiceJourney",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/calls",
        "SERVICE_JOURNEY_2",
        "ServiceJourney illegal element Call",
        "Element Call not allowed",
        Severity.ERROR
      )
    );

    validationTree.addValidationRule(
      new ValidateAllowedTransportModeOnServiceJourney()
    );
    validationTree.addValidationRule(
      new ValidateAllowedTransportSubModeOnServiceJourney()
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[not(passingTimes)]",
        "SERVICE_JOURNEY_3",
        "ServiceJourney missing element PassingTimes",
        "The ServiceJourney does not specify any TimetabledPassingTimes",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[not(DepartureTime or EarliestDepartureTime) and not(ArrivalTime or LatestArrivalTime)]",
        "SERVICE_JOURNEY_4",
        "ServiceJourney missing arrival and departure",
        "TimetabledPassingTime contains neither DepartureTime/EarliestDepartureTime nor ArrivalTime/LatestArrivalTime",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[not(passingTimes/TimetabledPassingTime[1]/DepartureTime) and not(passingTimes/TimetabledPassingTime[1]/EarliestDepartureTime)]",
        "SERVICE_JOURNEY_5",
        "ServiceJourney missing departure times",
        "All TimetabledPassingTime except last call must have DepartureTime",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[count(passingTimes/TimetabledPassingTime[last()]/ArrivalTime) = 0 and count(passingTimes/TimetabledPassingTime[last()]/LatestArrivalTime) = 0]",
        "SERVICE_JOURNEY_6",
        "ServiceJourney missing arrival time for last stop",
        "Last TimetabledPassingTime must have ArrivalTime",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[DepartureTime = ArrivalTime]",
        "SERVICE_JOURNEY_7",
        "ServiceJourney identical arrival and departure",
        "ArrivalTime is identical to DepartureTime",
              Severity.WARNING
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[not(@id)]",
        "SERVICE_JOURNEY_8",
        "ServiceJourney missing id on TimetabledPassingTime",
        "Missing id on TimetabledPassingTime",
              Severity.WARNING
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[not(@version)]",
        "SERVICE_JOURNEY_9",
        "ServiceJourney missing version on TimetabledPassingTime",
        "Missing version on TimetabledPassingTime",
              Severity.WARNING
      )
    );

    validationTree.addValidationRule(
      new ValidateDuplicatedTimetabledPassingTimeId("")
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[not(JourneyPatternRef)]",
        "SERVICE_JOURNEY_10",
        "ServiceJourney missing reference to JourneyPattern",
        "The ServiceJourney does not refer to a JourneyPattern",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[(TransportMode and not(TransportSubmode))  or (not(TransportMode) and TransportSubmode)]",
        "SERVICE_JOURNEY_11",
        "ServiceJourney invalid overriding of transport modes",
        "If overriding Line TransportMode or TransportSubmode on a ServiceJourney, both elements must be present",
              Severity.WARNING
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[not(OperatorRef) and not(//ServiceFrame/lines/*[self::Line or self::FlexibleLine]/OperatorRef)]",
        "SERVICE_JOURNEY_12",
        "ServiceJourney missing OperatorRef",
        "Missing OperatorRef on ServiceJourney (not defined on Line)",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[not(dayTypes/DayTypeRef) and not(@id=//TimetableFrame/vehicleJourneys/DatedServiceJourney/ServiceJourneyRef/@ref)]",
        "SERVICE_JOURNEY_13",
        "ServiceJourney missing reference to calendar data",
        "The ServiceJourney does not refer to DayTypes nor DatedServiceJourneys",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[dayTypes/DayTypeRef and @id=//TimetableFrame/vehicleJourneys/DatedServiceJourney/ServiceJourneyRef/@ref]",
        "SERVICE_JOURNEY_14",
        "ServiceJourney duplicated reference to calendar data",
        "The ServiceJourney references both DayTypes and DatedServiceJourneys",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "for $a in vehicleJourneys/ServiceJourney return if(count(//ServiceFrame/journeyPatterns/*[@id = $a/JourneyPatternRef/@ref]/pointsInSequence/StopPointInJourneyPattern) != count($a/passingTimes/TimetabledPassingTime)) then $a else ()",
        "SERVICE_JOURNEY_15",
        "ServiceJourney missing some passing times",
        "ServiceJourney does not specify passing time for all StopPointInJourneyPattern",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[@id = preceding-sibling::ServiceJourney/@id]",
        "SERVICE_JOURNEY_16",
        "ServiceJourney multiple versions",
        "ServiceJourney is repeated with a different version",
              Severity.WARNING
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DatedServiceJourney[not(OperatingDayRef)]",
        "DATED_SERVICE_JOURNEY_1",
        "DatedServiceJourney missing OperatingDayRef",
        "Missing OperatingDayRef on DatedServiceJourney",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DatedServiceJourney[not(ServiceJourneyRef)]",
        "DATED_SERVICE_JOURNEY_2",
        "DatedServiceJourney missing ServiceJourneyRef",
        "Missing ServiceJourneyRef on DatedServiceJourney",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DatedServiceJourney[count(ServiceJourneyRef) > 1]",
        "DATED_SERVICE_JOURNEY_3",
        "DatedServiceJourney multiple ServiceJourneyRef",
        "Multiple ServiceJourneyRef on DatedServiceJourney",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DatedServiceJourney[@id = preceding-sibling::DatedServiceJourney/@id]",
        "DATED_SERVICE_JOURNEY_4",
        "DatedServiceJourney multiple versions",
        "DatedServiceJourney is repeated with a different version",
              Severity.WARNING
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DatedServiceJourney/DatedServiceJourneyRef[@ref = preceding-sibling::DatedServiceJourneyRef/@ref]",
        "DATED_SERVICE_JOURNEY_5",
        "DatedServiceJourney multiple references to the same DatedServiceJourney",
        "Multiple references from a DatedServiceJourney to the same DatedServiceJourney",
              Severity.ERROR
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DeadRun[not(passingTimes)]",
        "DEAD_RUN_1",
        "DeadRun missing PassingTime references",
        "The Dead run does not reference passing times",
              Severity.INFO
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DeadRun[not(JourneyPatternRef)]",
        "DEAD_RUN_2",
        "DeadRun missing JourneyPattern references",
        "The Dead run does not reference a journey pattern",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DeadRun[not(dayTypes/DayTypeRef)]",
        "DEAD_RUN_3",
        "DeadRun missing DayType references",
        "The Dead run does not reference day types",
              Severity.ERROR
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/FlexibleServiceProperties[not(@id)]",
        "FLEXIBLE_SERVICE_1",
        "FlexibleService missing Id on FlexibleServiceProperties",
        "Missing id on FlexibleServiceProperties",
              Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/FlexibleServiceProperties[not(@version)]",
        "FLEXIBLE_SERVICE_2",
        "FlexibleService missing version on FlexibleServiceProperties",
        "Missing version on FlexibleServiceProperties",
              Severity.ERROR
      )
    );

    validationTree.addValidationRule(new ValidateAllowedFlexibleServiceType());
    validationTree.addValidationRule(
      new ValidateAllowedBookingWhenProperty(
        "vehicleJourneys/ServiceJourney/FlexibleServiceProperties"
      )
    );
    validationTree.addValidationRule(
      new ValidateAllowedBuyWhenProperty(
        "vehicleJourneys/ServiceJourney/FlexibleServiceProperties"
      )
    );
    validationTree.addValidationRule(
      new ValidateAllowedBookingMethodProperty(
        "vehicleJourneys/ServiceJourney/FlexibleServiceProperties"
      )
    );
    validationTree.addValidationRule(
      new ValidateAllowedBookingAccessProperty(
        "vehicleJourneys/ServiceJourney/FlexibleServiceProperties"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/FlexibleServiceProperties[BookWhen and MinimumBookingPeriod]",
        "FLEXIBLE_SERVICE_3",
        "FlexibleService illegal use of both BookWhen and MinimumBookingPeriod",
        "Only one of BookWhen or MinimumBookingPeriod should be specified on FlexibleServiceProperties",
              Severity.WARNING
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/FlexibleServiceProperties[(BookWhen and not(LatestBookingTime)) or (not(BookWhen) and LatestBookingTime)]",
        "FLEXIBLE_SERVICE_4",
        "FlexibleService BookWhen without LatestBookingTime or LatestBookingTime without BookWhen",
        "BookWhen must be used together with LatestBookingTime on FlexibleServiceProperties",
              Severity.WARNING
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "journeyInterchanges/ServiceJourneyInterchange[Advertised or Planned]",
        "INTERCHANGE_1",
        "Interchange invalid properties",
        "The 'Planned' and 'Advertised' properties of an Interchange should not be specified",
              Severity.WARNING
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "journeyInterchanges/ServiceJourneyInterchange[Guaranteed='true' and  (MaximumWaitTime='PT0S' or MaximumWaitTime='PT0M') ]",
        "INTERCHANGE_2",
        "Interchange unexpected MaximumWaitTime",
        "Guaranteed Interchange should not have a maximum wait time value of zero",
              Severity.WARNING
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "journeyInterchanges/ServiceJourneyInterchange[MaximumWaitTime > xs:dayTimeDuration('PT1H')]",
        "INTERCHANGE_3",
        "Interchange excessive MaximumWaitTime",
        "The maximum waiting time after planned departure for the interchange consumer journey (MaximumWaitTime) should not be longer than one hour",
              Severity.WARNING
      )
    );

    validationTree.addSubTree(getNoticesValidationTree());
    validationTree.addSubTree(getNoticeAssignmentsValidationTree());

    return validationTree;
  }

  /**
   * CompositeFrame validation rules that apply both to Line files and common files.
   *
   */
  protected List<XPathValidationRule> getCompositeFrameBaseValidationRules() {
    List<XPathValidationRule> validationRules = new ArrayList<>();
    validationRules.add(
      new ValidateNotExist(
        "frames/SiteFrame",
        "COMPOSITE_SITE_FRAME_IN_COMMON_FILE",
        "CompositeFrame unexpected SiteFrame",
        "Unexpected element SiteFrame. It will be ignored",
              Severity.WARNING
      )
    );

    validationRules.add(
      new ValidateNotExist(
        ".[not(validityConditions)]",
        "COMPOSITE_FRAME_1",
        "CompositeFrame missing ValidityCondition",
        "A CompositeFrame must define a ValidityCondition valid for all data within the CompositeFrame",
              Severity.ERROR
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "frames//validityConditions",
        "COMPOSITE_FRAME_2",
        "CompositeFrame invalid nested ValidityCondition",
        "ValidityConditions defined inside a frame inside a CompositeFrame",
              Severity.WARNING
      )
    );

    validationRules.add(
      new ValidateNotExist(
        "//ValidBetween[not(FromDate) and not(ToDate)]",
        "COMPOSITE_FRAME_3",
        "CompositeFrame missing ValidBetween",
        "ValidBetween missing either or both of FromDate/ToDate",
              Severity.ERROR
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "//ValidBetween[FromDate and ToDate and ToDate < FromDate]",
        "COMPOSITE_FRAME_4",
        "CompositeFrame invalid ValidBetween",
        "FromDate cannot be after ToDate on ValidBetween",
              Severity.ERROR
      )
    );

    validationRules.add(
      new ValidateNotExist(
        "//AvailabilityCondition[FromDate and ToDate and ToDate < FromDate]",
        "COMPOSITE_FRAME_5",
        "CompositeFrame invalid AvailabilityCondition",
        "FromDate cannot be after ToDate on AvailabilityCondition",
              Severity.ERROR
      )
    );

    validationRules.add(
            new ValidateNotExist(
                    "//AvailabilityCondition[not(FromDate) and not(ToDate)]",
                    "COMPOSITE_FRAME_6",
                    "CompositeFrame missing AvailabilityCondition",
                    "AvailabilityCondition must have either FromDate or ToDate or both present",
                    Severity.ERROR
            )
    );

    return validationRules;
  }

  protected ValidationTree getResourceFrameValidationTree(String path) {
    ValidationTree resourceFrameValidationTree = new ValidationTree(
      "Resource frame",
      path
    );

    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator[not(CompanyNumber) or normalize-space(CompanyNumber) = '']",
        "OPERATOR_1",
        "Operator missing CompanyNumber",
        "Missing CompanyNumber element on Operator",
              Severity.INFO
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator[not(Name) or normalize-space(Name) = '']",
        "OPERATOR_2",
        "Operator missing Name",
        "Missing Name on Operator",
              Severity.ERROR
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator[not(LegalName) or normalize-space(LegalName) = '']",
        "OPERATOR_3",
        "Operator missing LegalName",
        "Missing LegalName element on Operator",
              Severity.INFO
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator[not(ContactDetails)]",
        "OPERATOR_4",
        "Operator missing ContactDetails",
        "Missing ContactDetails element on Operator",
              Severity.WARNING
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator/ContactDetails[(not(Email) or normalize-space(Email) = '') and (not(Phone) or normalize-space(Phone) = '') and (not(Url) or normalize-space(Url) = '')]",
        "OPERATOR_5",
        "Operator missing Url for ContactDetails",
        "At least one of Url, Phone or Email must be defined for ContactDetails on Operator",
              Severity.WARNING
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator[not(CustomerServiceContactDetails)]",
        "OPERATOR_6",
        "Operator missing CustomerServiceContactDetails",
        "Missing CustomerServiceContactDetails element on Operator",
              Severity.WARNING
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator/CustomerServiceContactDetails[not(Url) or normalize-space(Url) = '']",
        "OPERATOR_7",
        "Operator missing Url for CustomerServiceContactDetails",
        "Missing Url element for CustomerServiceContactDetails on Operator",
              Severity.WARNING
      )
    );

    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Authority[not(CompanyNumber) or normalize-space(CompanyNumber) = '']",
        "AUTHORITY_1",
        "Authority missing CompanyNumber",
        "Missing CompanyNumber element on Authority",
              Severity.INFO
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Authority[not(Name) or normalize-space(Name) = '']",
        "AUTHORITY_2",
        "Authority missing Name",
        "Missing Name element on Authority",
              Severity.ERROR
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Authority[not(LegalName) or normalize-space(LegalName) = '']",
        "AUTHORITY_3",
        "Authority missing LegalName",
        "Missing LegalName element on Authority",
              Severity.INFO
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Authority[not(ContactDetails)]",
        "AUTHORITY_4",
        "Authority missing ContactDetails",
        "Missing ContactDetails on Authority",
              Severity.ERROR
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Authority/ContactDetails[not(Url) or not(starts-with(Url, 'http://') or (starts-with(Url, 'https://')) )]",
        "AUTHORITY_5",
        "Authority missing Url for ContactDetails",
        "The Url must be defined for ContactDetails on Authority and it must start with 'http://' or 'https://'",
              Severity.ERROR
      )
    );

    return resourceFrameValidationTree;
  }

  protected ValidationTree getServiceCalendarFrameValidationTree(String path) {
    ValidationTree serviceCalendarFrameValidationTree = new ValidationTree(
      "Service Calendar frame",
      path
    );

    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "//DayType[not(//DayTypeAssignment/DayTypeRef/@ref = @id)]",
        "SERVICE_CALENDAR_1",
        "The DayType is not assigned to any calendar dates or periods"
      )
    );
    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "//ServiceCalendar[not(dayTypes) and not(dayTypeAssignments)]",
        "SERVICE_CALENDAR_2",
        "ServiceCalendar does not contain neither DayTypes nor DayTypeAssignments"
      )
    );
    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "//ServiceCalendar[not(ToDate)]",
        "SERVICE_CALENDAR_3",
        "Missing ToDate on ServiceCalendar"
      )
    );
    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "//ServiceCalendar[not(FromDate)]",
        "SERVICE_CALENDAR_4",
        "Missing FromDate on ServiceCalendar"
      )
    );
    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "//ServiceCalendar[FromDate and ToDate and ToDate < FromDate]",
        "SERVICE_CALENDAR_5",
        "FromDate cannot be after ToDate on ServiceCalendar"
      )
    );

    return serviceCalendarFrameValidationTree;
  }

  protected ValidationTree getVehicleScheduleFrameValidationTree(String path) {
    ValidationTree serviceCalendarFrameValidationTree = new ValidationTree(
      "Vehicle Schedule frame",
      path
    );

    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateAtLeastOne(
        "blocks/Block | blocks/TrainBlock",
        "BLOCK_1",
        "Block missing VehicleScheduleFrame",
        "At least one Block or TrainBlock required in VehicleScheduleFrame",
        Severity.ERROR
      )
    );
    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "blocks/Block[not(journeys)]",
        "BLOCK_2",
        "At least one Journey must be defined for Block"
      )
    );
    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "blocks/Block[not(dayTypes)]",
        "BLOCK_3",
        "At least one DayType must be defined for Block"
      )
    );

    return serviceCalendarFrameValidationTree;
  }

  /**
   * Validation rules that apply both to Line files and Common files.
   *
   */
  protected List<XPathValidationRule> getServiceFrameBaseValidationRules() {
    List<XPathValidationRule> validationRules = new ArrayList<>();
    validationRules.add(
      new ValidateNotExist(
        "Network[not(AuthorityRef)]",
        "NETWORK_1",
        "Missing AuthorityRef on Network"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "Network[not(Name) or normalize-space(Name) = '']",
        "NETWORK_2",
        "Missing Name element on Network"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "Network/groupsOfLines/GroupOfLines[not(Name)  or normalize-space(Name) = '']",
        "NETWORK_3",
        "Missing Name element on GroupOfLines"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "groupsOfLines",
        "SERVICE_FRAME_1",
        "Unexpected element groupsOfLines outside of Network"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "timingPoints",
        "SERVICE_FRAME_2",
        "Unexpected element timingPoints. Content ignored"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "routePoints/RoutePoint[not(projections)]",
        "SERVICE_FRAME_3",
        "Missing Projection on RoutePoint"
      )
    );

    validationRules.add(
      new ValidateNotExist(
        "stopAssignments/PassengerStopAssignment[not(ScheduledStopPointRef)]",
        "PASSENGER_STOP_ASSIGNMENT_1",
        "Missing ScheduledStopPointRef on PassengerStopAssignment"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "stopAssignments/PassengerStopAssignment[not(QuayRef)]",
        "PASSENGER_STOP_ASSIGNMENT_2",
        "Missing QuayRef on PassengerStopAssignment"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "stopAssignments/PassengerStopAssignment[QuayRef/@ref = following-sibling::PassengerStopAssignment/QuayRef/@ref]",
        "PASSENGER_STOP_ASSIGNMENT_3",
        "The same quay is assigned more than once in PassengerStopAssignments"
      )
    );

    validationRules.add(
      new ValidateNotExist(
        "serviceLinks/ServiceLink[not(FromPointRef)]",
        "SERVICE_LINK_1",
        "Missing FromPointRef on ServiceLink"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "serviceLinks/ServiceLink[not(ToPointRef)]",
        "SERVICE_LINK_2",
        "Missing ToPointRef on ServiceLink"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "serviceLinks/ServiceLink/projections/LinkSequenceProjection/g:LineString/g:posList[not(normalize-space(text()))]",
        "SERVICE_LINK_3",
        "Missing projections element on ServiceLink"
      )
    );

    validationRules.add(
      (
        new ValidateNotExist(
          "destinationDisplays/DestinationDisplay[not(FrontText) or normalize-space(FrontText) = '']",
          "DESTINATION_DISPLAY_1",
          "Missing FrontText on DestinationDisplay"
        )
      )
    );
    validationRules.add(
      (
        new ValidateNotExist(
          "destinationDisplays/DestinationDisplay/vias/Via[not(DestinationDisplayRef)]",
          "DESTINATION_DISPLAY_2",
          "Missing DestinationDisplayRef on Via"
        )
      )
    );

    return validationRules;
  }

  protected ValidationTree getNoticesValidationTree() {
    ValidationTree noticesValidationTree = new ValidationTree(
      "Notices",
      "notices"
    );

    noticesValidationTree.addValidationRule(
      new ValidateNotExist(
        "Notice[not(Text) or normalize-space(Text/text()) = '']",
        "NOTICE_1",
        "Missing element Text for Notice"
      )
    );
    noticesValidationTree.addValidationRule(
      new ValidateNotExist(
        "Notice/alternativeTexts/AlternativeText[not(Text) or normalize-space(Text/text()) = '']",
        "NOTICE_2",
        "Missing or empty element Text for Notice Alternative Text"
      )
    );
    noticesValidationTree.addValidationRule(
      new ValidateNotExist(
        "Notice/alternativeTexts/AlternativeText/Text[not(@lang)]",
        "NOTICE_3",
        "Missing element Lang for Notice Alternative Text"
      )
    );
    noticesValidationTree.addValidationRule(
      new ValidateNotExist(
        "Notice/alternativeTexts/AlternativeText[Text/@lang = following-sibling::AlternativeText/Text/@lang or Text/@lang = preceding-sibling::AlternativeText/Text/@lang]",
        "NOTICE_4",
        "The Notice has two Alternative Texts with the same language"
      )
    );

    return noticesValidationTree;
  }

  protected ValidationTree getNoticeAssignmentsValidationTree() {
    ValidationTree noticesAssignmentsValidationTree = new ValidationTree(
      "Notices Assignments",
      "noticeAssignments"
    );
    noticesAssignmentsValidationTree.addValidationRule(
      new ValidateNotExist(
        "NoticeAssignment[for $a in following-sibling::NoticeAssignment return if(NoticeRef/@ref= $a/NoticeRef/@ref and NoticedObjectRef/@ref= $a/NoticedObjectRef/@ref) then $a else ()]",
        "NOTICE_5",
        "The notice is assigned multiple times to the same object"
      )
    );
    noticesAssignmentsValidationTree.addValidationRule(
      new ValidateNotExist(
        "NoticeAssignment[not(NoticedObjectRef)]",
        "NOTICE_6",
        "The notice assignment does not reference an object"
      )
    );
    noticesAssignmentsValidationTree.addValidationRule(
      new ValidateNotExist(
        "NoticeAssignment[not(NoticeRef)]",
        "NOTICE_7",
        "The notice assignment does not reference a notice"
      )
    );

    return noticesAssignmentsValidationTree;
  }

  public static void main(String[] args) {
    DefaultValidationTreeFactory defaultValidationTreeFactory =
      new DefaultValidationTreeFactory();
    System.out.println(
      defaultValidationTreeFactory.buildValidationTree().printRulesList()
    );
  }
}

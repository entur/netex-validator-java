package org.entur.netex.validation.validator.xpath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
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
        "Non-numeric NeTEx version",
        "VERSION_NON_NUMERIC"
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
        "Unexpected element SiteFrame. It will be ignored",
        "SITE_FRAME_IN_COMMON_FILE"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "TimetableFrame",
        "Timetable frame not allowed in common files",
        "TIMETABLE_FRAME_IN_COMMON_FILE"
      )
    );

    validationTree.addValidationRule(
      new ValidateAtLeastOne(
        "ServiceFrame[validityConditions] | ServiceCalendarFrame[validityConditions]",
        "Neither ServiceFrame nor ServiceCalendarFrame defines ValidityConditions",
        "VALIDITY_CONDITIONS_IN_COMMON_FILE_1"
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "ResourceFrame[not(validityConditions) and count(//ResourceFrame) > 1]",
        "Multiple ResourceFrames without validity conditions",
        "VALIDITY_CONDITIONS_IN_COMMON_FILE_2"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "ServiceFrame[not(validityConditions) and count(//ServiceFrame) > 1]",
        "Multiple ServiceFrames without validity conditions",
        "VALIDITY_CONDITIONS_IN_COMMON_FILE_3"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "ServiceCalendarFrame[not(validityConditions) and count(//ServiceCalendarFrame) > 1]",
        "Multiple ServiceCalendarFrames without validity conditions",
        "VALIDITY_CONDITIONS_IN_COMMON_FILE_4"
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
        "Timetable frame not allowed in common files",
        "COMPOSITE_TIMETABLE_FRAME_IN_COMMON_FILE"
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
        "Line not allowed in common files",
        "SERVICE_FRAME_IN_COMMON_FILE_1"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "routes/Route",
        "Route not allowed in common files",
        "SERVICE_FRAME_IN_COMMON_FILE_2"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern | journeyPatterns/ServiceJourneyPattern",
        "JourneyPattern not allowed in common files",
        "SERVICE_FRAME_IN_COMMON_FILE_3"
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
        "Exactly one ResourceFrame should be present",
        "RESOURCE_FRAME_IN_LINE_FILE"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        SITE_FRAME,
        "Unexpected element SiteFrame. It will be ignored",
        "SITE_FRAME_IN_LINE_FILE"
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
        "Neither ServiceFrame, ServiceCalendarFrame nor TimetableFrame defines ValidityConditions",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_1"
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "ServiceFrame[not(validityConditions) and count(//ServiceFrame) > 1]",
        "Multiple frames of same type without validity conditions",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_2"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "ServiceCalendarFrame[not(validityConditions) and count(//ServiceCalendarFrame) > 1]",
        "Multiple frames of same type without validity conditions",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_3"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "TimetableFrame[not(validityConditions) and count(//TimetableFrame) > 1]",
        "Multiple frames of same type without validity conditions",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_4"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "VehicleScheduleFrame[not(validityConditions) and count(//VehicleScheduleFrame) > 1]",
        "Multiple frames of same type without validity conditions",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_5"
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
        "There must be either Lines or Flexible Lines",
        "LINE_1"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine][not(Name) or normalize-space(Name) = '']",
        "Missing Name on Line",
        "LINE_2"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine][not(PublicCode) or normalize-space(PublicCode) = '']",
        "Missing PublicCode on Line",
        "LINE_3"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine][not(TransportMode)]",
        "Missing TransportMode on Line",
        "LINE_4"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine][not(TransportSubmode)]",
        "Missing TransportSubmode on Line",
        "LINE_5"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine]/routes/Route",
        "Routes should not be defined within a Line or FlexibleLine",
        "LINE_6"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine][not(RepresentedByGroupRef)]",
        "A Line must refer to a GroupOfLines or a Network through element RepresentedByGroupRef",
        "LINE_7"
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine]/*[self::Presentation or self::AlternativePresentation]/*[self::Colour or self::TextColour][text()][string-length(text())!=6]",
        "Line colour should be encoded with 6 hexadecimal digits",
        "LINE_8"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/*[self::Line or self::FlexibleLine]/*[self::Presentation or self::AlternativePresentation]/*[self::Colour or self::TextColour][text()][not(matches(text(),'[0-9A-Fa-f]{6}'))]",
        "Line colour should be encoded with valid hexadecimal digits",
        "LINE_9"
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidatedAllowedTransportMode(
        ValidatedAllowedTransportMode.DEFAULT_VALID_TRANSPORT_MODES
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidatedAllowedTransportSubMode()
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/FlexibleLine[not(FlexibleLineType)]",
        "Missing FlexibleLineType on FlexibleLine",
        "FLEXIBLE_LINE_1"
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/FlexibleLine[BookWhen and MinimumBookingPeriod]",
        "Only one of BookWhen or MinimumBookingPeriod should be specified on FlexibleLine",
        "FLEXIBLE_LINE_10"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "lines/FlexibleLine[(BookWhen and not(LatestBookingTime)) or (not(BookWhen) and LatestBookingTime)]",
        "BookWhen must be used together with LatestBookingTime on FlexibleLine",
        "FLEXIBLE_LINE_11"
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
          "There should be at least one Route",
          "ROUTE_1"
        )
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "routes/Route[not(Name) or normalize-space(Name) = '']",
        "Missing Name on Route",
        "ROUTE_2"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "routes/Route[not(LineRef) and not(FlexibleLineRef)]",
        "Missing lineRef on Route",
        "ROUTE_3"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "routes/Route[not(pointsInSequence)]",
        "Missing pointsInSequence on Route",
        "ROUTE_4"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "routes/Route/DirectionRef",
        "DirectionRef not allowed on Route (use DirectionType)",
        "ROUTE_5"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "routes/Route/pointsInSequence/PointOnRoute[@order = preceding-sibling::PointOnRoute/@order]",
        "Several points on route have the same order",
        "ROUTE_6"
      )
    );

    serviceFrameValidationTree.addValidationRule(
      (
        new ValidateNotExist(
          "journeyPatterns/ServiceJourneyPattern",
          "ServiceJourneyPattern not allowed",
          "JOURNEY_PATTERN_1"
        )
      )
    );
    serviceFrameValidationTree.addValidationRule(
      (
        new ValidateAtLeastOne(
          "journeyPatterns/JourneyPattern",
          "No JourneyPattern defined in the Service Frame",
          "JOURNEY_PATTERN_2"
        )
      )
    );

    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern[not(RouteRef)]",
        "Missing RouteRef on JourneyPattern",
        "JOURNEY_PATTERN_3"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern[1][not(DestinationDisplayRef)]",
        "Missing DestinationDisplayRef on first StopPointInJourneyPattern",
        "JOURNEY_PATTERN_4"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern[last()][DestinationDisplayRef]",
        "DestinationDisplayRef not allowed on last StopPointInJourneyPattern",
        "JOURNEY_PATTERN_5"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern[ForAlighting = 'false' and ForBoarding = 'false']",
        "StopPointInJourneyPattern neither allows boarding nor alighting",
        "JOURNEY_PATTERN_6"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern[DestinationDisplayRef/@ref = preceding-sibling::StopPointInJourneyPattern[1]/DestinationDisplayRef/@ref and number(@order) >  number(preceding-sibling::StopPointInJourneyPattern[1]/@order)]",
        "StopPointInJourneyPattern declares reference to the same DestinationDisplay as previous StopPointInJourneyPattern",
        "JOURNEY_PATTERN_7"
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
        "Only one of BookWhen or MinimumBookingPeriod should be specified on StopPointInJourneyPattern",
        "JOURNEY_PATTERN_8"
      )
    );
    serviceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements[(BookWhen and not(LatestBookingTime)) or (not(BookWhen) and LatestBookingTime)]",
        "BookWhen must be used together with LatestBookingTime on StopPointInJourneyPattern",
        "JOURNEY_PATTERN_9"
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
        "There should be at least one ServiceJourney",
        "SERVICE_JOURNEY_1"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/calls",
        "Element Call not allowed",
        "SERVICE_JOURNEY_2"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[not(passingTimes)]",
        "The ServiceJourney does not specify any TimetabledPassingTimes",
        "SERVICE_JOURNEY_3"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[not(DepartureTime or EarliestDepartureTime) and not(ArrivalTime or LatestArrivalTime)]",
        "TimetabledPassingTime contains neither DepartureTime/EarliestDepartureTime nor ArrivalTime/LatestArrivalTime",
        "SERVICE_JOURNEY_4"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[not(passingTimes/TimetabledPassingTime[1]/DepartureTime) and not(passingTimes/TimetabledPassingTime[1]/EarliestDepartureTime)]",
        "All TimetabledPassingTime except last call must have DepartureTime",
        "SERVICE_JOURNEY_5"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[count(passingTimes/TimetabledPassingTime[last()]/ArrivalTime) = 0 and count(passingTimes/TimetabledPassingTime[last()]/LatestArrivalTime) = 0]",
        "Last TimetabledPassingTime must have ArrivalTime",
        "SERVICE_JOURNEY_6"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[DepartureTime = ArrivalTime]",
        "ArrivalTime is identical to DepartureTime",
        "SERVICE_JOURNEY_7"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[not(@id)]",
        "Missing id on TimetabledPassingTime",
        "SERVICE_JOURNEY_8"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[not(@version)]",
        "Missing version on TimetabledPassingTime",
        "SERVICE_JOURNEY_9"
      )
    );

    validationTree.addValidationRule(
      new ValidateDuplicatedTimetabledPassingTimeId("")
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[not(JourneyPatternRef)]",
        "The ServiceJourney does not refer to a JourneyPattern",
        "SERVICE_JOURNEY_10"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[(TransportMode and not(TransportSubmode))  or (not(TransportMode) and TransportSubmode)]",
        "If overriding Line TransportMode or TransportSubmode on a ServiceJourney, both elements must be present",
        "SERVICE_JOURNEY_11"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[not(OperatorRef) and not(//ServiceFrame/lines/*[self::Line or self::FlexibleLine]/OperatorRef)]",
        "Missing OperatorRef on ServiceJourney (not defined on Line)",
        "SERVICE_JOURNEY_12"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[not(dayTypes/DayTypeRef) and not(@id=//TimetableFrame/vehicleJourneys/DatedServiceJourney/ServiceJourneyRef/@ref)]",
        "The ServiceJourney does not refer to DayTypes nor DatedServiceJourneys",
        "SERVICE_JOURNEY_13"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[dayTypes/DayTypeRef and @id=//TimetableFrame/vehicleJourneys/DatedServiceJourney/ServiceJourneyRef/@ref]",
        "The ServiceJourney references both DayTypes and DatedServiceJourneys",
        "SERVICE_JOURNEY_14"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "for $a in vehicleJourneys/ServiceJourney return if(count(//ServiceFrame/journeyPatterns/*[@id = $a/JourneyPatternRef/@ref]/pointsInSequence/StopPointInJourneyPattern) != count($a/passingTimes/TimetabledPassingTime)) then $a else ()",
        "ServiceJourney does not specify passing time for all StopPointInJourneyPattern",
        "SERVICE_JOURNEY_15"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney[@id = preceding-sibling::ServiceJourney/@id]",
        "ServiceJourney is repeated with a different version",
        "SERVICE_JOURNEY_16"
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DatedServiceJourney[not(OperatingDayRef)]",
        "Missing OperatingDayRef on DatedServiceJourney",
        "DATED_SERVICE_JOURNEY_1"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DatedServiceJourney[not(ServiceJourneyRef)]",
        "Missing ServiceJourneyRef on DatedServiceJourney",
        "DATED_SERVICE_JOURNEY_2"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DatedServiceJourney[count(ServiceJourneyRef) > 1]",
        "Multiple ServiceJourneyRef on DatedServiceJourney",
        "DATED_SERVICE_JOURNEY_3"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DatedServiceJourney[@id = preceding-sibling::DatedServiceJourney/@id]",
        "DatedServiceJourney is repeated with a different version",
        "DATED_SERVICE_JOURNEY_4"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DatedServiceJourney/DatedServiceJourneyRef[@ref = preceding-sibling::DatedServiceJourneyRef/@ref]",
        "Multiple references from a DatedServiceJourney to the same DatedServiceJourney",
        "DATED_SERVICE_JOURNEY_5"
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DeadRun[not(passingTimes)]",
        "The Dead run does not reference passing times",
        "DEAD_RUN_1"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DeadRun[not(JourneyPatternRef)]",
        "The Dead run does not reference a journey pattern",
        "DEAD_RUN_2"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/DeadRun[not(dayTypes/DayTypeRef)]",
        "The Dead run does not reference day types",
        "DEAD_RUN_3"
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/FlexibleServiceProperties[not(@id)]",
        "Missing id on FlexibleServiceProperties",
        "FLEXIBLE_SERVICE_1"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/FlexibleServiceProperties[not(@version)]",
        "Missing version on FlexibleServiceProperties",
        "FLEXIBLE_SERVICE_2"
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
        "Only one of BookWhen or MinimumBookingPeriod should be specified on FlexibleServiceProperties",
        "FLEXIBLE_SERVICE_3"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "vehicleJourneys/ServiceJourney/FlexibleServiceProperties[(BookWhen and not(LatestBookingTime)) or (not(BookWhen) and LatestBookingTime)]",
        "BookWhen must be used together with LatestBookingTime on FlexibleServiceProperties",
        "FLEXIBLE_SERVICE_4"
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "journeyInterchanges/ServiceJourneyInterchange[Advertised or Planned]",
        "The 'Planned' and 'Advertised' properties of an Interchange should not be specified",
        "INTERCHANGE_1"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "journeyInterchanges/ServiceJourneyInterchange[Guaranteed='true' and  (MaximumWaitTime='PT0S' or MaximumWaitTime='PT0M') ]",
        "Guaranteed Interchange should not have a maximum wait time value of zero",
        "INTERCHANGE_2"
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "journeyInterchanges/ServiceJourneyInterchange[MaximumWaitTime > xs:dayTimeDuration('PT1H')]",
        "The maximum waiting time after planned departure for the interchange consumer journey (MaximumWaitTime) should not be longer than one hour",
        "INTERCHANGE_3"
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
  protected List<ValidationRule> getCompositeFrameBaseValidationRules() {
    List<ValidationRule> validationRules = new ArrayList<>();
    validationRules.add(
      new ValidateNotExist(
        "frames/SiteFrame",
        "Unexpected element SiteFrame. It will be ignored",
        "COMPOSITE_SITE_FRAME_IN_COMMON_FILE"
      )
    );

    validationRules.add(
      new ValidateNotExist(
        ".[not(validityConditions)]",
        "A CompositeFrame must define a ValidityCondition valid for all data within the CompositeFrame",
        "COMPOSITE_FRAME_1"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "frames//validityConditions",
        "ValidityConditions defined inside a frame inside a CompositeFrame",
        "COMPOSITE_FRAME_2"
      )
    );

    validationRules.add(
      new ValidateNotExist(
        "//ValidBetween[not(FromDate) and not(ToDate)]",
        "ValidBetween missing either or both of FromDate/ToDate",
        "COMPOSITE_FRAME_3"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "//ValidBetween[FromDate and ToDate and ToDate < FromDate]",
        "FromDate cannot be after ToDate on ValidBetween",
        "COMPOSITE_FRAME_4"
      )
    );

    validationRules.add(
      new ValidateNotExist(
        "//AvailabilityCondition[not(FromDate) and not(ToDate)]",
        "AvailabilityCondition must have either FromDate or ToDate or both present",
        "COMPOSITE_FRAME_4"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "//AvailabilityCondition[FromDate and ToDate and ToDate < FromDate]",
        "FromDate cannot be after ToDate on AvailabilityCondition",
        "COMPOSITE_FRAME_5"
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
        "Missing CompanyNumber element on Operator",
        "OPERATOR_1"
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator[not(Name) or normalize-space(Name) = '']",
        "Missing Name on Operator",
        "OPERATOR_2"
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator[not(LegalName) or normalize-space(LegalName) = '']",
        "Missing LegalName element on Operator",
        "OPERATOR_3"
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator[not(ContactDetails)]",
        "Missing ContactDetails element on Operator",
        "OPERATOR_4"
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator/ContactDetails[(not(Email) or normalize-space(Email) = '') and (not(Phone) or normalize-space(Phone) = '') and (not(Url) or normalize-space(Url) = '')]",
        "At least one of Url, Phone or Email must be defined for ContactDetails on Operator",
        "OPERATOR_5"
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator[not(CustomerServiceContactDetails)]",
        "Missing CustomerServiceContactDetails element on Operator",
        "OPERATOR_6"
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Operator/CustomerServiceContactDetails[not(Url) or normalize-space(Url) = '']",
        "Missing Url element for CustomerServiceContactDetails on Operator",
        "OPERATOR_7"
      )
    );

    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Authority[not(CompanyNumber) or normalize-space(CompanyNumber) = '']",
        "Missing CompanyNumber element on Authority",
        "AUTHORITY_1"
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Authority[not(Name) or normalize-space(Name) = '']",
        "Missing Name element on Authority",
        "AUTHORITY_2"
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Authority[not(LegalName) or normalize-space(LegalName) = '']",
        "Missing LegalName element on Authority",
        "AUTHORITY_3"
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Authority[not(ContactDetails)]",
        "Missing ContactDetails on Authority",
        "AUTHORITY_4"
      )
    );
    resourceFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "organisations/Authority/ContactDetails[not(Url) or not(starts-with(Url, 'http://') or (starts-with(Url, 'https://')) )]",
        "The Url must be defined for ContactDetails on Authority and it must start with 'http://' or 'https://'",
        "AUTHORITY_5"
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
        "The DayType is not assigned to any calendar dates or periods",
        "SERVICE_CALENDAR_1"
      )
    );
    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "//ServiceCalendar[not(dayTypes) and not(dayTypeAssignments)]",
        "ServiceCalendar does not contain neither DayTypes nor DayTypeAssignments",
        "SERVICE_CALENDAR_2"
      )
    );
    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "//ServiceCalendar[not(ToDate)]",
        "Missing ToDate on ServiceCalendar",
        "SERVICE_CALENDAR_3"
      )
    );
    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "//ServiceCalendar[not(FromDate)]",
        "Missing FromDate on ServiceCalendar",
        "SERVICE_CALENDAR_4"
      )
    );
    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "//ServiceCalendar[FromDate and ToDate and ToDate < FromDate]",
        "FromDate cannot be after ToDate on ServiceCalendar",
        "SERVICE_CALENDAR_5"
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
        "At least one Block or TrainBlock required in VehicleScheduleFrame",
        "BLOCK_1"
      )
    );
    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "blocks/Block[not(journeys)]",
        "At least one Journey must be defined for Block",
        "BLOCK_2"
      )
    );
    serviceCalendarFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "blocks/Block[not(dayTypes)]",
        "At least one DayType must be defined for Block",
        "BLOCK_3"
      )
    );

    return serviceCalendarFrameValidationTree;
  }

  /**
   * Validation rules that apply both to Line files and Common files.
   *
   */
  protected List<ValidationRule> getServiceFrameBaseValidationRules() {
    List<ValidationRule> validationRules = new ArrayList<>();
    validationRules.add(
      new ValidateNotExist(
        "Network[not(AuthorityRef)]",
        "Missing AuthorityRef on Network",
        "NETWORK_1"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "Network[not(Name) or normalize-space(Name) = '']",
        "Missing Name element on Network",
        "NETWORK_2"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "Network/groupsOfLines/GroupOfLines[not(Name)  or normalize-space(Name) = '']",
        "Missing Name element on GroupOfLines",
        "NETWORK_3"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "groupsOfLines",
        "Unexpected element groupsOfLines outside of Network",
        "SERVICE_FRAME_1"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "timingPoints",
        "Unexpected element timingPoints. Content ignored",
        "SERVICE_FRAME_2"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "routePoints/RoutePoint[not(projections)]",
        "Missing Projection on RoutePoint",
        "SERVICE_FRAME_3"
      )
    );

    validationRules.add(
      new ValidateNotExist(
        "stopAssignments/PassengerStopAssignment[not(ScheduledStopPointRef)]",
        "Missing ScheduledStopPointRef on PassengerStopAssignment",
        "PASSENGER_STOP_ASSIGNMENT_1"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "stopAssignments/PassengerStopAssignment[not(QuayRef)]",
        "Missing QuayRef on PassengerStopAssignment",
        "PASSENGER_STOP_ASSIGNMENT_2"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "stopAssignments/PassengerStopAssignment[QuayRef/@ref = following-sibling::PassengerStopAssignment/QuayRef/@ref]",
        "The same quay is assigned more than once in PassengerStopAssignments",
        "PASSENGER_STOP_ASSIGNMENT_3"
      )
    );

    validationRules.add(
      new ValidateNotExist(
        "serviceLinks/ServiceLink[not(FromPointRef)]",
        "Missing FromPointRef on ServiceLink",
        "SERVICE_LINK_1"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "serviceLinks/ServiceLink[not(ToPointRef)]",
        "Missing ToPointRef on ServiceLink",
        "SERVICE_LINK_2"
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "serviceLinks/ServiceLink/projections/LinkSequenceProjection/g:LineString/g:posList[not(normalize-space(text()))]",
        "Missing projections element on ServiceLink",
        "SERVICE_LINK_3"
      )
    );

    validationRules.add(
      (
        new ValidateNotExist(
          "destinationDisplays/DestinationDisplay[not(FrontText) or normalize-space(FrontText) = '']",
          "Missing FrontText on DestinationDisplay",
          "DESTINATION_DISPLAY_1"
        )
      )
    );
    validationRules.add(
      (
        new ValidateNotExist(
          "destinationDisplays/DestinationDisplay/vias/Via[not(DestinationDisplayRef)]",
          "Missing DestinationDisplayRef on Via",
          "DESTINATION_DISPLAY_2"
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
        "Missing element Text for Notice",
        "NOTICE_1"
      )
    );
    noticesValidationTree.addValidationRule(
      new ValidateNotExist(
        "Notice/alternativeTexts/AlternativeText[not(Text) or normalize-space(Text/text()) = '']",
        "Missing or empty element Text for Notice Alternative Text",
        "NOTICE_2"
      )
    );
    noticesValidationTree.addValidationRule(
      new ValidateNotExist(
        "Notice/alternativeTexts/AlternativeText/Text[not(@lang)]",
        "Missing element Lang for Notice Alternative Text",
        "NOTICE_3"
      )
    );
    noticesValidationTree.addValidationRule(
      new ValidateNotExist(
        "Notice/alternativeTexts/AlternativeText[Text/@lang = following-sibling::AlternativeText/Text/@lang or Text/@lang = preceding-sibling::AlternativeText/Text/@lang]",
        "The Notice has two Alternative Texts with the same language",
        "NOTICE_4"
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
        "The notice is assigned multiple times to the same object",
        "NOTICE_5"
      )
    );
    noticesAssignmentsValidationTree.addValidationRule(
      new ValidateNotExist(
        "NoticeAssignment[not(NoticedObjectRef)]",
        "The notice assignment does not reference an object",
        "NOTICE_6"
      )
    );
    noticesAssignmentsValidationTree.addValidationRule(
      new ValidateNotExist(
        "NoticeAssignment[not(NoticeRef)]",
        "The notice assignment does not reference a notice",
        "NOTICE_7"
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

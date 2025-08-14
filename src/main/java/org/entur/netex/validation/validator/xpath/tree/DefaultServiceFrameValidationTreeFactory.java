package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedBookingAccessProperty;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedBookingMethodProperty;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedBookingWhenProperty;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedBuyWhenProperty;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedFlexibleLineType;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedTransportModeOnLine;
import org.entur.netex.validation.validator.xpath.rules.ValidateAllowedTransportSubModeOnLine;
import org.entur.netex.validation.validator.xpath.rules.ValidateAtLeastOne;
import org.entur.netex.validation.validator.xpath.rules.ValidateExactlyOne;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

/**
 * Construct a validation tree builder for ServiceFrames.
 */
public class DefaultServiceFrameValidationTreeFactory implements ValidationTreeFactory {

  static final String CODE_LINE_1 = "LINE_1";
  public static final String CODE_LINE_2 = "LINE_2";
  public static final String CODE_LINE_3 = "LINE_3";
  public static final String CODE_LINE_4 = "LINE_4";
  public static final String CODE_LINE_5 = "LINE_5";
  public static final String CODE_LINE_6 = "LINE_6";
  public static final String CODE_LINE_7 = "LINE_7";
  public static final String CODE_LINE_8 = "LINE_8";
  public static final String CODE_LINE_9 = "LINE_9";
  public static final String CODE_SERVICE_FRAME_IN_COMMON_FILE_1 =
    "SERVICE_FRAME_IN_COMMON_FILE_1";
  public static final String CODE_SERVICE_FRAME_IN_COMMON_FILE_2 =
    "SERVICE_FRAME_IN_COMMON_FILE_2";
  public static final String CODE_SERVICE_FRAME_IN_COMMON_FILE_3 =
    "SERVICE_FRAME_IN_COMMON_FILE_3";
  public static final String CODE_NETWORK_1 = "NETWORK_1";
  public static final String CODE_NETWORK_2 = "NETWORK_2";
  public static final String CODE_NETWORK_3 = "NETWORK_3";
  public static final String CODE_ROUTE_1 = "ROUTE_1";
  public static final String CODE_ROUTE_2 = "ROUTE_2";
  public static final String CODE_ROUTE_3 = "ROUTE_3";
  public static final String CODE_ROUTE_4 = "ROUTE_4";
  public static final String CODE_ROUTE_5 = "ROUTE_5";
  public static final String CODE_ROUTE_6 = "ROUTE_6";
  public static final String CODE_SERVICE_FRAME_1 = "SERVICE_FRAME_1";
  public static final String CODE_SERVICE_FRAME_2 = "SERVICE_FRAME_2";
  public static final String CODE_SERVICE_FRAME_3 = "SERVICE_FRAME_3";
  public static final String CODE_PASSENGER_STOP_ASSIGNMENT_1 =
    "PASSENGER_STOP_ASSIGNMENT_1";
  public static final String CODE_PASSENGER_STOP_ASSIGNMENT_2 =
    "PASSENGER_STOP_ASSIGNMENT_2";
  public static final String CODE_PASSENGER_STOP_ASSIGNMENT_3 =
    "PASSENGER_STOP_ASSIGNMENT_3";
  public static final String CODE_DESTINATION_DISPLAY_1 = "DESTINATION_DISPLAY_1";
  public static final String CODE_DESTINATION_DISPLAY_2 = "DESTINATION_DISPLAY_2";
  public static final String CODE_SERVICE_LINK_1 = "SERVICE_LINK_1";
  public static final String CODE_SERVICE_LINK_2 = "SERVICE_LINK_2";
  public static final String CODE_SERVICE_LINK_3 = "SERVICE_LINK_3";
  public static final String CODE_SERVICE_LINK_4 = "SERVICE_LINK_4";
  public static final String CODE_SERVICE_LINK_5 = "SERVICE_LINK_5";
  public static final String CODE_FLEXIBLE_LINE_1 = "FLEXIBLE_LINE_1";
  public static final String CODE_FLEXIBLE_LINE_10 = "FLEXIBLE_LINE_10";
  public static final String CODE_FLEXIBLE_LINE_11 = "FLEXIBLE_LINE_11";
  public static final String CODE_JOURNEY_PATTERN_1 = "JOURNEY_PATTERN_1";
  public static final String CODE_JOURNEY_PATTERN_2 = "JOURNEY_PATTERN_2";
  public static final String CODE_JOURNEY_PATTERN_3 = "JOURNEY_PATTERN_3";
  public static final String CODE_JOURNEY_PATTERN_4 = "JOURNEY_PATTERN_4";
  public static final String CODE_JOURNEY_PATTERN_5 = "JOURNEY_PATTERN_5";
  public static final String CODE_JOURNEY_PATTERN_6 = "JOURNEY_PATTERN_6";
  public static final String CODE_JOURNEY_PATTERN_7 = "JOURNEY_PATTERN_7";
  public static final String CODE_JOURNEY_PATTERN_8 = "JOURNEY_PATTERN_8";
  public static final String CODE_JOURNEY_PATTERN_9 = "JOURNEY_PATTERN_9";

  @Override
  public ValidationTreeBuilder builder() {
    return new ValidationTreeBuilder("Service Frame", "ServiceFrame")
      .withRule(
        new ValidateNotExist(
          "Network[not(AuthorityRef)]",
          CODE_NETWORK_1,
          "Network missing AuthorityRef",
          "Missing AuthorityRef on Network",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "Network[not(Name) or normalize-space(Name) = '']",
          CODE_NETWORK_2,
          "Network missing Name on Network",
          "Missing Name element on Network",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "Network/groupsOfLines/GroupOfLines[not(Name)  or normalize-space(Name) = '']",
          CODE_NETWORK_3,
          "Network missing Name on GroupOfLines",
          "Missing Name element on GroupOfLines",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "groupsOfLines",
          CODE_SERVICE_FRAME_1,
          "ServiceFrame unexpected element GroupOfLines",
          "Unexpected element groupsOfLines outside of Network",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "timingPoints",
          CODE_SERVICE_FRAME_2,
          "ServiceFrame unexpected element timingPoints",
          "Unexpected element timingPoints. Content ignored",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "routePoints/RoutePoint[not(projections)]",
          CODE_SERVICE_FRAME_3,
          "ServiceFrame missing Projection on RoutePoint",
          "Missing Projection on RoutePoint",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "stopAssignments/PassengerStopAssignment[not(ScheduledStopPointRef)]",
          CODE_PASSENGER_STOP_ASSIGNMENT_1,
          "PassengerStopAssignment missing ScheduledStopPointRef",
          "Missing ScheduledStopPointRef on PassengerStopAssignment",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "stopAssignments/PassengerStopAssignment[not(QuayRef)]",
          CODE_PASSENGER_STOP_ASSIGNMENT_2,
          "PassengerStopAssignment missing QuayRef",
          "Missing QuayRef on PassengerStopAssignment",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "stopAssignments/PassengerStopAssignment[QuayRef/@ref = following-sibling::PassengerStopAssignment/QuayRef/@ref]",
          CODE_PASSENGER_STOP_ASSIGNMENT_3,
          "PassengerStopAssignment duplicated Quay assignment",
          "The same quay is assigned more than once in PassengerStopAssignments",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "serviceLinks/ServiceLink[not(FromPointRef)]",
          CODE_SERVICE_LINK_1,
          "ServiceLink missing FromPointRef",
          "Missing FromPointRef on ServiceLink",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "serviceLinks/ServiceLink[not(ToPointRef)]",
          CODE_SERVICE_LINK_2,
          "ServiceLink missing ToPointRef",
          "Missing ToPointRef on ServiceLink",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "serviceLinks/ServiceLink[not(projections)]",
          CODE_SERVICE_LINK_3,
          "ServiceLink missing element Projections",
          "Missing projections element on ServiceLink",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "serviceLinks/ServiceLink/projections/LinkSequenceProjection/g:LineString/g:posList[not(normalize-space(text()))]",
          CODE_SERVICE_LINK_4,
          "ServiceLink missing coordinate list",
          "Missing coordinates list on ServiceLink",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "serviceLinks/ServiceLink/projections/LinkSequenceProjection/g:LineString[count(g:pos) = 1]",
          CODE_SERVICE_LINK_5,
          "ServiceLink less than 2 points",
          "Less than 2 points on ServiceLink",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "destinationDisplays/DestinationDisplay[not(FrontText) or normalize-space(FrontText) = '']",
          CODE_DESTINATION_DISPLAY_1,
          "DestinationDisplay missing FrontText",
          "Missing FrontText on DestinationDisplay",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "destinationDisplays/DestinationDisplay/vias/Via[not(DestinationDisplayRef)]",
          CODE_DESTINATION_DISPLAY_2,
          "DestinationDisplay missing DestinationDisplayRef on Via",
          "Missing DestinationDisplayRef on Via",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateExactlyOne(
          "lines/*[self::Line or self::FlexibleLine]",
          CODE_LINE_1,
          "Line missing Line or FlexibleLine",
          "There must be either Lines or Flexible Lines",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "lines/*[self::Line or self::FlexibleLine][not(Name) or normalize-space(Name) = '']",
          CODE_LINE_2,
          "Line missing Name",
          "Missing Name on Line",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "lines/*[self::Line or self::FlexibleLine][not(PublicCode) or normalize-space(PublicCode) = '']",
          CODE_LINE_3,
          "Line missing PublicCode",
          "Missing PublicCode on Line",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "lines/*[self::Line or self::FlexibleLine][not(TransportMode)]",
          CODE_LINE_4,
          "Line missing TransportMode",
          "Missing TransportMode on Line",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "lines/*[self::Line or self::FlexibleLine][not(TransportSubmode)]",
          CODE_LINE_5,
          "Line missing TransportSubmode",
          "Missing TransportSubmode on Line",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "lines/*[self::Line or self::FlexibleLine]/routes/Route",
          CODE_LINE_6,
          "Line with incorrect use of Route",
          "Routes should not be defined within a Line or FlexibleLine",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "lines/*[self::Line or self::FlexibleLine][not(RepresentedByGroupRef)]",
          CODE_LINE_7,
          "Line missing Network or GroupOfLines",
          "A Line must refer to a GroupOfLines or a Network through element RepresentedByGroupRef",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "lines/*[self::Line or self::FlexibleLine]/*[self::Presentation or self::AlternativePresentation]/*[self::Colour or self::TextColour][text()][string-length(text())!=6]",
          CODE_LINE_8,
          "Invalid color coding length on Presentation",
          "Line colour should be encoded with 6 hexadecimal digits",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "lines/*[self::Line or self::FlexibleLine]/*[self::Presentation or self::AlternativePresentation]/*[self::Colour or self::TextColour][text()][not(matches(text(),'[0-9A-Fa-f]{6}'))]",
          CODE_LINE_9,
          "Invalid color coding value on Presentation",
          "Line colour should be encoded with valid hexadecimal digits",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(new ValidateAllowedTransportModeOnLine())
      .withRuleForLineFile(new ValidateAllowedTransportSubModeOnLine())
      .withRuleForLineFile(
        new ValidateNotExist(
          "lines/FlexibleLine[not(FlexibleLineType)]",
          CODE_FLEXIBLE_LINE_1,
          "FlexibleLine missing FlexibleLineType",
          "Missing FlexibleLineType on FlexibleLine",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "lines/FlexibleLine[BookWhen and MinimumBookingPeriod]",
          CODE_FLEXIBLE_LINE_10,
          "FlexibleLine illegal use of both BookWhen and MinimumBookingPeriod",
          "Only one of BookWhen or MinimumBookingPeriod should be specified on FlexibleLine",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "lines/FlexibleLine[(BookWhen and not(LatestBookingTime)) or (not(BookWhen) and LatestBookingTime)]",
          CODE_FLEXIBLE_LINE_11,
          "FlexibleLine BookWhen without LatestBookingTime or LatestBookingTime without BookWhen",
          "BookWhen must be used together with LatestBookingTime on FlexibleLine",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(new ValidateAllowedFlexibleLineType())
      .withRuleForLineFile(new ValidateAllowedBookingWhenProperty("lines/FlexibleLine"))
      .withRuleForLineFile(new ValidateAllowedBuyWhenProperty("lines/FlexibleLine"))
      .withRuleForLineFile(new ValidateAllowedBookingMethodProperty("lines/FlexibleLine"))
      .withRuleForLineFile(new ValidateAllowedBookingAccessProperty("lines/FlexibleLine"))
      .withRuleForLineFile(
        (
          new ValidateAtLeastOne(
            "routes/Route",
            CODE_ROUTE_1,
            "Route missing",
            "There should be at least one Route",
            Severity.ERROR
          )
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "routes/Route[not(Name) or normalize-space(Name) = '']",
          CODE_ROUTE_2,
          "Route missing Name",
          "Missing Name on Route",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "routes/Route[not(LineRef) and not(FlexibleLineRef)]",
          CODE_ROUTE_3,
          "Route missing LineRef",
          "Missing lineRef on Route",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "routes/Route[not(pointsInSequence)]",
          CODE_ROUTE_4,
          "Route missing pointsInSequence",
          "Missing pointsInSequence on Route",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "routes/Route/DirectionRef",
          CODE_ROUTE_5,
          "Route illegal DirectionRef",
          "DirectionRef not allowed on Route (use DirectionType)",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "routes/Route/pointsInSequence/PointOnRoute[@order = preceding-sibling::PointOnRoute/@order]",
          CODE_ROUTE_6,
          "Route duplicated order",
          "Several points on route have the same order",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        (
          new ValidateNotExist(
            "journeyPatterns/ServiceJourneyPattern",
            CODE_JOURNEY_PATTERN_1,
            "JourneyPattern illegal element ServiceJourneyPattern",
            "ServiceJourneyPattern not allowed",
            Severity.ERROR
          )
        )
      )
      .withRuleForLineFile(
        (
          new ValidateAtLeastOne(
            "journeyPatterns/JourneyPattern",
            CODE_JOURNEY_PATTERN_2,
            "JourneyPattern missing JourneyPattern",
            "No JourneyPattern defined in the Service Frame",
            Severity.ERROR
          )
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "journeyPatterns/JourneyPattern[not(RouteRef)]",
          CODE_JOURNEY_PATTERN_3,
          "JourneyPattern missing RouteRef",
          "Missing RouteRef on JourneyPattern",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern[1][not(DestinationDisplayRef)]",
          CODE_JOURNEY_PATTERN_4,
          "JourneyPattern missing DestinationDisplayRef on first stop point",
          "Missing DestinationDisplayRef on first StopPointInJourneyPattern",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern[last()][DestinationDisplayRef]",
          CODE_JOURNEY_PATTERN_5,
          "JourneyPattern illegal DestinationDisplayRef on last stop point",
          "DestinationDisplayRef not allowed on last StopPointInJourneyPattern",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern[ForAlighting = 'false' and ForBoarding = 'false']",
          CODE_JOURNEY_PATTERN_6,
          "JourneyPattern stop point without boarding or alighting",
          "StopPointInJourneyPattern neither allows boarding nor alighting",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern[DestinationDisplayRef/@ref = preceding-sibling::StopPointInJourneyPattern[1]/DestinationDisplayRef/@ref and number(@order) >  number(preceding-sibling::StopPointInJourneyPattern[1]/@order)]",
          CODE_JOURNEY_PATTERN_7,
          "JourneyPattern illegal repetition of DestinationDisplay",
          "StopPointInJourneyPattern declares reference to the same DestinationDisplay as previous StopPointInJourneyPattern",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateAllowedBookingWhenProperty(
          "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements"
        )
      )
      .withRuleForLineFile(
        new ValidateAllowedBuyWhenProperty(
          "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements"
        )
      )
      .withRuleForLineFile(
        new ValidateAllowedBookingMethodProperty(
          "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements"
        )
      )
      .withRuleForLineFile(
        new ValidateAllowedBookingAccessProperty(
          "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements"
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements[BookWhen and MinimumBookingPeriod]",
          CODE_JOURNEY_PATTERN_8,
          "JourneyPattern  illegal use of both BookWhen and MinimumBookingPeriod",
          "Only one of BookWhen or MinimumBookingPeriod should be specified on StopPointInJourneyPattern",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "journeyPatterns/JourneyPattern/pointsInSequence/StopPointInJourneyPattern/BookingArrangements[(BookWhen and not(LatestBookingTime)) or (not(BookWhen) and LatestBookingTime)]",
          CODE_JOURNEY_PATTERN_9,
          "JourneyPattern  BookWhen without LatestBookingTime or LatestBookingTime without BookWhen",
          "BookWhen must be used together with LatestBookingTime on StopPointInJourneyPattern",
          Severity.WARNING
        )
      )
      .withRuleForCommonFile(
        new ValidateNotExist(
          "lines/Line",
          CODE_SERVICE_FRAME_IN_COMMON_FILE_1,
          "ServiceFrame unexpected element Line",
          "Line not allowed in common files",
          Severity.ERROR
        )
      )
      .withRuleForCommonFile(
        new ValidateNotExist(
          "routes/Route",
          CODE_SERVICE_FRAME_IN_COMMON_FILE_2,
          "ServiceFrame unexpected element Route",
          "Route not allowed in common files",
          Severity.ERROR
        )
      )
      .withRuleForCommonFile(
        new ValidateNotExist(
          "journeyPatterns/JourneyPattern | journeyPatterns/ServiceJourneyPattern",
          CODE_SERVICE_FRAME_IN_COMMON_FILE_3,
          "ServiceFrame unexpected element JourneyPattern",
          "JourneyPattern not allowed in common files",
          Severity.ERROR
        )
      );
  }
}

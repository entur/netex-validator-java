package org.entur.netex.validation.validator.xpath.rules;

import java.util.List;
import java.util.Set;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.entur.netex.validation.validator.xpath.XPathValidationReportEntry;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidateMandatoryBookingWhenOrMinimumBookingPeriodPropertyTest {

  public static final String TEST_CODESPACE = "FLB";
  private static final NetexXMLParser NETEX_XML_PARSER = new NetexXMLParser(
    Set.of("SiteFrame")
  );
  private static final String NETEX_FRAGMENT =
    "                <frames xmlns=\"http://www.netex.org.uk/netex\">" +
    "<ServiceFrame>\n" +
    "                     <lines>\n" +
    "                        <FlexibleLine version=\"46\" id=\"BRA:FlexibleLine:9204411c-bf86-4b6a-b8fa-5c40b8702213\">\n" +
    "                         ${BOOK_WHEN}\n" +
    "                        </FlexibleLine>\n" +
    "                    </lines>\n" +
    "                    <journeyPatterns>\n" +
    "                        <JourneyPattern version=\"1\" id=\"BRA:JourneyPattern:58fe9e56-1799-4e09-af8e-5091199d2319\">\n" +
    "                            <pointsInSequence>\n" +
    "<StopPointInJourneyPattern order=\"1\" version=\"32\" id=\"BRA:StopPointInJourneyPattern:eaa48c3c-aee6-4b24-a721-ee3b05303f27\">\n" +
    "    <ScheduledStopPointRef ref=\"BRA:ScheduledStopPoint:69136f28-5575-4b68-b6db-7229a42af8ee\"/>\n" +
    "    <ForAlighting>false</ForAlighting>\n" +
    "    <ForBoarding>true</ForBoarding>\n" +
    "    <DestinationDisplayRef ref=\"BRA:DestinationDisplay:24ce4e13-be81-443a-9d65-f2c4b7cb4c76\"/>\n" +
    "${BOOKING_ARRANGEMENT}\n" +
    "</StopPointInJourneyPattern>\n" +
    "<StopPointInJourneyPattern order=\"2\" version=\"1\" id=\"BRA:StopPointInJourneyPattern:1f8eb0f1-76df-467b-ad35-8ca4136dbcec\">\n" +
    "    <ScheduledStopPointRef ref=\"BRA:ScheduledStopPoint:2e059449-3c88-4447-987a-90a41556a8a7\"/>\n" +
    "    <ForAlighting>true</ForAlighting>\n" +
    "    <ForBoarding>false</ForBoarding>\n" +
    "${BOOKING_ARRANGEMENT}\n" +
    "</StopPointInJourneyPattern>\n" +
    "                            </pointsInSequence>\n" +
    "                        </JourneyPattern>\n" +
    "                    </journeyPatterns>\n" +
    "                </ServiceFrame>" +
    "                <TimetableFrame version=\"1\" id=\"BRA:TimetableFrame:1\">\n" +
    "                    <vehicleJourneys>\n" +
    "                        <ServiceJourney version=\"13\" id=\"BRA:ServiceJourney:0caf533c-d8ce-41d0-980d-46c7c86d6895\">\n" +
    "                            <Name>indre-hele-spesialdager lørdager</Name>\n" +
    "                            <dayTypes>\n" +
    "<DayTypeRef ref=\"BRA:DayType:3e5e36b6-6131-4950-b498-60c91ac4117f\"/>\n" +
    "                            </dayTypes>\n" +
    "                            <JourneyPatternRef ref=\"BRA:JourneyPattern:58fe9e56-1799-4e09-af8e-5091199d2319\" version=\"1\"/>\n" +
    "                            ${FLEXIBLE_PROPERTIES}\n" +
    "                            <passingTimes>\n" +
    "<TimetabledPassingTime version=\"0\" id=\"BRA:TimetabledPassingTime:d3e69c70-21b7-45f9-a5ed-81f766cd6c73\">\n" +
    "    <StopPointInJourneyPatternRef ref=\"BRA:StopPointInJourneyPattern:eaa48c3c-aee6-4b24-a721-ee3b05303f27\" version=\"32\"/>\n" +
    "    <LatestArrivalTime>15:00:00</LatestArrivalTime>\n" +
    "    <EarliestDepartureTime>10:00:00</EarliestDepartureTime>\n" +
    "</TimetabledPassingTime>\n" +
    "<TimetabledPassingTime version=\"0\" id=\"BRA:TimetabledPassingTime:202ab057-1013-430e-97b6-002b3d6a001b\">\n" +
    "    <StopPointInJourneyPatternRef ref=\"BRA:StopPointInJourneyPattern:1f8eb0f1-76df-467b-ad35-8ca4136dbcec\" version=\"1\"/>\n" +
    "    <LatestArrivalTime>15:00:00</LatestArrivalTime>\n" +
    "    <EarliestDepartureTime>10:00:00</EarliestDepartureTime>\n" +
    "</TimetabledPassingTime>\n" +
    "                            </passingTimes>\n" +
    "                        </ServiceJourney>\n" +
    "                    </vehicleJourneys>\n" +
    "</TimetableFrame>\n" +
    "</frames>\n";

  @Test
  void testBookWhenMissingInLineAndStopPointAndServiceJourney() {
    ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty validateMandatoryBookingWhenOrMinimumBookingPeriodProperty =
      new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty("frames/");
    String flexibleLineWithMissingBookWhen = NETEX_FRAGMENT
      .replace("${BOOK_WHEN}", "")
      .replace("${BOOKING_ARRANGEMENT}", "")
      .replace("${FLEXIBLE_PROPERTIES}", "");
    XdmNode document = NETEX_XML_PARSER.parseStringToXdmNode(
      flexibleLineWithMissingBookWhen
    );
    XPathValidationContext xpathValidationContext = new XPathValidationContext(
      document,
      NETEX_XML_PARSER,
      TEST_CODESPACE,
      null
    );
    List<XPathValidationReportEntry> xPathValidationReportEntries =
      validateMandatoryBookingWhenOrMinimumBookingPeriodProperty.validate(
        xpathValidationContext
      );
    Assertions.assertNotNull(xPathValidationReportEntries);
    Assertions.assertFalse(xPathValidationReportEntries.isEmpty());
    Assertions.assertEquals(
      validateMandatoryBookingWhenOrMinimumBookingPeriodProperty.getCode(),
      xPathValidationReportEntries.get(0).getCode()
    );
  }

  @Test
  void testBookWhenInLine() {
    ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty validateMandatoryBookingWhenOrMinimumBookingPeriodProperty =
      new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty("frames/");
    String flexibleLineWithBookWhen = NETEX_FRAGMENT
      .replace("${BOOK_WHEN}", "<BookWhen>advanceAndDayOfTravel</BookWhen>")
      .replace("${BOOKING_ARRANGEMENT}", "")
      .replace("${FLEXIBLE_PROPERTIES}", "");
    XdmNode document = NETEX_XML_PARSER.parseStringToXdmNode(
      flexibleLineWithBookWhen
    );
    XPathValidationContext xpathValidationContext = new XPathValidationContext(
      document,
      NETEX_XML_PARSER,
      TEST_CODESPACE,
      null
    );
    List<XPathValidationReportEntry> xPathValidationReportEntries =
      validateMandatoryBookingWhenOrMinimumBookingPeriodProperty.validate(
        xpathValidationContext
      );
    Assertions.assertNotNull(xPathValidationReportEntries);
    Assertions.assertTrue(xPathValidationReportEntries.isEmpty());
  }

  @Test
  void testBookWhenInJourneyPattern() {
    ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty validateMandatoryBookingWhenOrMinimumBookingPeriodProperty =
      new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty("frames/");
    String journeyPatternWithBookingArrangement = NETEX_FRAGMENT
      .replace("${BOOK_WHEN}", "")
      .replace(
        "${BOOKING_ARRANGEMENT}",
        "<BookingArrangements><BookWhen>advanceAndDayOfTravel</BookWhen></BookingArrangements>"
      )
      .replace("${FLEXIBLE_PROPERTIES}", "");
    XdmNode document = NETEX_XML_PARSER.parseStringToXdmNode(
      journeyPatternWithBookingArrangement
    );
    XPathValidationContext xpathValidationContext = new XPathValidationContext(
      document,
      NETEX_XML_PARSER,
      TEST_CODESPACE,
      null
    );
    List<XPathValidationReportEntry> xPathValidationReportEntries =
      validateMandatoryBookingWhenOrMinimumBookingPeriodProperty.validate(
        xpathValidationContext
      );
    Assertions.assertNotNull(xPathValidationReportEntries);
    Assertions.assertTrue(xPathValidationReportEntries.isEmpty());
  }

  @Test
  void testBookWhenInServiceJourney() {
    ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty validateMandatoryBookingWhenOrMinimumBookingPeriodProperty =
      new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty("frames/");
    String serviceJourneyWithFlexibleProperties = NETEX_FRAGMENT
      .replace("${BOOK_WHEN}", "")
      .replace("${BOOKING_ARRANGEMENT}", "")
      .replace(
        "${FLEXIBLE_PROPERTIES}",
        "<FlexibleServiceProperties><BookWhen>advanceAndDayOfTravel</BookWhen></FlexibleServiceProperties>"
      );
    XdmNode document = NETEX_XML_PARSER.parseStringToXdmNode(
      serviceJourneyWithFlexibleProperties
    );
    XPathValidationContext xpathValidationContext = new XPathValidationContext(
      document,
      NETEX_XML_PARSER,
      TEST_CODESPACE,
      null
    );
    List<XPathValidationReportEntry> xPathValidationReportEntries =
      validateMandatoryBookingWhenOrMinimumBookingPeriodProperty.validate(
        xpathValidationContext
      );
    Assertions.assertNotNull(xPathValidationReportEntries);
    Assertions.assertTrue(xPathValidationReportEntries.isEmpty());
  }
}

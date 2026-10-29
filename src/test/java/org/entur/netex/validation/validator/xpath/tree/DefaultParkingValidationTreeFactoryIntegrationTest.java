package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.tree.DefaultParkingValidationTreeFactory.CODE_PARKING_AVAILABILITY_CONDITION_WITHOUT_DAY_TYPE;
import static org.entur.netex.validation.validator.xpath.tree.DefaultParkingValidationTreeFactory.CODE_PARKING_TIMEBAND_INVALID_TIME_RANGE;
import static org.entur.netex.validation.validator.xpath.tree.DefaultParkingValidationTreeFactory.CODE_PARKING_TIMEBAND_WITHOUT_START_OR_END_TIME;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.entur.netex.validation.validator.NetexValidatorsRunner;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.validator.xpath.XPathRuleValidator;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for parking AvailabilityCondition validation rules.
 * Runs the full NetexValidatorsRunner pipeline (schema + XPath) against
 * minimal parking NeTEx files to verify that rules fire on invalid data
 * and are silent on valid data.
 */
class DefaultParkingValidationTreeFactoryIntegrationTest {

  private static final String CODESPACE = "FSR";
  private static final String REPORT_ID = "test-report";
  private static final String FILE_NAME = "FSR_parking.xml";

  /**
   * Minimal parking NeTEx that passes schema validation and satisfies all three
   * parking rules. Used as the base for "bad" variants by replacing specific elements.
   */
  private static final String VALID_PARKING_NETEX =
    """
    <?xml version="1.0" encoding="UTF-8"?>
    <PublicationDelivery
        xmlns="http://www.netex.org.uk/netex"
        version="1.13:NO-NeTEx-networktimetable:1.3">
      <PublicationTimestamp>2026-01-01T00:00:00</PublicationTimestamp>
      <ParticipantRef>FSR</ParticipantRef>
      <dataObjects>
        <ResourceFrame version="1" id="FSR:ResourceFrame:1">
          <validityConditions>
            <AvailabilityCondition version="1" id="FSR:AvailabilityCondition:1">
              <FromDate>2026-01-01T00:00:00</FromDate>
              <ToDate>2030-01-01T00:00:00</ToDate>
            </AvailabilityCondition>
          </validityConditions>
          <organisations>
            <Operator version="1" id="FSR:Operator:1">
              <Name>Test Operator</Name>
              <OrganisationType>operator</OrganisationType>
            </Operator>
          </organisations>
        </ResourceFrame>
        <ServiceCalendarFrame version="1" id="FSR:ServiceCalendarFrame:1">
          <dayTypes>
            <DayType version="1" id="FSR:DayType:Weekday">
              <Name>Weekday</Name>
              <properties>
                <PropertyOfDay>
                  <DaysOfWeek>Monday Tuesday Wednesday Thursday Friday</DaysOfWeek>
                </PropertyOfDay>
              </properties>
            </DayType>
          </dayTypes>
        </ServiceCalendarFrame>
        <SiteFrame version="1" id="FSR:SiteFrame:1">
          <stopPlaces>
            <StopPlace version="1" id="FSR:StopPlace:1">
              <Name>Test Stop</Name>
              <StopPlaceType>other</StopPlaceType>
            </StopPlace>
          </stopPlaces>
          <parkings>
            <Parking version="1" id="FSR:Parking:1">
              <validityConditions>
                <AvailabilityCondition version="1" id="FSR:Parking:1:AC:1">
                  <FromDate>2026-01-01T00:00:00</FromDate>
                  <ToDate>2030-01-01T00:00:00</ToDate>
                  <IsAvailable>true</IsAvailable>
                  <dayTypes>
                    <DayTypeRef version="1" ref="FSR:DayType:Weekday"/>
                  </dayTypes>
                  <timebands>
                    <Timeband version="1" id="FSR:Parking:1:TB:1">
                      <StartTime>08:00:00</StartTime>
                      <EndTime>18:00:00</EndTime>
                    </Timeband>
                  </timebands>
                </AvailabilityCondition>
              </validityConditions>
              <Name>Test Parking</Name>
              <OperatorRef ref="FSR:Operator:1" version="1"/>
              <ParentSiteRef ref="FSR:StopPlace:1" version="1"/>
              <ParkingType>parkAndRide</ParkingType>
              <ParkingLayout>openSpace</ParkingLayout>
              <TotalCapacity>50</TotalCapacity>
            </Parking>
          </parkings>
        </SiteFrame>
      </dataObjects>
    </PublicationDelivery>
    """;

  private NetexValidatorsRunner runner;

  @BeforeEach
  void setUp() {
    // Use a custom entry factory that stores rule.code() in the name field so tests
    // can assert against the CODE_* constants rather than fragile rule name strings.
    NetexXMLParser netexXMLParser = new NetexXMLParser(Collections.emptySet());
    NetexSchemaValidator netexSchemaValidator = new NetexSchemaValidator(100);
    XPathRuleValidator xPathRuleValidator = new XPathRuleValidator(
      new PublicationDeliveryValidationTreeFactory()
    );
    runner =
      NetexValidatorsRunner
        .of()
        .withNetexXMLParser(netexXMLParser)
        .withNetexSchemaValidator(netexSchemaValidator)
        .withXPathValidators(List.of(xPathRuleValidator))
        .withValidationReportEntryFactory(issue ->
          new ValidationReportEntry(
            issue.message(),
            issue.rule().code(),
            issue.rule().severity(),
            issue.dataLocation()
          )
        )
        .build();
  }

  @Test
  void testValidParkingProducesNoParkingRuleViolations() {
    ValidationReport report = validate(VALID_PARKING_NETEX);
    List<String> parkingCodes = parkingRuleCodes(report);
    assertTrue(
      parkingCodes.isEmpty(),
      "Expected no parking rule violations but got: " + parkingCodes
    );
  }

  @Test
  void testAvailabilityConditionWithoutDayTypeRefIsReported() {
    // Remove the entire <dayTypes> block — AvailabilityCondition without dayTypes/DayTypeRef
    String xml = VALID_PARKING_NETEX.replaceAll(
      "\\s*<dayTypes>[^<]*<DayTypeRef[^/]*/>[^<]*</dayTypes>",
      ""
    );
    ValidationReport report = validate(xml);
    assertTrue(
      hasCode(report, CODE_PARKING_AVAILABILITY_CONDITION_WITHOUT_DAY_TYPE),
      "Expected " +
      CODE_PARKING_AVAILABILITY_CONDITION_WITHOUT_DAY_TYPE +
      " but got: " +
      parkingRuleCodes(report)
    );
  }

  @Test
  void testTimebandWithoutEndTimeIsReported() {
    String xml = VALID_PARKING_NETEX.replace("<EndTime>18:00:00</EndTime>", "");
    ValidationReport report = validate(xml);
    assertTrue(
      hasCode(report, CODE_PARKING_TIMEBAND_WITHOUT_START_OR_END_TIME),
      "Expected " +
      CODE_PARKING_TIMEBAND_WITHOUT_START_OR_END_TIME +
      " but got: " +
      parkingRuleCodes(report)
    );
  }

  @Test
  void testTimebandWithEndTimeBeforeStartTimeIsReported() {
    String xml = VALID_PARKING_NETEX
      .replace("<StartTime>08:00:00</StartTime>", "<StartTime>18:00:00</StartTime>")
      .replace("<EndTime>18:00:00</EndTime>", "<EndTime>08:00:00</EndTime>");
    ValidationReport report = validate(xml);
    assertTrue(
      hasCode(report, CODE_PARKING_TIMEBAND_INVALID_TIME_RANGE),
      "Expected " +
      CODE_PARKING_TIMEBAND_INVALID_TIME_RANGE +
      " but got: " +
      parkingRuleCodes(report)
    );
  }

  private ValidationReport validate(String xml) {
    return runner.validate(
      CODESPACE,
      REPORT_ID,
      FILE_NAME,
      xml.getBytes(StandardCharsets.UTF_8)
    );
  }

  private static List<String> parkingRuleCodes(ValidationReport report) {
    return report
      .getValidationReportEntries()
      .stream()
      .map(ValidationReportEntry::getName)
      .filter(name -> name.startsWith("PARKING_"))
      .toList();
  }

  private static boolean hasCode(ValidationReport report, String code) {
    return report
      .getValidationReportEntries()
      .stream()
      .anyMatch(e -> e.getName().equals(code));
  }
}

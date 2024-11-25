package org.entur.netex.validation.validator.xpath;

import static org.entur.netex.validation.validator.ValidatorTestUtil.validateXPath;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class XPathRuleValidatorTest {

  private static final String TEST_DATASET_AUTHORITY_VALIDATION_FILE_NAME =
    "rb_flb-aggregated-netex.zip";
  private static final String TEST_DATASET_DAY_TYPE_NOT_ASSIGNED =
    "rb_flb-aggregated-netex-daytype-not-assigned.zip";

  private static final String TEST_DATASET_COLOUR_VALID_CODING_FILE_NAME =
    "test-colour-valid-coding.zip";
  private static final String TEST_DATASET_COLOUR_INVALID_CODING_LENGTH_FILE_NAME =
    "test-colour-invalid-coding-length.zip";
  private static final String TEST_DATASET_COLOUR_INVALID_CODING_VALUE_FILE_NAME =
    "test-colour-invalid-coding-value.zip";
  private static final String TEST_DSJ_MULTIPLE_REFERENCE_TO_SAME_DSJ =
    "test-multiple-references-to-same-dsj.zip";
  private static final String TEST_NON_NUMERIC_NETEX_VERSION =
    "rb_flb-aggregated-netex-non-numeric-version.zip";

  private static final String TEST_FLEXIBLE_LINE_VALID =
    "rb_bra-flexible-lines-valid-passing-times.zip";
  private static final String TEST_FLEXIBLE_LINE_MISSING_DEPARTURE_AND_ARRIVAL_TIMES =
    "rb_bra-flexible-lines-invalid-missing-departure-and-arrival-times.zip";
  private static final String TEST_FLEXIBLE_LINE_MISSING_EARLIEST_DEPARTURE_TIME =
    "rb_bra-flexible-lines-missing-departure-time.zip";

  private static final String TEST_FLEXIBLE_LINE_MISSING_NOTICE_REF =
    "rb_bra-flexible-lines-missing-notice-ref.zip";

  private static final String TEST_FLEXIBLE_LINE_MISSING_NOTICED_OBJECT_REF =
    "rb_bra-flexible-lines-missing-noticed-object-ref.zip";

  private static final String TEST_FLEXIBLE_LINE_MISSING_LAST_ARRIVAL_TIME =
    "rb_bra-flexible-lines-missing-last-arrival-time.zip";
  private static final String TEST_LINE_MISSING_DEPARTURE_AND_ARRIVAL_TIMES =
    "rb_flb-aggregated-netex-missing-departure-and-arrival-times.zip";

  private static final String TEST_LINE_MISSING_DEPARTURE_TIME =
    "rb_flb-aggregated-netex-missing-departure-time.zip";

  private static final String TEST_LINE_MISSING_LAST_ARRIVAL_TIME =
    "rb_flb-aggregated-netex-missing-last-arrival-time.zip";

  private final ValidationTreeFactory validationTreeFactory =
    new DefaultValidationTreeFactory();

  private final XPathRuleValidator xPathRuleValidator = new XPathRuleValidator(
    validationTreeFactory
  );
  private final NetexXMLParser netexXMLParser = new NetexXMLParser();

  @Test
  void testDayTypeAllAssigned() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream('/' + TEST_DATASET_AUTHORITY_VALIDATION_FILE_NAME);
    List<ValidationIssue> validationIssues = validateXPath(
      "FLB",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertTrue(
      validationIssues
        .stream()
        .noneMatch(validationIssue ->
          validationIssue.rule().code().equals("SERVICE_CALENDAR_1")
        )
    );
  }

  @Test
  void testDayTypeNotAssigned() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream('/' + TEST_DATASET_DAY_TYPE_NOT_ASSIGNED);
    List<ValidationIssue> validationIssues = validateXPath(
      "FLB",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationIssue ->
          validationIssue.rule().code().equals("SERVICE_CALENDAR_1")
        )
    );
  }

  @Test
  void testValidColourCoding() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream('/' + TEST_DATASET_COLOUR_VALID_CODING_FILE_NAME);
    List<ValidationIssue> validationIssues = validateXPath(
      "ENT",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertTrue(
      validationIssues
        .stream()
        .noneMatch(validationIssue ->
          validationIssue.rule().code().equals("LINE_8")
        )
    );
    Assertions.assertTrue(
      validationIssues
        .stream()
        .noneMatch(validationIssue ->
          validationIssue.rule().code().equals("LINE_9")
        )
    );
  }

  @Test
  void testInvalidColourCodingLength() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream(
        '/' + TEST_DATASET_COLOUR_INVALID_CODING_LENGTH_FILE_NAME
      );
    List<ValidationIssue> validationIssues = validateXPath(
      "ENT",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationIssue ->
          validationIssue.rule().code().equals("LINE_8")
        )
    );
    Assertions.assertTrue(
      validationIssues
        .stream()
        .noneMatch(validationIssue ->
          validationIssue.rule().code().equals("LINE_9")
        )
    );
  }

  @Test
  void testInvalidColourCodingValue() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream(
        '/' + TEST_DATASET_COLOUR_INVALID_CODING_VALUE_FILE_NAME
      );
    List<ValidationIssue> validationIssues = validateXPath(
      "ENT",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationIssue ->
          validationIssue.rule().code().equals("LINE_9")
        )
    );
    Assertions.assertTrue(
      validationIssues
        .stream()
        .noneMatch(validationIssue ->
          validationIssue.rule().code().equals("LINE_8")
        )
    );
  }

  @Test
  void testDatedServiceJourneyMultipleReferenceToTheSameDatedServiceJourney()
    throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream('/' + TEST_DSJ_MULTIPLE_REFERENCE_TO_SAME_DSJ);
    List<ValidationIssue> validationIssues = validateXPath(
      "ENT",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationIssue ->
          validationIssue.rule().code().equals("DATED_SERVICE_JOURNEY_5")
        )
    );
  }

  @Test
  void testNonNumericNetexVersion() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream('/' + TEST_NON_NUMERIC_NETEX_VERSION);
    List<ValidationIssue> validationIssues = validateXPath(
      "FLB",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationIssue ->
          validationIssue.rule().code().equals("VERSION_NON_NUMERIC")
        )
    );
  }

  @Test
  void testValidFlexibleLine() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream('/' + TEST_FLEXIBLE_LINE_VALID);
    List<ValidationIssue> validationIssues = validateXPath(
      "BRA",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .noneMatch(validationIssue ->
          validationIssue.rule().severity() == Severity.ERROR
        )
    );
  }

  @Test
  void testInValidFlexibleLineMissingDepartureAndArrivalTime()
    throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream(
        '/' + TEST_FLEXIBLE_LINE_MISSING_DEPARTURE_AND_ARRIVAL_TIMES
      );
    List<ValidationIssue> validationIssues = validateXPath(
      "BRA",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationIssue ->
          validationIssue.rule().code().equals("SERVICE_JOURNEY_4")
        )
    );
  }

  @Test
  void testInValidFlexibleLineMissingDepartureTime() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream(
        '/' + TEST_FLEXIBLE_LINE_MISSING_EARLIEST_DEPARTURE_TIME
      );
    List<ValidationIssue> validationIssues = validateXPath(
      "BRA",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationIssue ->
          validationIssue.rule().code().equals("SERVICE_JOURNEY_5")
        )
    );
  }

  @Test
  void testInValidFlexibleLineMissingLastArrivalTime() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream('/' + TEST_FLEXIBLE_LINE_MISSING_LAST_ARRIVAL_TIME);
    List<ValidationIssue> validationIssues = validateXPath(
      "BRA",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationIssue ->
          validationIssue.rule().code().equals("SERVICE_JOURNEY_6")
        )
    );
  }

  @Test
  void testInValidineMissingDepartureAndArrivalTime() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream('/' + TEST_LINE_MISSING_DEPARTURE_AND_ARRIVAL_TIMES);
    List<ValidationIssue> validationIssues = validateXPath(
      "FLB",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationReportEntry ->
          validationReportEntry.rule().code().equals("SERVICE_JOURNEY_4")
        )
    );
  }

  @Test
  void testInValidLineMissingDepartureTime() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream('/' + TEST_LINE_MISSING_DEPARTURE_TIME);
    List<ValidationIssue> validationIssues = validateXPath(
      "FLB",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationReportEntry ->
          validationReportEntry.rule().code().equals("SERVICE_JOURNEY_5")
        )
    );
  }

  @Test
  void testInValidLineMissingLastArrivalTime() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream('/' + TEST_LINE_MISSING_LAST_ARRIVAL_TIME);
    List<ValidationIssue> validationIssues = validateXPath(
      "FLB",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationIssue ->
          validationIssue.rule().code().equals("SERVICE_JOURNEY_6")
        )
    );
  }

  @Test
  void testMissingNoticedObjectRef() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream('/' + TEST_FLEXIBLE_LINE_MISSING_NOTICED_OBJECT_REF);
    List<ValidationIssue> validationIssues = validateXPath(
      "BRA",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationIssue ->
          validationIssue.rule().code().equals("NOTICE_6")
        )
    );
  }

  @Test
  void testMissingNoticeRef() throws IOException {
    InputStream testDatasetAsStream = getClass()
      .getResourceAsStream('/' + TEST_FLEXIBLE_LINE_MISSING_NOTICE_REF);
    List<ValidationIssue> validationIssues = validateXPath(
      "BRA",
      xPathRuleValidator,
      netexXMLParser,
      testDatasetAsStream
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertTrue(
      validationIssues
        .stream()
        .anyMatch(validationIssue ->
          validationIssue.rule().code().equals("NOTICE_7")
        )
    );
  }
}

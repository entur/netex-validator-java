package org.entur.netex.validation.validator.xpath;

import org.entur.netex.validation.configuration.DefaultValidationConfigLoader;
import org.entur.netex.validation.configuration.ValidationConfigLoader;
import org.entur.netex.validation.validator.DefaultValidationEntryFactory;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.entur.netex.validation.validator.ValidatorTestUtil.validateXPath;

class XpathValidatorTest {

    private static final String TEST_DATASET_AUTHORITY_VALIDATION_FILE_NAME = "rb_flb-aggregated-netex.zip";
    private static final String TEST_DATASET_DAY_TYPE_NOT_ASSIGNED = "rb_flb-aggregated-netex-daytype-not-assigned.zip";

    private static final String TEST_DATASET_COLOUR_VALID_CODING_FILE_NAME = "test-colour-valid-coding.zip";
    private static final String TEST_DATASET_COLOUR_INVALID_CODING_LENGTH_FILE_NAME = "test-colour-invalid-coding-length.zip";
    private static final String TEST_DATASET_COLOUR_INVALID_CODING_VALUE_FILE_NAME = "test-colour-invalid-coding-value.zip";
    private static final String TEST_DSJ_MULTIPLE_REFERENCE_TO_SAME_DSJ = "test-multiple-references-to-same-dsj.zip";
    private static final String TEST_NON_NUMERIC_NETEX_VERSION = "rb_flb-aggregated-netex-non-numeric-version.zip";


    private final ValidationTreeFactory validationTreeFactory = new DefaultValidationTreeFactory();
    private final ValidationConfigLoader validationConfigLoader = new DefaultValidationConfigLoader();
    private final XPathValidator xPathValidator = new XPathValidator(validationTreeFactory, new DefaultValidationEntryFactory(validationConfigLoader));
    private final NetexXMLParser netexXMLParser = new NetexXMLParser();

    @Test
    void testXPathValidator() throws IOException {
        InputStream testDatasetAsStream = getClass().getResourceAsStream('/' + TEST_DATASET_AUTHORITY_VALIDATION_FILE_NAME);
        List<ValidationReportEntry> validationReportEntries = validateXPath("FLB", xPathValidator, netexXMLParser, testDatasetAsStream);
        Assertions.assertFalse(validationReportEntries.isEmpty());
        Assertions.assertTrue(validationReportEntries.stream().allMatch(validationReportEntry -> validationReportEntry.getFileName().endsWith(".xml")));
    }

    @Test
    void testDayTypeAllAssigned() throws IOException {
        InputStream testDatasetAsStream = getClass().getResourceAsStream('/' + TEST_DATASET_AUTHORITY_VALIDATION_FILE_NAME);
        List<ValidationReportEntry> validationReportEntries = validateXPath("FLB", xPathValidator, netexXMLParser, testDatasetAsStream);
        Assertions.assertTrue(validationReportEntries.stream().noneMatch(validationReportEntry -> validationReportEntry.getName().equals(validationConfigLoader.getValidationRuleConfig("SERVICE_CALENDAR_1").getName())));

    }

    @Test
    void testDayTypeNotAssigned() throws IOException {
        InputStream testDatasetAsStream = getClass().getResourceAsStream('/' + TEST_DATASET_DAY_TYPE_NOT_ASSIGNED);
        List<ValidationReportEntry> validationReportEntries = validateXPath("FLB", xPathValidator, netexXMLParser, testDatasetAsStream);
        Assertions.assertFalse(validationReportEntries.isEmpty());
        Assertions.assertTrue(validationReportEntries.stream().anyMatch(validationReportEntry -> validationReportEntry.getName().equals(validationConfigLoader.getValidationRuleConfig("SERVICE_CALENDAR_1").getName())));
    }

    @Test
    void testValidColourCoding() throws IOException {
        InputStream testDatasetAsStream = getClass().getResourceAsStream('/' + TEST_DATASET_COLOUR_VALID_CODING_FILE_NAME);
        List<ValidationReportEntry> validationReportEntries = validateXPath("ENT", xPathValidator, netexXMLParser, testDatasetAsStream);
        Assertions.assertTrue(validationReportEntries.stream().noneMatch(validationReportEntry -> validationReportEntry.getName().equals(validationConfigLoader.getValidationRuleConfig("LINE_8").getName())));
        Assertions.assertTrue(validationReportEntries.stream().noneMatch(validationReportEntry -> validationReportEntry.getName().equals(validationConfigLoader.getValidationRuleConfig("LINE_9").getName())));

    }

    @Test
    void testInvalidColourCodingLength() throws IOException {
        InputStream testDatasetAsStream = getClass().getResourceAsStream('/' + TEST_DATASET_COLOUR_INVALID_CODING_LENGTH_FILE_NAME);
        List<ValidationReportEntry> validationReportEntries = validateXPath("ENT", xPathValidator, netexXMLParser, testDatasetAsStream);
        Assertions.assertFalse(validationReportEntries.isEmpty());
        Assertions.assertTrue(validationReportEntries.stream().anyMatch(validationReportEntry -> validationReportEntry.getName().equals(validationConfigLoader.getValidationRuleConfig("LINE_8").getName())));
        Assertions.assertTrue(validationReportEntries.stream().noneMatch(validationReportEntry -> validationReportEntry.getName().equals(validationConfigLoader.getValidationRuleConfig("LINE_9").getName())));
    }

    @Test
    void testInvalidColourCodingValue() throws IOException {
        InputStream testDatasetAsStream = getClass().getResourceAsStream('/' + TEST_DATASET_COLOUR_INVALID_CODING_VALUE_FILE_NAME);
        List<ValidationReportEntry> validationReportEntries = validateXPath("ENT", xPathValidator, netexXMLParser, testDatasetAsStream);
        Assertions.assertFalse(validationReportEntries.isEmpty());
        Assertions.assertTrue(validationReportEntries.stream().anyMatch(validationReportEntry -> validationReportEntry.getName().equals(validationConfigLoader.getValidationRuleConfig("LINE_9").getName())));
        Assertions.assertTrue(validationReportEntries.stream().noneMatch(validationReportEntry -> validationReportEntry.getName().equals(validationConfigLoader.getValidationRuleConfig("LINE_8").getName())));
    }

    @Test
    void testDatedServiceJourneyMultipleReferenceToTheSameDatedServiceJourney() throws IOException {
        InputStream testDatasetAsStream = getClass().getResourceAsStream('/' + TEST_DSJ_MULTIPLE_REFERENCE_TO_SAME_DSJ);
        List<ValidationReportEntry> validationReportEntries = validateXPath("ENT", xPathValidator, netexXMLParser, testDatasetAsStream);
        Assertions.assertFalse(validationReportEntries.isEmpty());
        Assertions.assertTrue(validationReportEntries.stream().anyMatch(validationReportEntry -> validationReportEntry.getName().equals(validationConfigLoader.getValidationRuleConfig("DATED_SERVICE_JOURNEY_5").getName())));
    }

    @Test
    void testNonNumericNetexVersion() throws IOException {
        InputStream testDatasetAsStream = getClass().getResourceAsStream('/' + TEST_NON_NUMERIC_NETEX_VERSION);
        List<ValidationReportEntry> validationReportEntries = validateXPath("FLB", xPathValidator, netexXMLParser, testDatasetAsStream);
        Assertions.assertFalse(validationReportEntries.isEmpty());
        Assertions.assertTrue(validationReportEntries.stream().anyMatch(validationReportEntry -> validationReportEntry.getName().equals(validationConfigLoader.getValidationRuleConfig("VERSION_NON_NUMERIC").getName())));
    }

}
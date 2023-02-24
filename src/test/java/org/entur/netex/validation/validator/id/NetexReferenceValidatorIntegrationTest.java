package org.entur.netex.validation.validator.id;

import org.entur.netex.validation.configuration.DefaultValidationConfigLoader;
import org.entur.netex.validation.validator.DefaultValidationEntryFactory;
import org.entur.netex.validation.validator.ValidationReport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.entur.netex.validation.validator.ValidatorTestUtil.getReport;

class NetexReferenceValidatorIntegrationTest {

    private static final String TEST_DATASET_FILE_NAME = "rb_flb-aggregated-netex.zip";
    private static final String TEST_DATASET_INVALID_REFERENCE_FILE_NAME = "rb_flb-aggregated-netex-invalid-reference.zip";
    private static final String TEST_DATASET_CODESPACE = "FLB";
    public static final String TEST_REPORT_ID = "report-id-test";


    @Test
    void testValidReference() throws IOException {
        NeTexReferenceValidator neTexReferenceValidator = createValidator();
        ValidationReport aggregatedValidationReport = getReport(TEST_DATASET_CODESPACE, TEST_REPORT_ID, TEST_DATASET_FILE_NAME, neTexReferenceValidator);
        Assertions.assertTrue(aggregatedValidationReport.getValidationReportEntries().isEmpty());
    }

    @Test
    void testInvalidReference() throws IOException {
        NeTexReferenceValidator neTexReferenceValidator = createValidator();
        ValidationReport aggregatedValidationReport = getReport(TEST_DATASET_CODESPACE, TEST_REPORT_ID, TEST_DATASET_INVALID_REFERENCE_FILE_NAME, neTexReferenceValidator);
        Assertions.assertFalse(aggregatedValidationReport.getValidationReportEntries().isEmpty());
    }

    private NeTexReferenceValidator createValidator() {
        NetexIdRepository netexIdRepository = new DefaultNetexIdRepository();
        ExternalReferenceValidator acceptAllStopPlacesAndQuays = externalIdsToValidate -> externalIdsToValidate.stream().filter(e -> e.getId().contains(":Quay:") || e.getId().contains(":StopPlace:")).collect(Collectors.toSet());
        return new NeTexReferenceValidator(netexIdRepository, List.of(acceptAllStopPlacesAndQuays), new DefaultValidationEntryFactory(new DefaultValidationConfigLoader()));
    }
}

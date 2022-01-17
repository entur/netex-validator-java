package org.entur.netex.validation.validator.id;

import org.entur.netex.validation.validator.ValidationReport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.entur.netex.validation.validator.ValidatorTestUtil.getReport;

class NetexIdUniquenessValidatorTest {

    private static final String TEST_DATASET_FILE_NAME = "rb_flb-aggregated-netex.zip";
    private static final String TEST_DATASET_DUPLICATE_ID_FILE_NAME = "rb_flb-aggregated-netex-duplicate-id.zip";
    private static final String TEST_DATASET_CODESPACE = "FLB";
    public static final String TEST_REPORT_ID = "report-id-test";


    @Test
    void testNoDuplicate() throws IOException {

        NetexIdRepository netexIdRepository = new DefaultNetexIdRepository();
        NetexIdUniquenessValidator netexIdUniquenessValidator = new NetexIdUniquenessValidator(netexIdRepository);

        ValidationReport aggregatedValidationReport = getReport(TEST_DATASET_CODESPACE, TEST_REPORT_ID, TEST_DATASET_FILE_NAME, netexIdUniquenessValidator);
        Assertions.assertTrue(aggregatedValidationReport.getValidationReportEntries().isEmpty());
    }

    @Test
    void testDuplicate() throws IOException {

        NetexIdRepository netexIdRepository = new DefaultNetexIdRepository();
        NetexIdUniquenessValidator netexIdUniquenessValidator = new NetexIdUniquenessValidator(netexIdRepository);

        ValidationReport aggregatedValidationReport = getReport(TEST_DATASET_CODESPACE, TEST_REPORT_ID, TEST_DATASET_DUPLICATE_ID_FILE_NAME, netexIdUniquenessValidator);
        Assertions.assertFalse(aggregatedValidationReport.getValidationReportEntries().isEmpty());
    }
}

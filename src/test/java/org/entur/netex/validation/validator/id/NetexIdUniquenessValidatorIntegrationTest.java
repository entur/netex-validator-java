package org.entur.netex.validation.validator.id;

import static org.entur.netex.validation.validator.ValidatorTestUtil.getReport;

import java.io.IOException;
import org.entur.netex.validation.configuration.DefaultValidationConfigLoader;
import org.entur.netex.validation.validator.DefaultValidationEntryFactory;
import org.entur.netex.validation.validator.ValidationReport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NetexIdUniquenessValidatorIntegrationTest {

  private static final String TEST_DATASET_FILE_NAME =
    "rb_flb-aggregated-netex.zip";
  private static final String TEST_DATASET_DUPLICATE_ID_FILE_NAME =
    "rb_flb-aggregated-netex-duplicate-id.zip";
  private static final String TEST_DATASET_CODESPACE = "FLB";
  public static final String TEST_REPORT_ID = "report-id-test";

  @Test
  void testNoDuplicate() throws IOException {
    NetexIdUniquenessValidator netexIdUniquenessValidator = createValidator();
    ValidationReport aggregatedValidationReport = getReport(
      TEST_DATASET_CODESPACE,
      TEST_REPORT_ID,
      TEST_DATASET_FILE_NAME,
      netexIdUniquenessValidator
    );
    Assertions.assertTrue(
      aggregatedValidationReport.getValidationReportEntries().isEmpty()
    );
  }

  @Test
  void testDuplicate() throws IOException {
    NetexIdUniquenessValidator netexIdUniquenessValidator = createValidator();
    ValidationReport aggregatedValidationReport = getReport(
      TEST_DATASET_CODESPACE,
      TEST_REPORT_ID,
      TEST_DATASET_DUPLICATE_ID_FILE_NAME,
      netexIdUniquenessValidator
    );
    Assertions.assertFalse(
      aggregatedValidationReport.getValidationReportEntries().isEmpty()
    );
  }

  private NetexIdUniquenessValidator createValidator() {
    return new NetexIdUniquenessValidator(
      new DefaultNetexIdRepository(),
      new DefaultValidationEntryFactory(new DefaultValidationConfigLoader())
    );
  }
}

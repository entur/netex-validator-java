package org.entur.netex.validation.validator.id;

import java.util.List;
import java.util.Set;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntryFactory;
import org.entur.netex.validation.validator.ValidationReportEntrySeverity;
import org.entur.netex.validation.validator.xpath.ValidationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NetexIdUniquenessValidatorTest {

  private static final String TEST_CODESPACE = "TST";
  private static final String TEST_VALIDATION_REPORT_ID =
    "TEST_VALIDATION_REPORT_ID";
  private static final String TEST_DUPLICATED_ID = "XXX:YY:1";

  private static final String TEST_NOT_DUPLICATED_ID = "XXX:YY:2";
  private NetexIdUniquenessValidator netexIdUniquenessValidator;
  private ValidationReport validationReport;

  @BeforeEach
  void setUpTest() {
    ValidationReportEntryFactory validationReportEntryFactory = (
        code,
        validationReportEntryMessage,
        dataLocation
      ) ->
      new ValidationReportEntry(
        validationReportEntryMessage,
        code,
        ValidationReportEntrySeverity.INFO
      );
    NetexIdRepository netexIdRepository = new NetexIdRepository() {
      @Override
      public Set<String> getDuplicateNetexIds(
        String reportId,
        String filename,
        Set<String> localIds
      ) {
        if (localIds.contains(TEST_DUPLICATED_ID)) {
          return Set.of(TEST_DUPLICATED_ID);
        } else {
          return Set.of();
        }
      }

      @Override
      public Set<String> getSharedNetexIds(String reportId) {
        return null;
      }

      @Override
      public void addSharedNetexIds(
        String reportId,
        Set<IdVersion> commonIds
      ) {}

      @Override
      public void cleanUp(String reportId) {}
    };
    netexIdUniquenessValidator =
      new NetexIdUniquenessValidator(
        netexIdRepository,
        validationReportEntryFactory
      );
    validationReport =
      new ValidationReport(TEST_CODESPACE, TEST_VALIDATION_REPORT_ID);
  }

  @Test
  void testDuplicatedIdInLineFile() {
    IdVersion idVersion = new IdVersion(
      TEST_DUPLICATED_ID,
      null,
      "YY",
      null,
      null,
      0,
      0
    );
    Set<IdVersion> localIds = Set.of(idVersion);
    ValidationContext validationContext = new ValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      List.of()
    );
    netexIdUniquenessValidator.validate(validationReport, validationContext);
    Assertions.assertFalse(
      validationReport.getValidationReportEntries().isEmpty()
    );
    Assertions.assertEquals(
      NetexIdUniquenessValidator.RULE_CODE_NETEX_ID_1,
      validationReport
        .getValidationReportEntries()
        .stream()
        .findFirst()
        .orElseThrow()
        .getName()
    );
  }

  @Test
  void testDuplicatedIdInCommonFile() {
    IdVersion idVersion = new IdVersion(
      TEST_DUPLICATED_ID,
      null,
      "YY",
      null,
      null,
      0,
      0
    );
    Set<IdVersion> localIds = Set.of(idVersion);
    ValidationContext validationContext = new ValidationContext(
      null,
      null,
      TEST_CODESPACE,
      "_common.xml",
      localIds,
      List.of()
    );
    netexIdUniquenessValidator.validate(validationReport, validationContext);
    Assertions.assertFalse(
      validationReport.getValidationReportEntries().isEmpty()
    );
    Assertions.assertEquals(
      NetexIdUniquenessValidator.RULE_CODE_NETEX_ID_10,
      validationReport
        .getValidationReportEntries()
        .stream()
        .findFirst()
        .orElseThrow()
        .getName()
    );
  }

  @Test
  void testNoDuplicateId() {
    IdVersion idVersion = new IdVersion(
      TEST_NOT_DUPLICATED_ID,
      null,
      "YY",
      null,
      null,
      0,
      0
    );
    Set<IdVersion> localIds = Set.of(idVersion);
    ValidationContext validationContext = new ValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      List.of()
    );
    netexIdUniquenessValidator.validate(validationReport, validationContext);
    Assertions.assertTrue(
      validationReport.getValidationReportEntries().isEmpty()
    );
  }
}

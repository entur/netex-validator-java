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

class VersionOnRefToLocalNetexIdValidatorTest {

  private static final String TEST_CODESPACE = "TST";
  private static final String TEST_VALIDATION_REPORT_ID =
    "TEST_VALIDATION_REPORT_ID";
  private static final String TEST_REFERENCED_ID = "XXX:YY:1";

  private VersionOnRefToLocalNetexIdValidator versionOnRefToLocalNetexIdValidator;
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
    versionOnRefToLocalNetexIdValidator =
      new VersionOnRefToLocalNetexIdValidator(validationReportEntryFactory);
    validationReport =
      new ValidationReport(TEST_CODESPACE, TEST_VALIDATION_REPORT_ID);
  }

  @Test
  void testLocalReferenceWithoutVersion() {
    IdVersion idVersionLocalRef = new IdVersion(
      TEST_REFERENCED_ID,
      null,
      "YYRef",
      null,
      null,
      0,
      0
    );
    List<IdVersion> localRefs = List.of(idVersionLocalRef);
    IdVersion idVersionLocal = new IdVersion(
      TEST_REFERENCED_ID,
      null,
      "YY",
      null,
      null,
      0,
      0
    );
    Set<IdVersion> localIds = Set.of(idVersionLocal);
    ValidationContext validationContext = new ValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      localRefs
    );
    versionOnRefToLocalNetexIdValidator.validate(
      validationReport,
      validationContext
    );
    Assertions.assertFalse(
      validationReport.getValidationReportEntries().isEmpty()
    );
    Assertions.assertEquals(
      VersionOnRefToLocalNetexIdValidator.RULE_CODE_NETEX_ID_9,
      validationReport
        .getValidationReportEntries()
        .stream()
        .findFirst()
        .orElseThrow()
        .getName()
    );
  }

  @Test
  void testLocalReferenceWithVersion() {
    IdVersion idVersionLocalRef = new IdVersion(
      TEST_REFERENCED_ID,
      "1",
      "YYRef",
      null,
      null,
      0,
      0
    );
    List<IdVersion> localRefs = List.of(idVersionLocalRef);
    IdVersion idVersionLocal = new IdVersion(
      TEST_REFERENCED_ID,
      "1",
      "YY",
      null,
      null,
      0,
      0
    );
    Set<IdVersion> localIds = Set.of(idVersionLocal);
    ValidationContext validationContext = new ValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      localRefs
    );
    versionOnRefToLocalNetexIdValidator.validate(
      validationReport,
      validationContext
    );
    Assertions.assertTrue(
      validationReport.getValidationReportEntries().isEmpty()
    );
  }
}

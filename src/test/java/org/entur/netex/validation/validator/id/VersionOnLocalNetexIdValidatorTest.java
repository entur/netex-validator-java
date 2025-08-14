package org.entur.netex.validation.validator.id;

import java.util.List;
import java.util.Set;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VersionOnLocalNetexIdValidatorTest {

  private static final String TEST_CODESPACE = "TST";
  private static final String TEST_VALIDATION_REPORT_ID = "TEST_VALIDATION_REPORT_ID";
  private static final String TEST_REFERENCED_ID = "XXX:YY:1";

  private VersionOnLocalNetexIdValidator versionOnLocalNetexIdValidator;
  private ValidationReport validationReport;

  @BeforeEach
  void setUpTest() {
    versionOnLocalNetexIdValidator = new VersionOnLocalNetexIdValidator();
    validationReport = new ValidationReport(TEST_CODESPACE, TEST_VALIDATION_REPORT_ID);
  }

  @Test
  void testLocalReferenceWithoutVersion() {
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
    XPathValidationContext xPathValidationContext = new XPathValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      List.of(),
      validationReport.getValidationReportId()
    );
    List<ValidationIssue> validationIssues = versionOnLocalNetexIdValidator.validate(
      xPathValidationContext
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertEquals(
      VersionOnLocalNetexIdValidator.RULE,
      validationIssues.stream().findFirst().orElseThrow().rule()
    );
  }

  @Test
  void testLocalReferenceWithVersion() {
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
    XPathValidationContext xPathValidationContext = new XPathValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      List.of(),
      validationReport.getValidationReportId()
    );
    List<ValidationIssue> validationIssues = versionOnLocalNetexIdValidator.validate(
      xPathValidationContext
    );
    Assertions.assertTrue(validationIssues.isEmpty());
  }
}

package org.entur.netex.validation.validator.id;

import java.util.List;
import java.util.Set;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VersionOnRefToLocalNetexIdValidatorTest {

  private static final String TEST_CODESPACE = "TST";
  private static final String TEST_VALIDATION_REPORT_ID = "TEST_VALIDATION_REPORT_ID";
  private static final String TEST_REFERENCED_ID = "XXX:YY:1";

  private VersionOnRefToLocalNetexIdValidator versionOnRefToLocalNetexIdValidator;
  private ValidationReport validationReport;

  @BeforeEach
  void setUpTest() {
    versionOnRefToLocalNetexIdValidator = new VersionOnRefToLocalNetexIdValidator();
    validationReport = new ValidationReport(TEST_CODESPACE, TEST_VALIDATION_REPORT_ID);
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
    XPathValidationContext xPathValidationContext = new XPathValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      localRefs,
      validationReport.getValidationReportId()
    );
    List<ValidationIssue> validationIssues = versionOnRefToLocalNetexIdValidator.validate(
      xPathValidationContext
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertEquals(
      VersionOnRefToLocalNetexIdValidator.RULE,
      validationIssues.stream().findFirst().orElseThrow().rule()
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
    XPathValidationContext xPathValidationContext = new XPathValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      localRefs,
      validationReport.getValidationReportId()
    );
    List<ValidationIssue> validationIssues = versionOnRefToLocalNetexIdValidator.validate(
      xPathValidationContext
    );
    Assertions.assertTrue(validationIssues.isEmpty());
  }
}

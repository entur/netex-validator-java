package org.entur.netex.validation.validator.id;

import java.util.List;
import java.util.Set;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReferenceToValidEntityTypeValidatorTest {

  private static final String TEST_CODESPACE = "TST";
  private static final String TEST_VALIDATION_REPORT_ID = "TEST_VALIDATION_REPORT_ID";
  private static final String TEST_REFERENCED_ID = "XXX:YY:1";
  private static final String TEST_INVALID_REFERENCED_ID = "This is an invalid NeTEx id";
  private ReferenceToValidEntityTypeValidator referenceToValidEntityTypeValidator;
  private ValidationReport validationReport;

  @BeforeEach
  void setUpTest() {
    referenceToValidEntityTypeValidator = new ReferenceToValidEntityTypeValidator();
    validationReport = new ValidationReport(TEST_CODESPACE, TEST_VALIDATION_REPORT_ID);
  }

  @Test
  void testInvalidReferenceType() {
    IdVersion idVersion = new IdVersion(TEST_REFERENCED_ID, null, "YY", null, null, 0, 0);
    List<IdVersion> localRefs = List.of(idVersion);
    XPathValidationContext xPathValidationContext = new XPathValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      Set.of(),
      localRefs,
      validationReport.getValidationReportId()
    );
    List<ValidationIssue> validationIssues = referenceToValidEntityTypeValidator.validate(
      xPathValidationContext
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertEquals(
      ReferenceToValidEntityTypeValidator.RULE_INVALID_REFERENCE,
      validationIssues.stream().findFirst().orElseThrow().rule()
    );
  }

  @Test
  void testInvalidReferenceStructure() {
    IdVersion idVersion = new IdVersion(
      TEST_INVALID_REFERENCED_ID,
      null,
      "YY",
      null,
      null,
      0,
      0
    );
    List<IdVersion> localRefs = List.of(idVersion);
    XPathValidationContext xPathValidationContext = new XPathValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      Set.of(),
      localRefs,
      validationReport.getValidationReportId()
    );
    List<ValidationIssue> validationIssues = referenceToValidEntityTypeValidator.validate(
      xPathValidationContext
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertEquals(
      ReferenceToValidEntityTypeValidator.RULE_INVALID_ID_STRUCTURE,
      validationIssues.stream().findFirst().orElseThrow().rule()
    );
  }

  @Test
  void testValidReferenceType() {
    IdVersion idVersion = new IdVersion(
      TEST_REFERENCED_ID,
      null,
      "YYRef",
      null,
      null,
      0,
      0
    );
    List<IdVersion> localRefs = List.of(idVersion);
    XPathValidationContext xPathValidationContext = new XPathValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      Set.of(),
      localRefs,
      validationReport.getValidationReportId()
    );
    List<ValidationIssue> validationIssues = referenceToValidEntityTypeValidator.validate(
      xPathValidationContext
    );
    Assertions.assertTrue(validationIssues.isEmpty());
  }
}

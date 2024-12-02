package org.entur.netex.validation.validator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SimpleValidationEntryFactoryTest {

  private static final String RULE_CODE = "code";
  private static final String RULE_NAME = "name";
  private static final String RULE_MESSAGE = "message";
  private static final Severity RULE_SEVERITY = Severity.ERROR;

  @Test
  void testBuildValidationReportEntry() {
    SimpleValidationEntryFactory factory = new SimpleValidationEntryFactory();
    ValidationRule validationRule = new ValidationRule(
      RULE_CODE,
      RULE_NAME,
      RULE_MESSAGE,
      RULE_SEVERITY
    );
    DataLocation datalocation = DataLocation.EMPTY_LOCATION;
    ValidationReportEntry validationReportEntry =
      factory.createValidationReportEntry(
        new ValidationIssue(validationRule, datalocation)
      );
    assertNotNull(validationReportEntry);
    assertEquals(RULE_NAME, validationReportEntry.getName());
    assertEquals(RULE_MESSAGE, validationReportEntry.getMessage());
    assertEquals(RULE_SEVERITY, validationReportEntry.getSeverity());
  }
}

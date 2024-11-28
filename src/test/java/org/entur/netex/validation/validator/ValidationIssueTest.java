package org.entur.netex.validation.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidationIssueTest {

  public static final String LINE_ID = "ENT:Line:1";

  @Test
  void testMessageFormat() {
    ValidationRule validationRule = new ValidationRule(
      "LINE_NAME",
      "A line must have a name",
      "Line %s does not have a name",
      Severity.ERROR
    );
    DataLocation dataLocation = new DataLocation(LINE_ID, "netex.xml", 1, 2);
    ValidationIssue validationIssue = new ValidationIssue(
      validationRule,
      dataLocation,
      LINE_ID
    );
    Assertions.assertEquals(
      "Line " + LINE_ID + " does not have a name",
      validationIssue.message()
    );
  }
}

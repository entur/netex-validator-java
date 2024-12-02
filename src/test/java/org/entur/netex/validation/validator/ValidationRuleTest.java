package org.entur.netex.validation.validator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ValidationRuleTest {

  @Test
  void testEqualityOnCode() {
    ValidationRule v1 = new ValidationRule("code", "name", Severity.ERROR);
    ValidationRule v2 = new ValidationRule("code", "other name", Severity.INFO);
    assertEquals(v1, v2);
  }
}

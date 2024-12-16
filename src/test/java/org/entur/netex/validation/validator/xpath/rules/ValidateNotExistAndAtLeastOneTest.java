package org.entur.netex.validation.validator.xpath.rules;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidateNotExistAndAtLeastOneTest {

  private static final String NETEX_FRAGMENT =
    """
  <ServiceFrame xmlns="http://www.netex.org.uk/netex" id="ENT:ServiceFrame:1" version="2223">
  <lines>
    <Line id="ENT:Line:2_1" version="2223">
    </Line>
  </lines>
</ServiceFrame>
""";

  private static final ValidationRule VALIDATION_RULE = new ValidationRule(
    "RULE_CODE",
    "RULE_NAME",
    Severity.ERROR
  );

  private XPathRuleValidationContext xpathRuleValidationContext;

  @BeforeEach
  void setUp() {
    xpathRuleValidationContext =
      TestValidationContextBuilder.ofNetexFragment(NETEX_FRAGMENT).build();
  }

  @Test
  void validateNotExistMatch() {
    ValidateNotExist validateNotExist = new ValidateNotExist(
      "lines/Line",
      VALIDATION_RULE
    );

    List<ValidationIssue> validationIssues = validateNotExist.validate(
      xpathRuleValidationContext
    );
    assertEquals(1, validationIssues.size());
    assertEquals(VALIDATION_RULE, validationIssues.get(0).rule());
  }

  @Test
  void validateNotExistNoMatch() {
    ValidateNotExist validateNotExist = new ValidateNotExist(
      "lines/FlexibleLine",
      VALIDATION_RULE
    );

    List<ValidationIssue> validationIssues = validateNotExist.validate(
      xpathRuleValidationContext
    );
    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void validateAtLeastOneMatch() {
    ValidateAtLeastOne validateAtLeastOne = new ValidateAtLeastOne(
      "lines/FlexibleLine",
      VALIDATION_RULE
    );

    List<ValidationIssue> validationIssues = validateAtLeastOne.validate(
      xpathRuleValidationContext
    );
    assertEquals(1, validationIssues.size());
    assertEquals(VALIDATION_RULE, validationIssues.get(0).rule());
  }

  @Test
  void validateAtLeastOneNoMatch() {
    ValidateAtLeastOne validateAtLeastOne = new ValidateAtLeastOne(
      "lines/Line",
      VALIDATION_RULE
    );

    List<ValidationIssue> validationIssues = validateAtLeastOne.validate(
      xpathRuleValidationContext
    );
    assertTrue(validationIssues.isEmpty());
  }
}

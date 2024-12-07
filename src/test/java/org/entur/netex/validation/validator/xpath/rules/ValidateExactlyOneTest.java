package org.entur.netex.validation.validator.xpath.rules;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.entur.netex.validation.validator.xpath.support.XPathTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidateExactlyOneTest {

  private static final String NETEX_FRAGMENT =
    """
      <ServiceFrame xmlns="http://www.netex.org.uk/netex" id="ENT:ServiceFrame:1" version="2223">
          <lines>
            <Line id="ENT:Line:1" version="2223"/>
            <FlexibleLine id="ENT:FlexibleLine:1" version="2223"/>
            <FlexibleLine id="ENT:FlexibleLine:2" version="2223"/>
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
      XPathTestSupport.validationContext(NETEX_FRAGMENT);
  }

  @Test
  void validateExactlyOneNoMatch() {
    ValidateExactlyOne validateExactlyOne = new ValidateExactlyOne(
      "lines/Line",
      VALIDATION_RULE
    );

    List<ValidationIssue> validationIssues = validateExactlyOne.validate(
      xpathRuleValidationContext
    );
    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void validateExactlyOneMatchMoreThanOne() {
    ValidateExactlyOne validateExactlyOne = new ValidateExactlyOne(
      "lines/FlexibleLine",
      VALIDATION_RULE
    );

    List<ValidationIssue> validationIssues = validateExactlyOne.validate(
      xpathRuleValidationContext
    );
    assertEquals(1, validationIssues.size());
    assertEquals(VALIDATION_RULE, validationIssues.get(0).rule());
  }

  @Test
  void validateExactlyOneMatchLessThanOne() {
    ValidateExactlyOne validateExactlyOne = new ValidateExactlyOne(
      "routes/Route",
      VALIDATION_RULE
    );

    List<ValidationIssue> validationIssues = validateExactlyOne.validate(
      xpathRuleValidationContext
    );
    assertEquals(1, validationIssues.size());
    assertEquals(VALIDATION_RULE, validationIssues.get(0).rule());
  }
}

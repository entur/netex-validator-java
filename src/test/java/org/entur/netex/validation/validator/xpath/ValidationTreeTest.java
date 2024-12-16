package org.entur.netex.validation.validator.xpath;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.test.xpath.support.XPathTestSupport;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidationTreeTest {

  private static final String RULE_CODE = "NO_LINE";
  private static final XPathValidationRule RULE_NO_LINE = new ValidateNotExist(
    "lines/Line",
    RULE_CODE,
    "Line Not allowed",
    "Illegal element Line",
    Severity.ERROR
  );

  private static final String NETEX_FRAGMENT =
    """
<ServiceFrame xmlns="http://www.netex.org.uk/netex" id="ATB:ServiceFrame:1" version="2223">
  <lines>
    <Line id="ATB:Line:2_1" version="2223">
    </Line>
  </lines>
</ServiceFrame>
""";
  private ValidationTree tree;
  private XPathRuleValidationContext xpathValidationContext;

  @BeforeEach
  void setUp() {
    tree = new ValidationTree("Service Frame", "ServiceFrame");
    tree.addValidationRule(RULE_NO_LINE);

    xpathValidationContext =
      TestValidationContextBuilder.ofNetexFragment(NETEX_FRAGMENT).build();
  }

  @Test
  void testValidate() {
    List<ValidationIssue> validationIssues = tree.validate(
      xpathValidationContext
    );

    assertFalse(validationIssues.isEmpty());
  }

  @Test
  void testValidateWithRuleFilterMatch() {
    List<ValidationIssue> validationIssues = tree.validate(
      xpathValidationContext,
      RULE_CODE
    );

    assertFalse(validationIssues.isEmpty());
  }

  @Test
  void testValidateWithRuleFilterNoMatch() {
    List<ValidationIssue> validationIssues = tree.validate(
      xpathValidationContext,
      "another code"
    );

    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testTreeFilterMatch() {
    ValidationTree root = new ValidationTree("Service Frame", "ServiceFrame");

    ValidationTree subTree = new ValidationTree(
      "Subtree",
      ".",
      context -> XPathTestSupport.TEST_CODESPACE.equals(context.getCodespace())
    );
    subTree.addValidationRule(RULE_NO_LINE);
    root.addSubTree(subTree);
    List<ValidationIssue> validationIssues = root.validate(
      xpathValidationContext
    );
    assertFalse(validationIssues.isEmpty());
  }

  @Test
  void testTreeFilterNoMatch() {
    ValidationTree root = new ValidationTree("Service Frame", "ServiceFrame");

    ValidationTree subTree = new ValidationTree(
      "Subtree",
      ".",
      context -> !XPathTestSupport.TEST_CODESPACE.equals(context.getCodespace())
    );
    subTree.addValidationRule(RULE_NO_LINE);
    root.addSubTree(subTree);
    List<ValidationIssue> validationIssues = root.validate(
      xpathValidationContext
    );
    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testDescribe() {
    tree.addValidationRule(RULE_NO_LINE);
    Set<XPathValidationRule> rules = tree.getRules();
    assertEquals(1, rules.size());
    assertEquals(RULE_NO_LINE, rules.iterator().next());
    assertEquals(RULE_NO_LINE, tree.getRule(RULE_CODE));

    String description = tree.describe();
    assertNotNull(description);
    assertTrue(description.contains(RULE_NO_LINE.rule().code()));
    assertTrue(description.contains(RULE_NO_LINE.rule().name()));

    Set<String> ruleMessages = tree.getRuleMessages();
    assertEquals(1, ruleMessages.size());
    String message = ruleMessages.iterator().next();
    assertTrue(message.contains(RULE_NO_LINE.rule().code()));
    assertTrue(message.contains(RULE_NO_LINE.rule().name()));
  }
}

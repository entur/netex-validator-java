package org.entur.netex.validation.validator.xpath;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;
import org.junit.jupiter.api.Test;

class ValidationTreeTest {

  public static final String RULE_CODE = "NO_BLOCK";

  @Test
  void testDescribe() {
    ValidationTree tree = new ValidationTree("Root", "/");
    XPathValidationRule xPathValidationRule = new ValidateNotExist(
      "//Block",
      "Blocks are not present",
      RULE_CODE
    );
    tree.addValidationRule(xPathValidationRule);
    Set<XPathValidationRule> rules = tree.getRules();
    assertEquals(1, rules.size());
    assertEquals(xPathValidationRule, rules.iterator().next());
    assertEquals(xPathValidationRule, tree.getRule(RULE_CODE));

    String description = tree.describe();
    assertNotNull(description);
    assertTrue(description.contains(xPathValidationRule.rule().code()));
    assertTrue(description.contains(xPathValidationRule.rule().name()));

    Set<String> ruleMessages = tree.getRuleMessages();
    assertEquals(1, ruleMessages.size());
    String message = ruleMessages.iterator().next();
    assertTrue(message.contains(xPathValidationRule.rule().code()));
    assertTrue(message.contains(xPathValidationRule.rule().name()));
  }
}

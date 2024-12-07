package org.entur.netex.validation.validator.xpath.tree;

import java.util.ArrayList;
import java.util.List;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.entur.netex.validation.validator.xpath.XPathValidationRule;

public class ValidationTreeBuilder {

  private final String context;
  private final String name;
  private final List<XPathValidationRule> rules = new ArrayList<>();
  private final List<XPathValidationRule> rulesForCommonFile =
    new ArrayList<>();
  private final List<XPathValidationRule> rulesForLineFile = new ArrayList<>();

  public ValidationTreeBuilder(String context, String name) {
    this.context = context;
    this.name = name;
  }

  public ValidationTreeBuilder withRule(XPathValidationRule rule) {
    rules.add(rule);
    return this;
  }

  public ValidationTreeBuilder withRuleForCommonFile(XPathValidationRule rule) {
    rulesForCommonFile.add(rule);
    return this;
  }

  public ValidationTreeBuilder withRuleForLineFile(XPathValidationRule rule) {
    rulesForLineFile.add(rule);
    return this;
  }

  public ValidationTree build() {
    ValidationTree validationTree = new ValidationTree(name, context);

    if (!rules.isEmpty()) {
      ValidationTree lineAndCommonTree = new ValidationTree(
        name + " (Line and Common File)",
        "."
      );
      lineAndCommonTree.addValidationRules(rules);
      validationTree.addSubTree(lineAndCommonTree);
    }

    if (!rulesForCommonFile.isEmpty()) {
      ValidationTree commonTree = new ValidationTree(
        name + " (Common File Only)",
        ".",
        XPathRuleValidationContext::isCommonFile
      );
      commonTree.addValidationRules(rulesForCommonFile);
      validationTree.addSubTree(commonTree);
    }

    if (!rulesForLineFile.isEmpty()) {
      ValidationTree commonTree = new ValidationTree(
        name + " (Line File Only)",
        ".",
        XPathRuleValidationContext::isLineFile
      );
      commonTree.addValidationRules(rulesForLineFile);
      validationTree.addSubTree(commonTree);
    }

    return validationTree;
  }
}

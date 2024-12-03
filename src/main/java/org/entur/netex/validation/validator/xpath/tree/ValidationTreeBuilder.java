package org.entur.netex.validation.validator.xpath.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.entur.netex.validation.validator.xpath.XPathValidationRule;

/**
 * Build a validation tree.
 * Rules that should be applied to common files only, line files only or both line files and
 * common files are stored in dedicated sub-trees.
 */
public class ValidationTreeBuilder {

  private final String context;
  private final String name;
  private final Map<String, XPathValidationRule> rules = new HashMap<>();
  private final Map<String, XPathValidationRule> rulesForCommonFile =
    new HashMap<>();
  private final Map<String, XPathValidationRule> rulesForLineFile =
    new HashMap<>();

  private final Map<String, ValidationTreeBuilder> subTreeBuilders =
    new HashMap<>();

  private final Predicate<XPathRuleValidationContext> executionCondition;

  public ValidationTreeBuilder(String name, String context) {
    this(name, context, validationContext -> true);
  }

  public ValidationTreeBuilder(
    String name,
    String context,
    Predicate<XPathRuleValidationContext> executionCondition
  ) {
    this.context = context;
    this.name = name;
    this.executionCondition = executionCondition;
  }

  public static ValidationTreeBuilder empty() {
    return new ValidationTreeBuilder("empty", ".");
  }

  public ValidationTreeBuilder withRule(XPathValidationRule rule) {
    rules.put(rule.rule().code(), rule);
    return this;
  }

  public ValidationTreeBuilder withRuleForCommonFile(XPathValidationRule rule) {
    rulesForCommonFile.put(rule.rule().code(), rule);
    return this;
  }

  public ValidationTreeBuilder withRuleForLineFile(XPathValidationRule rule) {
    rulesForLineFile.put(rule.rule().code(), rule);
    return this;
  }

  public ValidationTreeBuilder withSubTreeBuilder(
    ValidationTreeBuilder subtreeBuilder
  ) {
    subTreeBuilders.put(subtreeBuilder.name, subtreeBuilder);
    return this;
  }

  public ValidationTreeBuilder removeRule(String code) {
    rules.remove(code);
    return this;
  }

  public ValidationTreeBuilder removeRuleForCommonFile(String code) {
    rulesForCommonFile.remove(code);
    return this;
  }

  public ValidationTreeBuilder removeRuleForLineFile(String code) {
    rulesForLineFile.remove(code);
    return this;
  }

  public ValidationTree build() {
    ValidationTree validationTree = new ValidationTree(
      name,
      context,
      executionCondition
    );

    if (!rules.isEmpty()) {
      ValidationTree lineAndCommonTree = new ValidationTree(
        name + " (Line and Common File)",
        "."
      );
      lineAndCommonTree.addValidationRules(rules.values().stream().toList());
      validationTree.addSubTree(lineAndCommonTree);
    }

    if (!rulesForCommonFile.isEmpty()) {
      ValidationTree commonTree = new ValidationTree(
        name + " (Common File Only)",
        ".",
        XPathRuleValidationContext::isCommonFile
      );
      commonTree.addValidationRules(
        rulesForCommonFile.values().stream().toList()
      );
      validationTree.addSubTree(commonTree);
    }

    if (!rulesForLineFile.isEmpty()) {
      ValidationTree lineTree = new ValidationTree(
        name + " (Line File Only)",
        ".",
        XPathRuleValidationContext::isLineFile
      );
      lineTree.addValidationRules(rulesForLineFile.values().stream().toList());
      validationTree.addSubTree(lineTree);
    }

    subTreeBuilders
      .values()
      .stream()
      .map(ValidationTreeBuilder::build)
      .forEach(validationTree::addSubTree);

    return validationTree;
  }
}

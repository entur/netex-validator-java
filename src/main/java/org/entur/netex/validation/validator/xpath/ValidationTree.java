package org.entur.netex.validation.validator.xpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A tree of XPath validation rules to be applied to a NeTEx document. See {@link ValidationRule}.
 * The tree can be structured in subtrees that validate a part of the XML documents.
 * The tree leaves are instances {@link ValidationRule}.
 */
public class ValidationTree {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    ValidationTree.class
  );

  private final String name;
  private final String context;
  private final List<ValidationTree> subTrees;
  private final List<ValidationRule> validationRules;
  private final Predicate<XPathRuleValidationContext> executionCondition;

  /**
   * @param name the name of the validation tree.
   * @param context the XPath context, that is the XPath path on which the rule is applied.
   */
  public ValidationTree(String name, String context) {
    this(name, context, validationContext -> true);
  }

  /**
   * @param name the name of the validation tree.
   * @param context the XPath context, that is the XPath path on which the rule is applied.
   * @param executionCondition condition evaluated at validation-time. The validation tree is executed only if the condition returns true.
   */
  public ValidationTree(
    String name,
    String context,
    Predicate<XPathRuleValidationContext> executionCondition
  ) {
    this.name = name;
    this.context = context;
    this.executionCondition = executionCondition;
    this.validationRules = new ArrayList<>();
    this.subTrees = new ArrayList<>();
  }

  public List<XPathValidationReportEntry> validate(
    XPathRuleValidationContext validationContext
  ) {
    List<XPathValidationReportEntry> xPathValidationReportEntries =
      new ArrayList<>();
    for (ValidationRule validationRule : validationRules) {
      LOGGER.debug(
        "Running validation rule '{}'/'{}'",
        name,
        validationRule.getMessage()
      );
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      xPathValidationReportEntries.addAll(
        validationRule.validate(validationContext)
      );
      stopWatch.stop();
      LOGGER.debug(
        "Validated rule '{}'/'{}' in {} ms",
        name,
        validationRule.getMessage(),
        stopWatch.getTime()
      );
    }
    for (ValidationTree validationSubTree : subTrees) {
      XdmValue subContextNodes = validationContext
        .getNetexXMLParser()
        .selectNodeSet(
          validationSubTree.getContext(),
          validationContext.getXmlNode()
        );
      for (XdmItem xdmItem : subContextNodes) {
        XPathRuleValidationContext validationSubContext =
          new XPathRuleValidationContext(
            (XdmNode) xdmItem,
            validationContext.getNetexXMLParser(),
            validationContext.getCodespace(),
            validationContext.getFileName()
          );
        if (validationSubTree.executionCondition.test(validationSubContext)) {
          LOGGER.debug(
            "Running validation subtree '{}'/'{}'",
            name,
            validationSubTree.getName()
          );
          xPathValidationReportEntries.addAll(
            validationSubTree.validate(validationSubContext)
          );
        } else {
          LOGGER.debug(
            "Skipping validation subtree '{}'/'{}'",
            name,
            validationSubTree.getName()
          );
        }
      }
    }

    return xPathValidationReportEntries;
  }

  /**
   * Return a string representation of the validation tree, for debugging purpose.
   *
   * @return a string representation of the validation tree, for debugging purpose.
   */
  public String describe() {
    return describe(0);
  }

  private String describe(int indentation) {
    StringBuilder builder = new StringBuilder();
    char[] spaces = new char[indentation];
    Arrays.fill(spaces, ' ');
    for (ValidationRule validationRule : validationRules) {
      builder
        .append(spaces)
        .append("[")
        .append(validationRule.getCode())
        .append("] ")
        .append(validationRule.getMessage())
        .append("\n");
    }
    for (ValidationTree validationTree : subTrees) {
      builder.append(validationTree.describe(indentation + 2));
    }
    return builder.toString();
  }

  public Set<String> getRuleMessages() {
    Set<String> rules = new HashSet<>();
    for (ValidationRule validationRule : validationRules) {
      rules.add(
        "[" + validationRule.getCode() + "] " + validationRule.getMessage()
      );
    }
    for (ValidationTree validationTree : subTrees) {
      rules.addAll(validationTree.getRuleMessages());
    }
    return rules;
  }

  public Set<ValidationRule> getRules() {
    Set<ValidationRule> rules = new HashSet<>(validationRules);
    for (ValidationTree validationTree : subTrees) {
      rules.addAll(validationTree.getRules());
    }
    return rules;
  }

  public ValidationRule getRule(String code) {
    return getRules()
      .stream()
      .filter(validationRule -> validationRule.getCode().equals(code))
      .findFirst()
      .orElseThrow(() ->
        new IllegalArgumentException("No rule with code " + code)
      );
  }

  public String printRulesList() {
    AtomicInteger integer = new AtomicInteger(0);
    return getRules()
      .stream()
      .sorted(Comparator.comparing(ValidationRule::getCode))
      .map(validationRule ->
        " | " +
        validationRule.getCode() +
        " | " +
        validationRule.getMessage() +
        " |\n"
      )
      .distinct()
      .map(validationRuleString ->
        " | " + integer.addAndGet(1) + validationRuleString
      )
      .collect(Collectors.joining());
  }

  public void addValidationRule(ValidationRule validationRule) {
    validationRules.add(validationRule);
  }

  public void addValidationRules(List<ValidationRule> validationRules) {
    this.validationRules.addAll(validationRules);
  }

  public boolean removeValidationRule(String ruleCode) {
    return this.validationRules.removeIf(validationRule ->
        validationRule.getCode().equals(ruleCode)
      );
  }

  public void addSubTree(ValidationTree validationTree) {
    subTrees.add(validationTree);
  }

  public String getContext() {
    return context;
  }

  public String getName() {
    return name;
  }
}

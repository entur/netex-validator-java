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
import org.entur.netex.validation.validator.ValidationIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A tree of XPath validation rules to be applied to a NeTEx document. See {@link XPathValidationRule}.
 * The tree can be structured in subtrees that validate a part of the XML documents.
 * The tree leaves are instances {@link XPathValidationRule}.
 */
public class ValidationTree {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    ValidationTree.class
  );

  private final String name;
  private final String context;
  private final List<ValidationTree> subTrees;
  private final List<XPathValidationRule> xPathValidationRules;
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
    this.xPathValidationRules = new ArrayList<>();
    this.subTrees = new ArrayList<>();
  }

  public List<ValidationIssue> validate(
    XPathRuleValidationContext validationContext
  ) {
    List<ValidationIssue> validationIssues = new ArrayList<>();
    for (XPathValidationRule xPathValidationRule : xPathValidationRules) {
      LOGGER.debug(
        "Running validation rule '{}'/'{}'",
        name,
        xPathValidationRule.rule().name()
      );
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      validationIssues.addAll(xPathValidationRule.validate(validationContext));
      stopWatch.stop();
      LOGGER.debug(
        "Validated rule '{}'/'{}' in {} ms",
        name,
        xPathValidationRule.rule().name(),
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
          validationIssues.addAll(
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

    return validationIssues;
  }

  /**
   * Return a string representation of the validation tree, for debugging purpose.
   *
   */
  public String describe() {
    return describe(0);
  }

  private String describe(int indentation) {
    StringBuilder builder = new StringBuilder();
    char[] spaces = new char[indentation];
    Arrays.fill(spaces, ' ');
    for (XPathValidationRule validationRule : xPathValidationRules) {
      builder
        .append(spaces)
        .append("[")
        .append(validationRule.rule().code())
        .append("] ")
        .append(validationRule.rule().name())
        .append("\n");
    }
    for (ValidationTree validationTree : subTrees) {
      builder.append(validationTree.describe(indentation + 2));
    }
    return builder.toString();
  }

  public Set<String> getRuleMessages() {
    Set<String> rules = new HashSet<>();
    for (XPathValidationRule xPathValidationRule : xPathValidationRules) {
      rules.add(
        "[" +
        xPathValidationRule.rule().code() +
        "] " +
        xPathValidationRule.rule().name()
      );
    }
    for (ValidationTree validationTree : subTrees) {
      rules.addAll(validationTree.getRuleMessages());
    }
    return rules;
  }

  public Set<XPathValidationRule> getRules() {
    Set<XPathValidationRule> rules = new HashSet<>(xPathValidationRules);
    for (ValidationTree validationTree : subTrees) {
      rules.addAll(validationTree.getRules());
    }
    return rules;
  }

  public XPathValidationRule getRule(String code) {
    return getRules()
      .stream()
      .filter(validationRule -> validationRule.rule().code().equals(code))
      .findFirst()
      .orElseThrow(() ->
        new IllegalArgumentException("No rule with code " + code)
      );
  }

  public String printRulesList() {
    AtomicInteger integer = new AtomicInteger(0);
    return getRules()
      .stream()
      .sorted(
        Comparator.comparing(xPathValidationRule ->
          xPathValidationRule.rule().code()
        )
      )
      .map(validationRule ->
        " | " +
        validationRule.rule().code() +
        " | " +
        validationRule.rule().name() +
        " |\n"
      )
      .distinct()
      .map(validationRuleString ->
        " | " + integer.addAndGet(1) + validationRuleString
      )
      .collect(Collectors.joining());
  }

  public void addValidationRule(XPathValidationRule validationRule) {
    xPathValidationRules.add(validationRule);
  }

  public void addValidationRules(List<XPathValidationRule> validationRules) {
    this.xPathValidationRules.addAll(validationRules);
  }

  public boolean removeValidationRule(String ruleCode) {
    return this.xPathValidationRules.removeIf(validationRule ->
        validationRule.rule().code().equals(ruleCode)
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

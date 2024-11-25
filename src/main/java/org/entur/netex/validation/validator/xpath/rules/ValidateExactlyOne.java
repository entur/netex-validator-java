package org.entur.netex.validation.validator.xpath.rules;

import java.util.Collections;
import java.util.List;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmValue;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.xpath.AbstractXPathValidationRule;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;

/**
 * Validate that exactly one node is returned by the XPath query.
 */
public class ValidateExactlyOne extends AbstractXPathValidationRule {

  private final String xpath;
  private final ValidationRule rule;

  public ValidateExactlyOne(String xpath, String message, String code) {
    this(xpath, message, code, Severity.UNSET);
  }

  public ValidateExactlyOne(
    String xpath,
    String message,
    String code,
    Severity severity
  ) {
    this.xpath = xpath;
    this.rule = new ValidationRule(code, message, severity);
  }

  @Override
  public List<ValidationIssue> validate(
    XPathRuleValidationContext validationContext
  ) {
    try {
      XPathSelector selector = validationContext
        .getNetexXMLParser()
        .getXPathCompiler()
        .compile(xpath)
        .load();
      selector.setContextItem(validationContext.getXmlNode());
      XdmValue nodes = selector.evaluate();
      if (nodes.size() != 1) {
        DataLocation dataLocation = new DataLocation(
          null,
          validationContext.getFileName(),
          null,
          null
        );
        return List.of(new ValidationIssue(rule, dataLocation));
      }
      return Collections.emptyList();
    } catch (SaxonApiException e) {
      throw new NetexValidationException(
        "Error while validating rule " + xpath,
        e
      );
    }
  }

  @Override
  public ValidationRule rule() {
    return rule;
  }
}

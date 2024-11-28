package org.entur.netex.validation.validator.xpath.rules;

import java.util.ArrayList;
import java.util.List;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.xpath.AbstractXPathValidationRule;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;

/**
 * Validate that the XPath query does not return any node.
 */
public class ValidateNotExist extends AbstractXPathValidationRule {

  private final String xpath;
  private final ValidationRule rule;

  public ValidateNotExist(String xpath, String message, String code) {
    this(xpath, message, code, Severity.UNSET);
  }

  public ValidateNotExist(
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
      List<ValidationIssue> validationIssues = new ArrayList<>();
      for (XdmItem item : nodes) {
        XdmNode xdmNode = (XdmNode) item;
        DataLocation dataLocation = getXdmNodeLocation(
          validationContext.getFileName(),
          xdmNode
        );
        validationIssues.add(new ValidationIssue(rule, dataLocation));
      }
      return validationIssues;
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

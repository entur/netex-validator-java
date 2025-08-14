package org.entur.netex.validation.validator.xpath;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.XPathValidator;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run XPath validation rules against the dataset.
 */
public class XPathRuleValidator implements XPathValidator {

  private static final Logger LOGGER = LoggerFactory.getLogger(XPathRuleValidator.class);

  private final ValidationTree topLevelValidationTree;

  public XPathRuleValidator(ValidationTreeFactory validationTreeFactory) {
    this.topLevelValidationTree = validationTreeFactory.builder().build();
  }

  @Override
  public List<ValidationIssue> validate(XPathValidationContext xPathValidationContext) {
    LOGGER.debug(
      "Validating file {} in report {}",
      xPathValidationContext.getFileName(),
      xPathValidationContext.getValidationReportId()
    );
    return validate(
      xPathValidationContext.getCodespace(),
      xPathValidationContext.getFileName(),
      xPathValidationContext.getXmlNode(),
      xPathValidationContext.getNetexXMLParser()
    );
  }

  protected List<ValidationIssue> validate(
    String codespace,
    String fileName,
    XdmNode document,
    NetexXMLParser netexXMLParser
  ) {
    XPathRuleValidationContext validationContext = new XPathRuleValidationContext(
      document,
      netexXMLParser,
      codespace,
      fileName
    );
    return this.validate(validationContext);
  }

  public List<ValidationIssue> validate(XPathRuleValidationContext validationContext) {
    return topLevelValidationTree.validate(validationContext);
  }

  public String describe() {
    return topLevelValidationTree.describe();
  }

  @Override
  public Set<ValidationRule> getRules() {
    return topLevelValidationTree
      .getRules()
      .stream()
      .map(XPathValidationRule::rule)
      .collect(Collectors.toUnmodifiableSet());
  }
}

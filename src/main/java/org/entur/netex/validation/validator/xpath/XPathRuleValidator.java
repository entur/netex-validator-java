package org.entur.netex.validation.validator.xpath;

import java.util.List;
import java.util.Set;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.AbstractXPathNetexValidator;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntryFactory;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run XPath validation rules against the dataset.
 */
public class XPathRuleValidator extends AbstractXPathNetexValidator {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    XPathRuleValidator.class
  );

  private final ValidationTree topLevelValidationTree;

  public XPathRuleValidator(
    ValidationTreeFactory validationTreeFactory,
    ValidationReportEntryFactory validationReportEntryFactory
  ) {
    super(validationReportEntryFactory);
    this.topLevelValidationTree = validationTreeFactory.buildValidationTree();
  }

  @Override
  public void validate(
    ValidationReport validationReport,
    XPathValidationContext xPathValidationContext
  ) {
    LOGGER.debug(
      "Validating file {} in report {}",
      xPathValidationContext.getFileName(),
      validationReport.getValidationReportId()
    );
    List<ValidationReportEntry> validationReportEntries = validate(
      validationReport.getCodespace(),
      xPathValidationContext.getFileName(),
      xPathValidationContext.getXmlNode(),
      xPathValidationContext.getNetexXMLParser()
    );
    validationReport.addAllValidationReportEntries(validationReportEntries);
  }

  protected List<ValidationReportEntry> validate(
    String codespace,
    String fileName,
    XdmNode document,
    NetexXMLParser netexXMLParser
  ) {
    XPathRuleValidationContext validationContext =
      new XPathRuleValidationContext(
        document,
        netexXMLParser,
        codespace,
        fileName
      );
    return this.validate(validationContext);
  }

  public List<ValidationReportEntry> validate(
    XPathRuleValidationContext validationContext
  ) {
    List<XPathValidationReportEntry> xPathValidationReportEntries =
      topLevelValidationTree.validate(validationContext);
    return xPathValidationReportEntries
      .stream()
      .map(this::createValidationReportEntry)
      .toList();
  }

  private ValidationReportEntry createValidationReportEntry(
    XPathValidationReportEntry xPathValidationReportEntry
  ) {
    return createValidationReportEntry(
      xPathValidationReportEntry.code(),
      xPathValidationReportEntry.dataLocation(),
      xPathValidationReportEntry.message()
    );
  }

  public String describe() {
    return topLevelValidationTree.describe();
  }

  @Override
  public Set<String> getRuleDescriptions() {
    return topLevelValidationTree.getRuleMessages();
  }
}

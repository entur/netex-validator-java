package org.entur.netex.validation.validator.xpath;

import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.AbstractNetexValidator;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntryFactory;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Run XPath validation rules against the dataset.
 */
public class XPathValidator extends AbstractNetexValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(XPathValidator.class);

    private final ValidationTree topLevelValidationTree;

    public XPathValidator(ValidationTreeFactory validationTreeFactory, ValidationReportEntryFactory validationReportEntryFactory) {
        super(validationReportEntryFactory);
        this.topLevelValidationTree = validationTreeFactory.buildValidationTree();
    }

    @Override
    public void validate(ValidationReport validationReport, ValidationContext validationContext) {
        LOGGER.debug("Validating file {} in report {}", validationContext.getFileName(), validationReport.getValidationReportId());
        List<ValidationReportEntry> validationReportEntries = validate(validationReport.getCodespace(), validationContext.getFileName(), validationContext.getXmlNode(), validationContext.getNetexXMLParser());
        validationReport.addAllValidationReportEntries(validationReportEntries);
    }

    protected List<ValidationReportEntry> validate(String codespace, String fileName, XdmNode document, NetexXMLParser netexXMLParser) {
        XPathValidationContext validationContext = new XPathValidationContext(document, netexXMLParser, codespace, fileName);
        return this.validate(validationContext);

    }

    public List<ValidationReportEntry> validate(XPathValidationContext validationContext) {
        List<XPathValidationReportEntry> xPathValidationReportEntries = topLevelValidationTree.validate(validationContext);
        return xPathValidationReportEntries.stream().map(this::createValidationReportEntry).collect(Collectors.toList());
    }

    private ValidationReportEntry createValidationReportEntry(XPathValidationReportEntry xPathValidationReportEntry) {
        return createValidationReportEntry(xPathValidationReportEntry.getCode(), xPathValidationReportEntry.getMessage(), xPathValidationReportEntry.getFileName());
    }

    public String describe() {
        return topLevelValidationTree.describe();
    }

    public Set<String> getRuleMessages() {
        return topLevelValidationTree.getRuleMessages();
    }


}

package org.entur.netex.validation.validator.xpath;

import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.NetexValidator;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Run XPath validation rules against the dataset.
 */
public class XPathValidator implements NetexValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(XPathValidator.class);

    private final ValidationTree topLevelValidationTree;

    public XPathValidator(ValidationTreeFactory validationTreeFactory) {
        this.topLevelValidationTree = validationTreeFactory.buildValidationTree();
    }

    @Override
    public void validate(ValidationReport validationReport, ValidationContext validationContext) {
        LOGGER.debug("Validating file {} in report {}", validationContext.getFileName(), validationReport.getValidationReportId());
        List<ValidationReportEntry> validationReportEntries = validate(validationReport.getCodespace(), validationContext.getFileName(), validationContext.getXmlNode(), validationContext.getxPathCompiler());
        validationReport.addAllValidationReportEntries(validationReportEntries);
    }

    protected List<ValidationReportEntry> validate(String codespace, String fileName, XdmNode document, XPathCompiler xPathCompiler) {
        XPathValidationContext validationContext = new XPathValidationContext(document, xPathCompiler, codespace, fileName);
        return this.validate(validationContext);

    }

    public List<ValidationReportEntry> validate(XPathValidationContext validationContext) {
        return topLevelValidationTree.validate(validationContext);
    }

    public String describe() {
        return topLevelValidationTree.describe();
    }

    public Set<String> getRuleMessages() {
        return topLevelValidationTree.getRuleMessages();
    }


}

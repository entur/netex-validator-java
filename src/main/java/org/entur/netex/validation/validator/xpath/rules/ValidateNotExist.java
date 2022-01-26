package org.entur.netex.validation.validator.xpath.rules;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntrySeverity;
import org.entur.netex.validation.validator.xpath.AbstractXPathValidationRule;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Validate that the XPath query does not return any node.
 */
public class ValidateNotExist extends AbstractXPathValidationRule {

    private final String xpath;
    private final String message;
    private final String name;
    private final ValidationReportEntrySeverity severity;

    public ValidateNotExist(String xpath, String message, String name, ValidationReportEntrySeverity validationReportEntrySeverity) {
        this.xpath = xpath;
        this.message = message;
        this.name = name;
        this.severity = validationReportEntrySeverity;
    }

    @Override
    public List<ValidationReportEntry> validate(XPathValidationContext validationContext) {
        try {
            XPathSelector selector = validationContext.getNetexXMLParser().getXPathCompiler().compile(xpath).load();
            selector.setContextItem(validationContext.getXmlNode());
            XdmValue nodes = selector.evaluate();
            List<ValidationReportEntry> validationReportEntries = new ArrayList<>();
            for (XdmItem item : nodes) {
                XdmNode xdmNode = (XdmNode) item;
                String validationReportEntryMessage = getXdmNodeLocation(xdmNode) + message;
                validationReportEntries.add(new ValidationReportEntry(validationReportEntryMessage, name, severity, validationContext.getFileName()));
            }
            return validationReportEntries;
        } catch (SaxonApiException e) {
            throw new NetexValidationException("Error while validating rule " + xpath, e);
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ValidationReportEntrySeverity getSeverity() {
        return severity;
    }


}

package org.entur.netex.validation.validator.xpath.rules;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.xpath.AbstractXPathValidationRule;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.entur.netex.validation.validator.xpath.XPathValidationReportEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Validate that the XPath query does not return any node.
 */
public class ValidateNotExist extends AbstractXPathValidationRule {

    private final String xpath;
    private final String message;
    private final String code;

    public ValidateNotExist(String xpath, String message, String code) {
        this.xpath = xpath;
        this.message = message;
        this.code = code;
    }

    @Override
    public List<XPathValidationReportEntry> validate(XPathValidationContext validationContext) {
        try {
            XPathSelector selector = validationContext.getNetexXMLParser().getXPathCompiler().compile(xpath).load();
            selector.setContextItem(validationContext.getXmlNode());
            XdmValue nodes = selector.evaluate();
            List<XPathValidationReportEntry> validationReportEntries = new ArrayList<>();
            for (XdmItem item : nodes) {
                XdmNode xdmNode = (XdmNode) item;
                DataLocation dataLocation = getXdmNodeLocation(validationContext.getFileName(), xdmNode);
                validationReportEntries.add(new XPathValidationReportEntry(message, code, dataLocation));
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
    public String getCode() {
        return code;
    }

}

package org.entur.netex.validation.validator.xpath.rules;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmValue;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.xpath.AbstractXPathValidationRule;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.entur.netex.validation.validator.xpath.XPathValidationReportEntry;

import java.util.Collections;
import java.util.List;

/**
 * Validate that exactly one node is returned by the XPath query.
 */
public class ValidateExactlyOne extends AbstractXPathValidationRule {

    private final String code;
    private final String xpath;
    private final String message;

    public ValidateExactlyOne(String xpath, String message, String code) {
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
            if (nodes.size() != 1) {
                DataLocation dataLocation = new DataLocation(null, validationContext.getFileName(), null, null);
                return List.of(new XPathValidationReportEntry(message, code, dataLocation));
            }
            return Collections.emptyList();
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

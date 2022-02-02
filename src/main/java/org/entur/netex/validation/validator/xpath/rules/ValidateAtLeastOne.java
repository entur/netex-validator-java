package org.entur.netex.validation.validator.xpath.rules;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmValue;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.xpath.ValidationRule;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.entur.netex.validation.validator.xpath.XPathValidationReportEntry;

import java.util.Collections;
import java.util.List;

/**
 * Validate that at least one node is returned by the XPath query.
 */
public class ValidateAtLeastOne implements ValidationRule {

    private final String xpath;
    private final String message;
    private final String code;

    public ValidateAtLeastOne(String xpath, String message, String code) {
        this.xpath = xpath;
        this.message = message;
        this.code = code;
    }

    @Override
    public List<XPathValidationReportEntry> validate(XPathValidationContext validationContext)  {
        try {
            XPathSelector selector = validationContext.getNetexXMLParser().getXPathCompiler().compile(xpath).load();
            selector.setContextItem(validationContext.getXmlNode());
            XdmValue nodes = selector.evaluate();
            if (nodes.isEmpty()) {
                return List.of(new XPathValidationReportEntry(message, code, validationContext.getFileName()));
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

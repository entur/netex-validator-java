package org.entur.netex.validation.validator.xpath;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

/**
 * Base class for XPath validation rules.
 */
public abstract class AbstractXPathValidationRule implements ValidationRule {

    /**
     * Return a string representation of the location of the node in the XML document.
     * @param xdmNode the XML node.
     * @return a string representation of the location of the node in the XML document.
     */
    protected String getXdmNodeLocation(XdmNode xdmNode) {
        int lineNumber = xdmNode.getLineNumber();
        int columnNumber = xdmNode.getColumnNumber();
        String netexId = xdmNode.getAttributeValue(new QName("id"));
        if(netexId == null) {
            netexId = "(N/A)";
        }
        return "[Line " + lineNumber + ", Column " + columnNumber + ", Id " + netexId + "] ";
    }
}

package org.entur.netex.validation.validator.xpath;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.DataLocation;

/**
 * Base class for XPath validation rules.
 */
public abstract class AbstractXPathValidationRule implements ValidationRule {

    /**
     * Return a string representation of the location of the node in the XML document.
     *
     * @param xdmNode the XML node.
     * @return a string representation of the location of the node in the XML document.
     */
    protected DataLocation getXdmNodeLocation(String fileName, XdmNode xdmNode) {
        String netexId = xdmNode.getAttributeValue(new QName("id"));
        if (netexId == null) {
            netexId = "(N/A)";
        }
        return new DataLocation(netexId, fileName, xdmNode.getLineNumber(), xdmNode.getColumnNumber());
    }
}

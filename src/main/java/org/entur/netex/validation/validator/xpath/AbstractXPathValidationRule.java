package org.entur.netex.validation.validator.xpath;

import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmSequenceIterator;
import org.entur.netex.validation.validator.DataLocation;

/**
 * Base class for XPath validation rules.
 */
public abstract class AbstractXPathValidationRule
  implements XPathValidationRule {

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
    return new DataLocation(
      netexId,
      fileName,
      xdmNode.getLineNumber(),
      xdmNode.getColumnNumber()
    );
  }

  protected static XdmNode getChild(XdmNode parent, QName childName) {
    XdmSequenceIterator<XdmNode> iter = parent.axisIterator(
      Axis.CHILD,
      childName
    );
    if (iter.hasNext()) {
      return iter.next();
    } else {
      return null;
    }
  }
}

package org.entur.netex.validation.validator.xpath.support;

import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.entur.netex.validation.xml.NetexXMLParser;

/**
 * Utility methods for testing XPath validators.
 */
public class XPathTestSupport {

  public static final String TEST_CODESPACE = "ENT";
  public static final NetexXMLParser NETEX_XML_PARSER = new NetexXMLParser();
  public static final String TEST_FILENAME = "netex.xml";

  /**
   * Parse a NeTEx fragment and return an XML document.
   * The XML fragment must be namespaced with the NeTEx schema namespace http://www.netex.org.uk/netex
   */
  public static XdmNode parseDocument(String netexFragment) {
    XdmNode xdmItems = NETEX_XML_PARSER.parseStringToXdmNode(netexFragment);
    XdmNode rootElement = xdmItems.children().iterator().next();
    String namespaceURI = rootElement.getNodeName().getNamespaceURI();
    if (!NetexXMLParser.NETEX_NAMESPACE.equals(namespaceURI)) {
      throw new IllegalStateException(
        "The XML fragment is not namespaced under the NeTEx namespace. Provided namespace: '" +
        namespaceURI +
        "'"
      );
    }
    return rootElement;
  }

  /**
   * Build an XPathRuleValidationContext out of a netex fragment.
   * The XML fragment must be namespaced with the NeTEx schema namespace http://www.netex.org.uk/netex
   */
  public static XPathRuleValidationContext validationContext(
    String netexFragment
  ) {
    return validationContext(parseDocument(netexFragment), TEST_FILENAME);
  }

  /**
   * Build an XPathRuleValidationContext out of an XML document.
   * The XML document must be namespaced with the NeTEx schema namespace http://www.netex.org.uk/netex
   */
  public static XPathRuleValidationContext validationContext(XdmNode document) {
    return validationContext(document, TEST_FILENAME);
  }

  /**
   * Build an XPathRuleValidationContext out of an XML document and a filename.
   * The XML document must be namespaced with the NeTEx schema namespace http://www.netex.org.uk/netex
   */
  public static XPathRuleValidationContext validationContext(
    XdmNode document,
    String fileName
  ) {
    return new XPathRuleValidationContext(
      document,
      NETEX_XML_PARSER,
      TEST_CODESPACE,
      fileName
    );
  }
}

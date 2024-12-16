package org.entur.netex.validation.test.xpath.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.xml.NetexXMLParser;

/**
 * Utility methods for testing XPath validators.
 */
public class XPathTestSupport {

  public static final String TEST_CODESPACE = "ENT";
  public static final String TEST_FILENAME = "netex.xml";

  static final NetexXMLParser NETEX_XML_PARSER = new NetexXMLParser();

  /**
   * Parse a NeTEx fragment and return an XML document.
   * The XML fragment must be namespaced with the NeTEx schema namespace http://www.netex.org.uk/netex
   */
  public static XdmNode parseDocument(String netexFragment) {
    return parseDocument(new ByteArrayInputStream(netexFragment.getBytes()));
  }

  /**
   * Parse a NeTEx fragment and return an XML document.
   * The XML fragment must be namespaced with the NeTEx schema namespace http://www.netex.org.uk/netex
   */
  public static XdmNode parseDocument(InputStream inputStream) {
    XdmNode parsedDocument;
    try {
      parsedDocument =
        NETEX_XML_PARSER.parseByteArrayToXdmNode(inputStream.readAllBytes());
    } catch (IOException e) {
      throw new NetexValidationException(e);
    }
    XdmNode rootElement = parsedDocument.children().iterator().next();
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
}

package org.entur.netex.validation.validator.xpath;

import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.xml.NetexXMLParser;

/**
 * The validation context for an XPath validation rule.
 */
public class XPathValidationContext {

    private final XdmNode xmlNode;
    private final NetexXMLParser netexXMLParser;
    private final String codespace;
    private final String fileName;

    /**
     *
     * @param document the NeTEx document or NeTEx document part.
     * @param netexXMLParser the NeTEx parser.
     * @param codespace the current codespace.
     * @param fileName the current filename.
     */
    public XPathValidationContext(XdmNode document, NetexXMLParser netexXMLParser, String codespace, String fileName) {
        this.xmlNode = document;
        this.netexXMLParser = netexXMLParser;
        this.codespace = codespace;
        this.fileName = fileName;
    }

    public XdmNode getXmlNode() {
        return xmlNode;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCodespace() {
        return codespace;
    }

    public NetexXMLParser getNetexXMLParser() {
        return netexXMLParser;
    }
}

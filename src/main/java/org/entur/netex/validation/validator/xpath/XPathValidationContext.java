package org.entur.netex.validation.validator.xpath;

import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmNode;

/**
 * The validation context for an XPath validation rule.
 */
public class XPathValidationContext {

    private final XdmNode xmlNode;
    private final XPathCompiler xPathCompiler;

    private final String codespace;
    private final String fileName;

    /**
     *
     * @param document the NeTEx document or NeTEx document part.
     * @param xPathCompiler the XPath compiler.
     * @param codespace the current codespace.
     * @param fileName the current filename.
     */
    public XPathValidationContext(XdmNode document, XPathCompiler xPathCompiler, String codespace, String fileName) {
        this.xmlNode = document;
        this.xPathCompiler = xPathCompiler;
        this.codespace = codespace;
        this.fileName = fileName;
    }

    public XdmNode getXmlNode() {
        return xmlNode;
    }

    public XPathCompiler getXPathCompiler() {
        return xPathCompiler;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCodespace() {
        return codespace;
    }
}

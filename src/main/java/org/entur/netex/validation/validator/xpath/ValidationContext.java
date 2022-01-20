package org.entur.netex.validation.validator.xpath;

import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.id.IdVersion;

import java.util.List;
import java.util.Set;

/**
 * Context of the current NeTEx validation.
 */
public class ValidationContext {

    private final XdmNode xmlNode;
    private final XPathCompiler xPathCompiler;

    private final String codespace;
    private final String fileName;
    private final Set<IdVersion> localIds;
    private final List<IdVersion> localRefs;

    /**
     * Build the validation context for a validation run
     * @param document the document being analyzed.
     * @param xPathCompiler the XPath compiler.
     * @param codespace the current codespace.
     * @param fileName the name of the file being analyzed
     * @param localIds the set of NeTEx ids declared in the file.
     * @param localRefs the set of references to other NeTEx objects. They may refer to object in the same file or to external sources.
     */
    public ValidationContext(XdmNode document, XPathCompiler xPathCompiler, String codespace, String fileName, Set<IdVersion> localIds, List<IdVersion> localRefs) {
        this.xmlNode = document;
        this.xPathCompiler = xPathCompiler;
        this.codespace = codespace;
        this.fileName = fileName;
        this.localIds = localIds;
        this.localRefs = localRefs;
    }

    public XdmNode getXmlNode() {
        return xmlNode;
    }

    public XPathCompiler getxPathCompiler() {
        return xPathCompiler;
    }

    public String getFileName() {
        return fileName;
    }


    public String getCodespace() {
        return codespace;
    }

    public Set<IdVersion> getLocalIds() {
        return localIds;
    }

    public List<IdVersion> getLocalRefs() {
        return localRefs;
    }

    public boolean isCommonFile() {
        return fileName.startsWith("_");
    }
}

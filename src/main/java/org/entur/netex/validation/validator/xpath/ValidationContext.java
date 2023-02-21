package org.entur.netex.validation.validator.xpath;

import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.id.IdVersion;
import org.entur.netex.validation.xml.NetexXMLParser;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Context of the current NeTEx validation.
 */
public class ValidationContext {

    private final XdmNode xmlNode;
    private final NetexXMLParser netexXMLParser;
    private final String codespace;
    private final String fileName;
    private final Set<IdVersion> localIds;
    private final List<IdVersion> localRefs;

    /**
     * Build the validation context for a validation run
     * @param document the document being analyzed.
     * @param netexXMLParser the NeTEx parser.
     * @param codespace the current codespace.
     * @param fileName the name of the file being analyzed
     * @param localIds the set of NeTEx ids declared in the file.
     * @param localRefs the set of references to other NeTEx objects. They may refer to object in the same file or to external sources.
     */
    public ValidationContext(XdmNode document, NetexXMLParser netexXMLParser, String codespace, String fileName, Set<IdVersion> localIds, List<IdVersion> localRefs) {
        this.xmlNode = document;
        this.netexXMLParser = netexXMLParser;
        this.codespace = Objects.requireNonNull(codespace);
        this.fileName = fileName;
        this.localIds = Objects.requireNonNull(localIds);
        this.localRefs = Objects.requireNonNull(localRefs);
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

    public Set<IdVersion> getLocalIds() {
        return localIds;
    }

    public List<IdVersion> getLocalRefs() {
        return localRefs;
    }

    public boolean isCommonFile() {
        return fileName.startsWith("_");
    }

    public NetexXMLParser getNetexXMLParser() {
        return netexXMLParser;
    }

}

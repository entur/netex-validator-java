package org.entur.netex.validation.validator.id;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.entur.netex.validation.exception.NetexValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Parse a NeTEx document and extract NeTEx ids and references.
 */
public final class NetexIdExtractorHelper {

    private NetexIdExtractorHelper() {
    }

    /**
     * Collect NeTEx ids declared in a NeTEx document.
     * @param document the parsed NeTEx document.
     * @param xPathCompiler the XPath compiler.
     * @param filename the NeTEx file name.
     * @param ignorableElementNames set of NeTEx elements that are not collected.
     * @return a list of IDVersion representing the NeTEx ids present in the document.
     */
    public static List<IdVersion> collectEntityIdentifiers(XdmNode document, XPathCompiler xPathCompiler, String filename, Set<String> ignorableElementNames) {
        return collectIdOrRefWithVersion(document, xPathCompiler, filename, "id", ignorableElementNames);
    }

    /**
     * Collect NeTEx references declared in a NeTEx document.
     * @param document the parsed NeTEx document.
     * @param xPathCompiler the XPath compiler.
     * @param filename the NeTEx file name.
     * @param ignorableElementNames set of NeTEx elements that are not collected.
     * @return a list of IDVersion representing the NeTEx references present in the document.
     */
    public static List<IdVersion> collectEntityReferences(XdmNode document, XPathCompiler xPathCompiler, String filename, Set<String> ignorableElementNames) {
        return collectIdOrRefWithVersion(document, xPathCompiler, filename, "ref", ignorableElementNames);
    }

    private static List<IdVersion> collectIdOrRefWithVersion(XdmNode document, XPathCompiler xPathCompiler, String filename, String attributeName, Set<String> ignorableElementNames) {
        StringBuilder filterClause = new StringBuilder();
        filterClause.append("//n:*[");
        if (ignorableElementNames != null) {
            for (String elementName : ignorableElementNames) {
                filterClause.append("not(local-name(.)='").append(elementName).append("') and ");
            }
        }
        filterClause.append("@").append(attributeName).append("]");

        XdmValue nodes;
        try {
            XPathSelector selector = xPathCompiler.compile(filterClause.toString()).load();
            selector.setContextItem(document);
            nodes = selector.evaluate();
        } catch (SaxonApiException e) {
            throw new NetexValidationException("Exception while parsing the NeTEx file " + filename, e);
        }

        QName versionQName = new QName("version");
        List<IdVersion> ids = new ArrayList<>();
        for (XdmItem item : nodes) {
            XdmNode n = (XdmNode) item;
            String elementName = n.getNodeName().getLocalName();

            List<String> parentElementNames = new ArrayList<>();
            XdmNode p = n.getParent();
            while (p != null && p.getNodeName() != null) {
                parentElementNames.add(p.getNodeName().getLocalName());
                p = p.getParent();
            }
            String id = n.getAttributeValue(new QName(attributeName));
            String version = n.getAttributeValue(versionQName);

            ids.add(new IdVersion(id, version, elementName, parentElementNames, filename,
                    n.getLineNumber(), n.getColumnNumber()));

        }
        return ids;
    }


}

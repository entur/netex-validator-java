package org.entur.netex.validation.xml;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.entur.netex.validation.Constants;
import org.entur.netex.validation.exception.NetexValidationException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stax.StAXSource;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Set;

/**
 * NeTEx parsing utility class.
 */
public final class XMLParserUtil {

    private static final String NETEX_NAMESPACE = "http://www.netex.org.uk/netex";
    private static final String SIRI_NAMESPACE = "http://www.siri.org.uk/siri";
    private static final String OPENGIS_NAMESPACE = "http://www.opengis.net/gml/3.2";

    private static XPathCompiler xpathCompiler;
    private static final Processor processor = new Processor(false);

    private XMLParserUtil() {
    }

    /**
     * Return a secure XMLInput factory.
     * Security-sensitive features are disabled.
     * @return a secure XMLInput factory
     */
    public static XMLInputFactory getSecureXmlInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        return factory;
    }


    /**
     * Return a shared, thread-safe, instance of XPathCompiler.
     * @return a shared XPathCompiler.
     */
    public static synchronized XPathCompiler getXPathCompiler() {

        if (xpathCompiler == null) {
            xpathCompiler = processor.newXPathCompiler();
            xpathCompiler.setCaching(true);
            xpathCompiler.declareNamespace("", NETEX_NAMESPACE);
            xpathCompiler.declareNamespace("n", NETEX_NAMESPACE);
            xpathCompiler.declareNamespace("s", SIRI_NAMESPACE);
            xpathCompiler.declareNamespace("g", OPENGIS_NAMESPACE);
        }

        return xpathCompiler;
    }

    /**
     * Parse an XML file and return an XML nodes graph.
     * @param content the XML file.
     * @return an XML nodes graph representing the XML document.
     */
    public static XdmNode parseFileToXdmNode(byte[] content) {
        DocumentBuilder builder = processor.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        // ignore SiteFrame
        Set<QName> elementsToSkip = Set.of(new QName(Constants.NETEX_NAMESPACE, "SiteFrame"));
        try {
            return builder.build(new StAXSource(SkippingXMLStreamReaderFactory.newXMLStreamReader(new BufferedInputStream(new ByteArrayInputStream(content)), elementsToSkip)));
        } catch (SaxonApiException | XMLStreamException e) {
            throw new NetexValidationException("Exception while parsing the NeTex document", e);
        }
    }

    /**
     * Select a set of nodes according to an XPath expression.
     * @param expression the XPath expression to evaluate.
     * @param xPathCompiler the XPath compiler.
     * @param document the XML document on which the XPath is evaluated.
     * @return the nodes that match the XPath expression.
     */
    public static XdmValue selectNodeSet(String expression, XPathCompiler xPathCompiler, XdmNode document) {
        try {
            XPathSelector selector = xPathCompiler.compile(expression).load();
            selector.setContextItem(document);
            return selector.evaluate();
        } catch (SaxonApiException e) {
            throw new NetexValidationException("Exception while selecting node with xPath " + expression, e);
        }
    }
}

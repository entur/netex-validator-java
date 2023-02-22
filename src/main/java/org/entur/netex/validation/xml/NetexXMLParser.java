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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * NeTEx parsing utility class.
 */
public class NetexXMLParser {

    private static final String NETEX_NAMESPACE = "http://www.netex.org.uk/netex";
    private static final String SIRI_NAMESPACE = "http://www.siri.org.uk/siri";
    private static final String OPENGIS_NAMESPACE = "http://www.opengis.net/gml/3.2";

    private final XPathCompiler xpathCompiler;
    private final Processor processor;
    private final Set<QName> ignorableNeTexElements;

    public NetexXMLParser() {
        this(Collections.emptySet());
    }

    public NetexXMLParser(Set<String> ignorableNetexElements) {
        this.processor = new Processor(false);
        this.xpathCompiler = buildXPathCompiler();
        this.ignorableNeTexElements = ignorableNetexElements.stream().map(elementName -> new QName(Constants.NETEX_NAMESPACE, elementName)).collect(Collectors.toSet());
    }

    /**
     * Return a secure XMLInput factory.
     * Security-sensitive features are disabled.
     *
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
     *
     * @return a shared XPathCompiler.
     */
    private XPathCompiler buildXPathCompiler() {

        XPathCompiler compiler = processor.newXPathCompiler();
        compiler.setCaching(true);
        compiler.declareNamespace("", NETEX_NAMESPACE);
        compiler.declareNamespace("n", NETEX_NAMESPACE);
        compiler.declareNamespace("s", SIRI_NAMESPACE);
        compiler.declareNamespace("g", OPENGIS_NAMESPACE);
        return compiler;
    }

    /**
     * Parse a string containing an XML document and return an XML nodes graph.
     */
    public XdmNode parseStringToXdmNode(String content) {
        return parseByteArrayToXdmNode(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Parse a byte array containing an XML document and return an XML nodes graph.
     */
    public XdmNode parseByteArrayToXdmNode(byte[] content) {
        return parseInputStreamToXdmNode((new ByteArrayInputStream(content)));
    }


    /**
     * Parse an input stream containing an XML document and return an XML nodes graph.
     */
    public XdmNode parseInputStreamToXdmNode(InputStream inputStream) {
        DocumentBuilder builder = processor.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        try {
            return builder.build(new StAXSource(SkippingXMLStreamReaderFactory.newXMLStreamReader(new BufferedInputStream(inputStream), ignorableNeTexElements)));
        } catch (SaxonApiException | XMLStreamException e) {
            throw new NetexValidationException("Exception while parsing the NeTex document", e);
        }
    }

    /**
     * Select a set of nodes according to an XPath expression.
     *
     * @param expression the XPath expression to evaluate.
     * @param document   the XML document on which the XPath is evaluated.
     * @return the nodes that match the XPath expression.
     */
    public XdmValue selectNodeSet(String expression, XdmNode document) {
        try {
            XPathSelector selector = xpathCompiler.compile(expression).load();
            selector.setContextItem(document);
            return selector.evaluate();
        } catch (SaxonApiException e) {
            throw new NetexValidationException("Exception while selecting node with xPath " + expression, e);
        }
    }

    public XPathCompiler getXPathCompiler() {
        return xpathCompiler;
    }
}

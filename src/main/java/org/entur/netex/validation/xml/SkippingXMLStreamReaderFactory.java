package org.entur.netex.validation.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.Set;

/**
 * Stream reader that skips parsing of the given XML elements in order to improve performance.
 */
public class SkippingXMLStreamReaderFactory {

    private SkippingXMLStreamReaderFactory() {
    }

    public static XMLStreamReader newXMLStreamReader(final InputStream is, final Set<QName> elementsToSkip) throws XMLStreamException {
        XMLInputFactory factory = XMLParserUtil.getSecureXmlInputFactory();
        XMLStreamReader xmlStreamReader = factory.createXMLStreamReader(is);
        if (elementsToSkip.isEmpty()) {
            return xmlStreamReader;
        } else {
            return factory.createFilteredReader(xmlStreamReader, new StreamFilter() {

                private boolean accept = true;

                private QName rootSkipStart;

                @Override
                public boolean accept(XMLStreamReader reader) {
                    if (reader.isStartElement() && accept && elementsToSkip.contains(reader.getName())) {
                        if (rootSkipStart == null) {
                            rootSkipStart = reader.getName();
                        }
                        accept = false;
                        return false;
                    } else if (reader.isEndElement() && !accept && elementsToSkip.contains(reader.getName())) {
                        if (rootSkipStart.equals(reader.getName())) {
                            rootSkipStart = null;
                            accept = true;
                            return false;
                        }
                    }
                    return accept;
                }
            });
        }
    }
}

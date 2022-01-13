package org.entur.netex.validation.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;


/**
 * Read the NeTEx version stored in the version attribute of the PublicationDelivery element.
 */
public final class PublicationDeliveryVersionAttributeReader {


    private static final Logger LOGGER = LoggerFactory.getLogger(PublicationDeliveryVersionAttributeReader.class);

    private PublicationDeliveryVersionAttributeReader() {
    }

    public static String findPublicationDeliveryVersion(byte[] content) {

        String versionAttribute = null;
        try {
            XMLInputFactory inputFactory = XMLParserUtil.getSecureXmlInputFactory();
            InputStream in = new ByteArrayInputStream(content);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(new BufferedInputStream(in));
            while (versionAttribute == null && eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    if ("PublicationDelivery".equals(startElement.getName().getLocalPart())) {

                        Iterator<Attribute> attributes = event.asStartElement().getAttributes();
                        while (attributes.hasNext()) {
                            Attribute attribute = attributes.next();
                            if ("version".equals(attribute.getName().toString())) {
                                versionAttribute = attribute.getValue();
                            }
                        }

                    }

                }
            }
            eventReader.close();
            in.close();
        } catch (XMLStreamException e) {
            LOGGER.error("Malformed xml", e);
        } catch (IOException e) {
            LOGGER.error("Error closing file", e);
        }

        return versionAttribute;
    }

}

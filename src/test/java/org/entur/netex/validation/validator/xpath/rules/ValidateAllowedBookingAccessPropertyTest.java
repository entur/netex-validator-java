package org.entur.netex.validation.validator.xpath.rules;

import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.entur.netex.validation.validator.xpath.XPathValidationReportEntry;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.BookingAccessEnumeration;

import java.util.List;
import java.util.Set;

class ValidateAllowedBookingAccessPropertyTest {

    public static final String TEST_CODESPACE = "FLB";
    private static final NetexXMLParser NETEX_XML_PARSER = new NetexXMLParser(Set.of("SiteFrame"));
    private static final String NETEX_FRAGMENT = "<FlexibleLine xmlns=\"http://www.netex.org.uk/netex\" version=\"46\" id=\"BRA:FlexibleLine:9204411c-bf86-4b6a-b8fa-5c40b8702213\">\n" +
            "                            <Name>HentMeg Kongsberg</Name>\n" +
            "                            <Description>Brakar HentMeg er en bestillingstjeneste der du kan bestille en bussreise fra holdeplass til holdeplass i Kongsberg.</Description>\n" +
            "                            <TransportMode>bus</TransportMode>\n" +
            "                            <TransportSubmode>\n" +
            "<BusSubmode>localBus</BusSubmode>\n" +
            "                            </TransportSubmode>\n" +
            "                            <PublicCode>HentMeg</PublicCode>\n" +
            "                            <OperatorRef ref=\"BRA:Operator:1\"/>\n" +
            "                            <RepresentedByGroupRef ref=\"BRA:Network:e7f2a84e-2a94-4899-b833-37d18cddb26f\"/>\n" +
            "                            <FlexibleLineType>fixedStopAreaWide</FlexibleLineType>\n" +
            "                            <BookingContact>\n" +
            "<Phone>32 20 30 90</Phone>\n" +
            "<Url>https://www.brakar.no/hent-meg/</Url>\n" +
            "                            </BookingContact>\n" +
            "                            <BookingMethods>callOffice online</BookingMethods>\n" +
            "                            <BookingAccess>${BOOKING_ACCESS}</BookingAccess>\n" +
            "                            <BookWhen>advanceAndDayOfTravel</BookWhen>\n" +
            "                            <BuyWhen>onReservation</BuyWhen>\n" +
            "                            <MinimumBookingPeriod>PT15M</MinimumBookingPeriod>\n" +
            "                            <BookingNote></BookingNote>\n" +
            "                        </FlexibleLine>";


    @Test
    void testInvalidBookingAccess() {
        ValidateAllowedBookingAccessProperty validateAllowedBookingAccessProperty = new ValidateAllowedBookingAccessProperty("FlexibleLine");
        String flexibleLineWithInvalidBookingAccess = NETEX_FRAGMENT.replace("${BOOKING_ACCESS}", BookingAccessEnumeration.OTHER.value());
        XdmNode document = NETEX_XML_PARSER.parseStringToXdmNode(flexibleLineWithInvalidBookingAccess);
        XPathValidationContext xpathValidationContext = new XPathValidationContext(document, NETEX_XML_PARSER, TEST_CODESPACE, null);
        List<XPathValidationReportEntry> xPathValidationReportEntries = validateAllowedBookingAccessProperty.validate(xpathValidationContext);
        Assertions.assertNotNull(xPathValidationReportEntries);
        Assertions.assertFalse(xPathValidationReportEntries.isEmpty());
    }

    @Test
    void testValidBookingAccess() {
        ValidateAllowedBookingAccessProperty validateAllowedBookingAccessProperty = new ValidateAllowedBookingAccessProperty("FlexibleLine");
        String flexibleLineWithValidBookingAccess = NETEX_FRAGMENT.replace("${BOOKING_ACCESS}", BookingAccessEnumeration.PUBLIC.value());
        XdmNode document = NETEX_XML_PARSER.parseStringToXdmNode(flexibleLineWithValidBookingAccess);
        XPathValidationContext xpathValidationContext = new XPathValidationContext(document, NETEX_XML_PARSER, TEST_CODESPACE, null);
        List<XPathValidationReportEntry> xPathValidationReportEntries = validateAllowedBookingAccessProperty.validate(xpathValidationContext);
        Assertions.assertNotNull(xPathValidationReportEntries);
        Assertions.assertTrue(xPathValidationReportEntries.isEmpty());
    }


}
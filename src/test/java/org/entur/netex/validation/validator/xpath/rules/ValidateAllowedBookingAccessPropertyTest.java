package org.entur.netex.validation.validator.xpath.rules;

import java.util.List;
import java.util.Set;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.BookingAccessEnumeration;

class ValidateAllowedBookingAccessPropertyTest {

  public static final String TEST_CODESPACE = "FLB";
  private static final NetexXMLParser NETEX_XML_PARSER = new NetexXMLParser(
    Set.of("SiteFrame")
  );
  private static final String NETEX_FRAGMENT =
    """
                  <FlexibleLine xmlns="http://www.netex.org.uk/netex" version="46" id="BRA:FlexibleLine:9204411c-bf86-4b6a-b8fa-5c40b8702213">
                                              <Name>HentMeg Kongsberg</Name>
                                              <Description>Brakar HentMeg er en bestillingstjeneste der du kan bestille en bussreise fra holdeplass til holdeplass i Kongsberg.</Description>
                                              <TransportMode>bus</TransportMode>
                                              <TransportSubmode>
                  <BusSubmode>localBus</BusSubmode>
                                              </TransportSubmode>
                                              <PublicCode>HentMeg</PublicCode>
                                              <OperatorRef ref="BRA:Operator:1"/>
                                              <RepresentedByGroupRef ref="BRA:Network:e7f2a84e-2a94-4899-b833-37d18cddb26f"/>
                                              <FlexibleLineType>fixedStopAreaWide</FlexibleLineType>
                                              <BookingContact>
                  <Phone>32 20 30 90</Phone>
                  <Url>https://www.brakar.no/hent-meg/</Url>
                                              </BookingContact>
                                              <BookingMethods>callOffice online</BookingMethods>
                                              <BookingAccess>${BOOKING_ACCESS}</BookingAccess>
                                              <BookWhen>advanceAndDayOfTravel</BookWhen>
                                              <BuyWhen>onReservation</BuyWhen>
                                              <MinimumBookingPeriod>PT15M</MinimumBookingPeriod>
                                              <BookingNote></BookingNote>
                                          </FlexibleLine>""";

  @Test
  void testInvalidBookingAccess() {
    ValidateAllowedBookingAccessProperty validateAllowedBookingAccessProperty =
      new ValidateAllowedBookingAccessProperty("FlexibleLine");
    String flexibleLineWithInvalidBookingAccess = NETEX_FRAGMENT.replace(
      "${BOOKING_ACCESS}",
      BookingAccessEnumeration.OTHER.value()
    );
    XdmNode document = NETEX_XML_PARSER.parseStringToXdmNode(
      flexibleLineWithInvalidBookingAccess
    );
    XPathRuleValidationContext xpathRuleValidationContext =
      new XPathRuleValidationContext(
        document,
        NETEX_XML_PARSER,
        TEST_CODESPACE,
        null
      );
    List<ValidationIssue> validationIssues =
      validateAllowedBookingAccessProperty.validate(xpathRuleValidationContext);
    Assertions.assertNotNull(validationIssues);
    Assertions.assertFalse(validationIssues.isEmpty());
  }

  @Test
  void testValidBookingAccess() {
    ValidateAllowedBookingAccessProperty validateAllowedBookingAccessProperty =
      new ValidateAllowedBookingAccessProperty("FlexibleLine");
    String flexibleLineWithValidBookingAccess = NETEX_FRAGMENT.replace(
      "${BOOKING_ACCESS}",
      BookingAccessEnumeration.PUBLIC.value()
    );
    XdmNode document = NETEX_XML_PARSER.parseStringToXdmNode(
      flexibleLineWithValidBookingAccess
    );
    XPathRuleValidationContext xpathRuleValidationContext =
      new XPathRuleValidationContext(
        document,
        NETEX_XML_PARSER,
        TEST_CODESPACE,
        null
      );
    List<ValidationIssue> validationIssues =
      validateAllowedBookingAccessProperty.validate(xpathRuleValidationContext);
    Assertions.assertNotNull(validationIssues);
    Assertions.assertTrue(validationIssues.isEmpty());
  }
}

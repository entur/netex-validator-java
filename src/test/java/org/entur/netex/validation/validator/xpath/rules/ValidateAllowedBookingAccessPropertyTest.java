package org.entur.netex.validation.validator.xpath.rules;

import java.util.List;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.BookingAccessEnumeration;

class ValidateAllowedBookingAccessPropertyTest {

  private static final String NETEX_FRAGMENT =
    """
    <ServiceFrame xmlns="http://www.netex.org.uk/netex">
      <lines>
        <FlexibleLine  version="46" id="BRA:FlexibleLine:9204411c-bf86-4b6a-b8fa-5c40b8702213">
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
        </FlexibleLine>
     </lines>
    </ServiceFrame>
    
    """;

  @Test
  void testInvalidBookingAccess() {
    ValidateAllowedBookingAccessProperty validateAllowedBookingAccessProperty =
      new ValidateAllowedBookingAccessProperty("lines/FlexibleLine");
    String flexibleLineWithInvalidBookingAccess = NETEX_FRAGMENT.replace(
      "${BOOKING_ACCESS}",
      BookingAccessEnumeration.OTHER.value()
    );

    XPathRuleValidationContext xpathRuleValidationContext =
      TestValidationContextBuilder
        .ofNetexFragment(flexibleLineWithInvalidBookingAccess)
        .build();
    List<ValidationIssue> validationIssues =
      validateAllowedBookingAccessProperty.validate(xpathRuleValidationContext);
    Assertions.assertNotNull(validationIssues);
    Assertions.assertFalse(validationIssues.isEmpty());
  }

  @Test
  void testValidBookingAccess() {
    ValidateAllowedBookingAccessProperty validateAllowedBookingAccessProperty =
      new ValidateAllowedBookingAccessProperty("lines/FlexibleLine");
    String flexibleLineWithValidBookingAccess = NETEX_FRAGMENT.replace(
      "${BOOKING_ACCESS}",
      BookingAccessEnumeration.PUBLIC.value()
    );
    XPathRuleValidationContext xpathRuleValidationContext =
      TestValidationContextBuilder
        .ofNetexFragment(flexibleLineWithValidBookingAccess)
        .build();

    List<ValidationIssue> validationIssues =
      validateAllowedBookingAccessProperty.validate(xpathRuleValidationContext);
    Assertions.assertNotNull(validationIssues);
    Assertions.assertTrue(validationIssues.isEmpty());
  }
}

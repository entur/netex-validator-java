package org.entur.netex.validation.validator.xpath.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.Test;

class ValidateAllowedTransportModeAndSubmodeOnServiceJourneyTest {

  private static final String NETEX_FRAGMENT =
    """
    <TimetableFrame xmlns="http://www.netex.org.uk/netex">
            <vehicleJourneys  >
               <ServiceJourney version="1" id="AVI:ServiceJourney:DX592-01-1078498752">
                  ${TRANSPORT_MODE}
                  ${TRANSPORT_SUBMODE}
               </ServiceJourney>
            </vehicleJourneys>
    </TimetableFrame>
    """;

  @Test
  void testValidTransportModeOnServiceJourney() {
    XPathRuleValidationContext xpathRuleValidationContext =
      createXPathValidationContext(
        "<TransportMode>bus</TransportMode>",
        "<TransportSubmode><BusSubmode>localBus</BusSubmode></TransportSubmode>"
      );

    ValidateAllowedTransportMode validateAllowedTransportMode =
      new ValidateAllowedTransportModeOnServiceJourney();
    List<ValidationIssue> validationIssues =
      validateAllowedTransportMode.validate(xpathRuleValidationContext);
    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testInvalidTransportModeOnServiceJourney() {
    XPathRuleValidationContext xpathRuleValidationContext =
      createXPathValidationContext(
        "<TransportMode>xxx</TransportMode>",
        "<TransportSubmode><BusSubmode>localBus</BusSubmode></TransportSubmode>"
      );

    ValidateAllowedTransportMode validateAllowedTransportMode =
      new ValidateAllowedTransportModeOnServiceJourney();
    List<ValidationIssue> validationIssues =
      validateAllowedTransportMode.validate(xpathRuleValidationContext);
    assertEquals(1, validationIssues.size());
    ValidationIssue validationIssue = validationIssues.get(0);
    assertEquals(validateAllowedTransportMode.rule(), validationIssue.rule());
  }

  @Test
  void testValidTransportSubModeOnServiceJourney() {
    XPathRuleValidationContext xpathRuleValidationContext =
      createXPathValidationContext(
        "<TransportMode>bus</TransportMode>",
        "<TransportSubmode><BusSubmode>localBus</BusSubmode></TransportSubmode>"
      );

    ValidateAllowedTransportSubMode validateAllowedTransportSubMode =
      new ValidateAllowedTransportSubModeOnServiceJourney();
    List<ValidationIssue> validationIssues =
      validateAllowedTransportSubMode.validate(xpathRuleValidationContext);
    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testInvalidTransportSubModeOnServiceJourney() {
    XPathRuleValidationContext xpathRuleValidationContext =
      createXPathValidationContext(
        "<TransportMode>bus</TransportMode>",
        "<TransportSubmode><BusSubmode>xxx</BusSubmode></TransportSubmode>"
      );

    ValidateAllowedTransportSubMode validateAllowedTransportSubMode =
      new ValidateAllowedTransportSubModeOnServiceJourney();
    List<ValidationIssue> validationIssues =
      validateAllowedTransportSubMode.validate(xpathRuleValidationContext);
    assertEquals(1, validationIssues.size());
    ValidationIssue validationIssue = validationIssues.get(0);
    assertEquals(
      validateAllowedTransportSubMode.rule(),
      validationIssue.rule()
    );
  }

  private XPathRuleValidationContext createXPathValidationContext(
    String transportMode,
    String transportSubmode
  ) {
    return TestValidationContextBuilder
      .ofNetexFragment(
        NETEX_FRAGMENT
          .replace("${TRANSPORT_MODE}", transportMode)
          .replace("${TRANSPORT_SUBMODE}", transportSubmode)
      )
      .build();
  }
}

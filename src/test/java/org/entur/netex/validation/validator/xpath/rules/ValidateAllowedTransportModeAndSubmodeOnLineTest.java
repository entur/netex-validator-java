package org.entur.netex.validation.validator.xpath.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.Test;

class ValidateAllowedTransportModeAndSubmodeOnLineTest {

  private static final String NETEX_FRAGMENT =
    """
      <ServiceFrame xmlns="http://www.netex.org.uk/netex">
               <lines >
                      <${LINE} version="325" id="ATB:FlexibleLine:0a115187-d3c9-532f-afa4-3302534b1a40">
                        <Name>Nærøysund TB</Name>
                        ${TRANSPORT_MODE}
                        ${TRANSPORT_SUBMODE}
                        <PublicCode>AtB Bestill</PublicCode>
                        <PrivateCode>Nærøysund TB til linje 660 Rørvik</PrivateCode>
                        <OperatorRef ref="ATB:Operator:1"/>
                        <RepresentedByGroupRef ref="ATB:Network:78977cc2-ba79-5492-af19-4d3fd5191876"/>
                      </${LINE}>
               </lines>
      </ServiceFrame>
      """;

  @Test
  void testValidTransportModeOnLine() {
    XPathRuleValidationContext xpathRuleValidationContext =
      createXPathValidationContext(
        "Line",
        "<TransportMode>bus</TransportMode>",
        "<TransportSubmode><BusSubmode>localBus</BusSubmode></TransportSubmode>"
      );

    ValidateAllowedTransportMode validateAllowedTransportMode =
      new ValidateAllowedTransportModeOnLine();
    List<ValidationIssue> validationIssues =
      validateAllowedTransportMode.validate(xpathRuleValidationContext);
    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testValidTransportModeOnFlexibleLine() {
    XPathRuleValidationContext xpathRuleValidationContext =
      createXPathValidationContext(
        "FlexibleLine",
        "<TransportMode>bus</TransportMode>",
        "<TransportSubmode><BusSubmode>localBus</BusSubmode></TransportSubmode>"
      );

    ValidateAllowedTransportMode validateAllowedTransportMode =
      new ValidateAllowedTransportModeOnLine();
    List<ValidationIssue> validationIssues =
      validateAllowedTransportMode.validate(xpathRuleValidationContext);
    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testInvalidTransportModeOnLine() {
    XPathRuleValidationContext xpathRuleValidationContext =
      createXPathValidationContext(
        "Line",
        "<TransportMode>xxx</TransportMode>",
        "<TransportSubmode><BusSubmode>localBus</BusSubmode></TransportSubmode>"
      );

    ValidateAllowedTransportMode validateAllowedTransportMode =
      new ValidateAllowedTransportModeOnLine();
    List<ValidationIssue> validationIssues =
      validateAllowedTransportMode.validate(xpathRuleValidationContext);
    assertEquals(1, validationIssues.size());
    ValidationIssue validationIssue = validationIssues.get(0);
    assertEquals(validateAllowedTransportMode.rule(), validationIssue.rule());
  }

  @Test
  void testInvalidTransportModeOnFlexibleLine() {
    XPathRuleValidationContext xpathRuleValidationContext =
      createXPathValidationContext(
        "FlexibleLine",
        "<TransportMode>xxx</TransportMode>",
        "<TransportSubmode><BusSubmode>localBus</BusSubmode></TransportSubmode>"
      );

    ValidateAllowedTransportMode validateAllowedTransportMode =
      new ValidateAllowedTransportModeOnLine();
    List<ValidationIssue> validationIssues =
      validateAllowedTransportMode.validate(xpathRuleValidationContext);
    assertEquals(1, validationIssues.size());
    ValidationIssue validationIssue = validationIssues.get(0);
    assertEquals(validateAllowedTransportMode.rule(), validationIssue.rule());
  }

  @Test
  void testValidTransportSubModeOnLine() {
    XPathRuleValidationContext xpathRuleValidationContext =
      createXPathValidationContext(
        "Line",
        "<TransportMode>bus</TransportMode>",
        "<TransportSubmode><BusSubmode>localBus</BusSubmode></TransportSubmode>"
      );

    ValidateAllowedTransportSubMode validateAllowedTransportSubMode =
      new ValidateAllowedTransportSubModeOnLine();
    List<ValidationIssue> validationIssues =
      validateAllowedTransportSubMode.validate(xpathRuleValidationContext);
    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testInvalidTransportSubModeOnLine() {
    XPathRuleValidationContext xpathRuleValidationContext =
      createXPathValidationContext(
        "Line",
        "<TransportMode>bus</TransportMode>",
        "<TransportSubmode><BusSubmode>xxx</BusSubmode></TransportSubmode>"
      );

    ValidateAllowedTransportSubMode validateAllowedTransportSubMode =
      new ValidateAllowedTransportSubModeOnLine();
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
    String line,
    String transportMode,
    String transportSubmode
  ) {
    return TestValidationContextBuilder
      .ofNetexFragment(
        NETEX_FRAGMENT
          .replace("${TRANSPORT_MODE}", transportMode)
          .replace("${TRANSPORT_SUBMODE}", transportSubmode)
          .replace("${LINE}", line)
      )
      .build();
  }
}

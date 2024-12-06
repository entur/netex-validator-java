package org.entur.netex.validation.validator.xpath.rules;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidateNotExistTest {

  private static final String TEST_CODESPACE = "FLB";
  private static final NetexXMLParser NETEX_XML_PARSER = new NetexXMLParser();
  private static final String NETEX_FRAGMENT =
    """
                    <ServiceFrame xmlns="http://www.netex.org.uk/netex" id="ATB:ServiceFrame:1" version="2223">
                      <lines>
                        <Line id="ATB:Line:2_1" version="2223">
                        </Line>
                      </lines>
                    </ServiceFrame>
                    """;
  private static final ValidationRule VALIDATION_RULE = new ValidationRule(
    "RULE_CODE",
    "RULE_NAME",
    Severity.ERROR
  );

  private XPathRuleValidationContext xpathRuleValidationContext;

  @BeforeEach
  void setUp() {
    XdmNode document = NETEX_XML_PARSER.parseStringToXdmNode(NETEX_FRAGMENT);
    xpathRuleValidationContext =
      new XPathRuleValidationContext(
        document,
        NETEX_XML_PARSER,
        TEST_CODESPACE,
        null
      );
  }

  @Test
  void testMatch() {
    ValidateNotExist validateNotExist = new ValidateNotExist(
      "ServiceFrame/lines/Line",
      VALIDATION_RULE
    );

    List<ValidationIssue> validationIssues = validateNotExist.validate(
      xpathRuleValidationContext
    );
    assertEquals(1, validationIssues.size());
    assertEquals(VALIDATION_RULE, validationIssues.get(0).rule());
  }

  @Test
  void testNoMatch() {
    ValidateNotExist validateNotExist = new ValidateNotExist(
      "ServiceFrame/lines/FlexibleLine",
      VALIDATION_RULE
    );

    List<ValidationIssue> validationIssues = validateNotExist.validate(
      xpathRuleValidationContext
    );
    assertTrue(validationIssues.isEmpty());
  }
}

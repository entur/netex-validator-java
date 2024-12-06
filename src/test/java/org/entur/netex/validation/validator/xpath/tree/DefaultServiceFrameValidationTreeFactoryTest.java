package org.entur.netex.validation.validator.xpath.tree;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultServiceFrameValidationTreeFactoryTest {

  public static final String TEST_CODESPACE = "ATB";
  private static final NetexXMLParser NETEX_XML_PARSER = new NetexXMLParser();
  private ValidationTree validationTree;

  @BeforeEach
  void setUp() {
    DefaultServiceFrameValidationTreeFactory factory =
      new DefaultServiceFrameValidationTreeFactory();
    validationTree = factory.buildValidationTree();
  }

  private static final String NETEX_FRAGMENT_LINE =
    """
                    <ServiceFrame xmlns="http://www.netex.org.uk/netex" id="ATB:ServiceFrame:1" version="2223">
                      <lines>
                        <Line id="ATB:Line:2_1" version="2223">
                        </Line>
                      </lines>
                    </ServiceFrame>
                    """;

  @Test
  void test() {
    XdmNode document = NETEX_XML_PARSER.parseStringToXdmNode(
      NETEX_FRAGMENT_LINE
    );
    XPathRuleValidationContext xpathValidationContext =
      new XPathRuleValidationContext(
        document,
        NETEX_XML_PARSER,
        TEST_CODESPACE,
        null
      );

    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      "LINE_1"
    );
    assertEquals(1, validationIssues.size());
    assertEquals("LINE_1", validationIssues.get(0).rule().code());
  }
}

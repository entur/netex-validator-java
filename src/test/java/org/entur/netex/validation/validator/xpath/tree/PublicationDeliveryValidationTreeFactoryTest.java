package org.entur.netex.validation.validator.xpath.tree;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.Test;

class PublicationDeliveryValidationTreeFactoryTest {

  public static final String TEST_CODESPACE = "ATB";
  private static final NetexXMLParser NETEX_XML_PARSER = new NetexXMLParser(
    Set.of("SiteFrame")
  );

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
    PublicationDeliveryValidationTreeFactory factory =
      new PublicationDeliveryValidationTreeFactory();
    ValidationTree validationTree = factory.buildValidationTree();
    assertNotNull(validationTree);

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
    assertNotNull(validationIssues);
  }
}

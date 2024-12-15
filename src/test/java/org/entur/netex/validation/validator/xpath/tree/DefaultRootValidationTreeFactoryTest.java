package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.tree.DefaultRootValidationTreeFactory.CODE_VERSION_NON_NUMERIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.Test;

class DefaultRootValidationTreeFactoryTest {

  private static final String NETEX_FRAGMENT_INVALID =
    """
<PublicationDelivery xmlns="http://www.netex.org.uk/netex" xmlns:ns2="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri" version="1.15:NO-NeTEx-networktimetable:1.5">
    <dataObjects>
        <CompositeFrame created="2024-11-28T02:58:39.64" version="1" id="ENT:CompositeFrame:3126575">
            <frames>
              <ServiceFrame id="ENT:ServiceFrame:1" version="2223">
                <lines>
                  <Line id="ENT:Line:2_1" version="XXXX">
                  </Line>
                </lines>
              </ServiceFrame>
            </frames>
        </CompositeFrame>
    </dataObjects>
</PublicationDelivery>
        """;

  private static final String NETEX_FRAGMENT_VALID =
    """
<PublicationDelivery xmlns="http://www.netex.org.uk/netex" xmlns:ns2="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri" version="1.15:NO-NeTEx-networktimetable:1.5">
    <dataObjects>
        <CompositeFrame created="2024-11-28T02:58:39.64" version="1" id="ENT:CompositeFrame:3126575">
            <frames>
              <ServiceFrame id="ENT:ServiceFrame:1" version="2223">
                <lines>
                  <Line id="ENT:Line:2_1" version="2223">
                  </Line>
                </lines>
              </ServiceFrame>
            </frames>
        </CompositeFrame>
    </dataObjects>
</PublicationDelivery>
        """;

  @Test
  void testNonNumericVersion() {
    ValidationTree validationTree = new DefaultRootValidationTreeFactory()
      .builder()
      .build();
    XPathRuleValidationContext xpathValidationContext =
      TestValidationContextBuilder
        .ofNetexFragment(NETEX_FRAGMENT_INVALID)
        .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_VERSION_NON_NUMERIC
    );
    assertEquals(1, validationIssues.size());
    assertEquals(
      CODE_VERSION_NON_NUMERIC,
      validationIssues.get(0).rule().code()
    );
  }

  @Test
  void testNumericVersion() {
    ValidationTree validationTree = new DefaultRootValidationTreeFactory()
      .builder()
      .build();
    XPathRuleValidationContext xpathValidationContext =
      TestValidationContextBuilder
        .ofNetexFragment(NETEX_FRAGMENT_VALID)
        .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_VERSION_NON_NUMERIC
    );
    assertTrue(validationIssues.isEmpty());
  }
}

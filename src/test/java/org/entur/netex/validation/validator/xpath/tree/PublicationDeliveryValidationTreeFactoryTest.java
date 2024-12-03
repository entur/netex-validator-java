package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.tree.DefaultServiceFrameValidationTreeFactory.CODE_LINE_2;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.entur.netex.validation.validator.xpath.support.XPathTestSupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class PublicationDeliveryValidationTreeFactoryTest {

  private static final String COMPOSITE_FRAME_FRAGMENT =
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

  private static final String SINGLE_FRAME_FRAGMENT =
    """
      <PublicationDelivery xmlns="http://www.netex.org.uk/netex" xmlns:ns2="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri" version="1.15:NO-NeTEx-networktimetable:1.5">
          <dataObjects>
            <ServiceFrame id="ENT:ServiceFrame:1" version="2223">
              <lines>
                <Line id="ENT:Line:2_1" version="2223">
                </Line>
              </lines>
            </ServiceFrame>
          </dataObjects>
      </PublicationDelivery>
      """;

  @Disabled
  @Test
  void testMatchFrameInCompositeFrame() {
    ValidationTree validationTree =
      new PublicationDeliveryValidationTreeFactory().builder().build();
    assertNotNull(validationTree);

    XPathRuleValidationContext validationContext =
      XPathTestSupport.validationContext(COMPOSITE_FRAME_FRAGMENT);

    List<ValidationIssue> validationIssues = validationTree.validate(
      validationContext,
      CODE_LINE_2
    );
    assertFalse(validationIssues.isEmpty());
  }

  @Disabled
  @Test
  void testMatchSingleFrame() {
    ValidationTree validationTree =
      new PublicationDeliveryValidationTreeFactory().builder().build();
    assertNotNull(validationTree);

    XPathRuleValidationContext validationContext =
      XPathTestSupport.validationContext(SINGLE_FRAME_FRAGMENT);

    List<ValidationIssue> validationIssues = validationTree.validate(
      validationContext,
      CODE_LINE_2
    );
    assertFalse(validationIssues.isEmpty());
  }
}

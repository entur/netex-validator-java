package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.tree.DefaultCompositeFrameTreeFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultCompositeFrameTreeFactoryTest {

  private static final String NETEX_FRAGMENT_INVALID =
    """
<CompositeFrame xmlns="http://www.netex.org.uk/netex" created="2024-11-28T14:40:57" version="663" id="GOA:CompositeFrame:Shared">
      <frames>
        <!-- Invalid SiteFrame -->
        <SiteFrame/>
        <ResourceFrame>
           <!-- Invalid nested validityConditions -->
           <validityConditions>
                <AvailabilityCondition version="663" id="GOA:AvailabilityCondition:Shared">
                  <FromDate>2024-11-28T00:00:00</FromDate>
                  <ToDate>2025-03-27T00:00:00</ToDate>
                </AvailabilityCondition>
          </validityConditions>
        </ResourceFrame>
      </frames>
</CompositeFrame>
                """;

  private static final String NETEX_FRAGMENT_VALID =
    """
     <CompositeFrame xmlns="http://www.netex.org.uk/netex" created="2024-11-28T14:40:57" version="663" id="GOA:CompositeFrame:Shared">
      <validityConditions>
        <AvailabilityCondition version="663" id="GOA:AvailabilityCondition:Shared">
          <FromDate>2024-11-28T00:00:00</FromDate>
          <ToDate>2025-03-27T00:00:00</ToDate>
        </AvailabilityCondition>
      </validityConditions>
      <codespaces>
        <Codespace id="goa">
          <Xmlns>GOA</Xmlns>
          <XmlnsUrl>http://www.rutebanken.org/ns/goa</XmlnsUrl>
        </Codespace>
        <Codespace id="nsr">
          <Xmlns>NSR</Xmlns>
          <XmlnsUrl>http://www.rutebanken.org/ns/nsr</XmlnsUrl>
        </Codespace>
        <Codespace id="pen">
          <Xmlns>PEN</Xmlns>
          <XmlnsUrl>http://www.rutebanken.org/ns/pen</XmlnsUrl>
        </Codespace>
      </codespaces>
      <FrameDefaults>
        <DefaultLocale>
          <TimeZone>Europe/Oslo</TimeZone>
          <DefaultLanguage>no</DefaultLanguage>
        </DefaultLocale>
        <DefaultLocationSystem>4326</DefaultLocationSystem>
      </FrameDefaults>
      <frames>
        <ResourceFrame version="663" id="GOA:ResourceFrame:Shared"/>
      </frames>
</CompositeFrame>
                      """;

  private ValidationTree validationTree;

  @BeforeEach
  void setUp() {
    validationTree = new DefaultCompositeFrameTreeFactory().builder().build();
  }

  static Stream<String> ruleCodes() {
    return Stream.of(
      CODE_COMPOSITE_FRAME_SITE_FRAME,
      CODE_COMPOSITE_FRAME_1,
      CODE_COMPOSITE_FRAME_2
    );
  }

  @ParameterizedTest
  @MethodSource("ruleCodes")
  void testInvalidResourceFrame(String code) {
    XPathRuleValidationContext xpathValidationContext =
      TestValidationContextBuilder
        .ofNetexFragment(NETEX_FRAGMENT_INVALID)
        .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      code
    );
    assertEquals(1, validationIssues.size());
    assertEquals(code, validationIssues.get(0).rule().code());
  }

  @ParameterizedTest
  @MethodSource("ruleCodes")
  void testValidResourceFrame(String code) {
    XPathRuleValidationContext xpathValidationContext =
      TestValidationContextBuilder
        .ofNetexFragment(NETEX_FRAGMENT_VALID)
        .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      code
    );
    assertTrue(validationIssues.isEmpty());
  }
}

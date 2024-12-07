package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.entur.netex.validation.validator.xpath.support.XPathTestSupport.validationContext;
import static org.entur.netex.validation.validator.xpath.tree.DefaultServiceFrameValidationTreeFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultServiceFrameValidationTreeFactoryTest {

  private static final String NETEX_FRAGMENT_LINE_INVALID =
    """
  <ServiceFrame xmlns="http://www.netex.org.uk/netex">
  <Network version="0" id="BRA:Network:e7f2a84e-2a94-4899-b833-37d18cddb26f">
  </Network>
  <routes>
          <Route version="0" id="SJN:Route:79-R">
              <DirectionRef/>
            </Route>
  </routes>
  <lines>
    <Line id="ENT:Line:2_1" version="2223">
        <routes>
          <Route/>
        </routes>
        <Presentation>
            <Colour>E13</Colour>
            <TextColour>XXXXXX</TextColour>
          </Presentation>
    </Line>
  </lines>
  </ServiceFrame>

""";

  private static final String NETEX_FRAGMENT_LINE_VALID =
          """
        <ServiceFrame xmlns="http://www.netex.org.uk/netex">
        <routes>
          <Route version="0" id="SJN:Route:79-R">
              <Name>BO - ROG</Name>
              <ShortName>BO-ROG</ShortName>
              <LineRef ref="SJN:Line:79" version="3" />
              <pointsInSequence>
                <PointOnRoute order="1" version="0" id="SJN:PointOnRoute:79-R-1">
                  <RoutePointRef ref="SJN:RoutePoint:BO" />
                </PointOnRoute>
                <PointOnRoute order="2" version="0" id="SJN:PointOnRoute:79-R-2">
                  <RoutePointRef ref="SJN:RoutePoint:MOeR" />
                </PointOnRoute>
              </pointsInSequence>
            </Route>
        </routes>
        <lines>
          <Line id="ENT:Line:2_1" version="2223">
          <Name>Harstad/Narvik-Ørland</Name>
          <TransportMode>air</TransportMode>
          <TransportSubmode><AirSubmode>domesticFlight</AirSubmode></TransportSubmode>
          <PublicCode>R75</PublicCode>
          <RepresentedByGroupRef ref="AVI:Network:DX"/>
          <Presentation>
            <Colour>E13E2E</Colour>
            <TextColour>FFFFFF</TextColour>
          </Presentation>
          </Line>
        </lines>
        </ServiceFrame>
      
      """;

  private ValidationTree validationTree;

  @BeforeEach
  void setUp() {
    validationTree = new DefaultServiceFrameValidationTreeFactory().buildValidationTree();
  }


  static Stream<String> ruleCodes() {
    return Stream.of(
            CODE_LINE_2, CODE_LINE_3, CODE_LINE_4, CODE_LINE_5, CODE_LINE_6, CODE_LINE_7, CODE_LINE_8,
            CODE_NETWORK_1, CODE_NETWORK_2,
            CODE_ROUTE_2,CODE_ROUTE_3,CODE_ROUTE_4,CODE_ROUTE_5);  }

  @ParameterizedTest
  @MethodSource("ruleCodes")
  void testInvalidServiceFrame(String code) {
    XPathRuleValidationContext xpathValidationContext = validationContext(
            NETEX_FRAGMENT_LINE_INVALID
    );
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
            code
    );
    assertEquals(1, validationIssues.size());
    assertEquals(code, validationIssues.get(0).rule().code());
  }

  @ParameterizedTest
  @MethodSource("ruleCodes")
  void testValidServiceFrame(String code) {
    XPathRuleValidationContext xpathValidationContext = validationContext(
            NETEX_FRAGMENT_LINE_VALID
    );
    List<ValidationIssue> validationIssues = validationTree.validate(
            xpathValidationContext,
            code
    );
    assertTrue(validationIssues.isEmpty());
  }


}

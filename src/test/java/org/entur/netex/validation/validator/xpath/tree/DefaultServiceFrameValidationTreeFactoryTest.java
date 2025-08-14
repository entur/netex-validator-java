package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.tree.DefaultServiceFrameValidationTreeFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultServiceFrameValidationTreeFactoryTest {

  private static final String NETEX_FRAGMENT_INVALID =
    """
<ServiceFrame xmlns="http://www.netex.org.uk/netex">
  <Network version="0" id="ENT:Network:e7f2a84e-2a94-4899-b833-37d18cddb26f">
  </Network>
  <routePoints>
    <RoutePoint/>
  </routePoints>
  <routes>
      <Route version="0" id="ENT:Route:79-R">
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
  <groupsOfLines/>
  <timingPoints/>
  <destinationDisplays>
    <DestinationDisplay version="0" id="ENT:DestinationDisplay:21-1_OsloS">
        <vias>
           <Via/>
        </vias>
    </DestinationDisplay>
  </destinationDisplays>
  <stopAssignments>
      <PassengerStopAssignment order="1" version="0" id="ENT:PassengerStopAssignment:ALV-1"/>
      <PassengerStopAssignment order="2" version="0" id="ENT:PassengerStopAssignment:ATN-1">
        <ScheduledStopPointRef ref="ENT:ScheduledStopPoint:ATN-1" version="0" />
        <QuayRef ref="NSR:Quay:714" />
      </PassengerStopAssignment>
      <PassengerStopAssignment order="3" version="0" id="ENT:PassengerStopAssignment:ATN-2">
        <ScheduledStopPointRef ref="ENT:ScheduledStopPoint:ATN-2" version="0" />
        <QuayRef ref="NSR:Quay:714" />
      </PassengerStopAssignment>
  </stopAssignments>
</ServiceFrame>

""";

  private static final String NETEX_FRAGMENT_VALID =
    """
        <ServiceFrame xmlns="http://www.netex.org.uk/netex">
          <Network version="0" id="ENT:Network:ENT">
                  <Name>ENT</Name>
                  <AuthorityRef ref="ENT:Authority:ENT" version="1" />
          </Network>
          <routePoints>
          <RoutePoint version="0" id="ENT:RoutePoint:ALV">
            <keyList>
              <KeyValue>
                <Key>UIC</Key>
                <Value>007600926</Value>
              </KeyValue>
            </keyList>
            <Name>Alvdal</Name>
            <Location>
              <Longitude>10.632059</Longitude>
              <Latitude>62.109443</Latitude>
            </Location>
            <projections>
              <PointProjection version="0" id="ENT:PointProjection:ALV-1">
                <ProjectedPointRef ref="ENT:ScheduledStopPoint:ALV-1" version="0" />
              </PointProjection>
            </projections>
          </RoutePoint>
         </routePoints>
        <routes>
          <Route version="0" id="ENT:Route:79-R">
              <Name>BO - ROG</Name>
              <ShortName>BO-ROG</ShortName>
              <LineRef ref="ENT:Line:79" version="3" />
              <pointsInSequence>
                <PointOnRoute order="1" version="0" id="ENT:PointOnRoute:79-R-1">
                  <RoutePointRef ref="ENT:RoutePoint:BO" />
                </PointOnRoute>
                <PointOnRoute order="2" version="0" id="ENT:PointOnRoute:79-R-2">
                  <RoutePointRef ref="ENT:RoutePoint:MOeR" />
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
        <destinationDisplays>
          <DestinationDisplay version="0" id="ENT:DestinationDisplay:21-1_OsloS">
            <FrontText>Oslo S</FrontText>
            <PublicCode>F6</PublicCode>
            <vias>
              <Via>
                <DestinationDisplayRef ref="OST:DestinationDisplay:133" version="1"></DestinationDisplayRef>
              </Via>
            </vias>
          </DestinationDisplay>
        </destinationDisplays>
        <stopAssignments>
          <PassengerStopAssignment order="1" version="0" id="ENT:PassengerStopAssignment:ALV-1">
            <ScheduledStopPointRef ref="ENT:ScheduledStopPoint:ALV-1" version="0" />
            <QuayRef ref="NSR:Quay:650" />
          </PassengerStopAssignment>
        </stopAssignments>
        </ServiceFrame>
      
      """;

  private static final String NETEX_FRAGMENT_INVALID_SERVICE_LINK_NO_PROJECTION =
    """
<ServiceFrame xmlns="http://www.netex.org.uk/netex">
     <serviceLinks>
        <ServiceLink id="BRA:ServiceLink:210108111955158_2_210108111955389_1_2030" version="1563">
              <Distance>2030.0</Distance>
              <FromPointRef ref="BRA:ScheduledStopPoint:6233600_2" version="1563" />
              <ToPointRef ref="BRA:ScheduledStopPoint:6239930_1" version="1563" />
        </ServiceLink>
    </serviceLinks>
</ServiceFrame>
       """;

  private static final String NETEX_FRAGMENT_INVALID_SERVICE_LINK_NO_COORDINATE_LIST =
    """
<ServiceFrame xmlns="http://www.netex.org.uk/netex" xmlns:gml="http://www.opengis.net/gml/3.2">
     <serviceLinks>
        <ServiceLink id="BRA:ServiceLink:210108111955158_2_210108111955389_1_2030" version="1563">
              <Distance>2030.0</Distance>
                           <projections>
                              <LinkSequenceProjection id="BRA:LinkSequenceProjection:210108111955158_2_210108111955389_1_2030" version="1563">
                                <gml:LineString gml:id="id_210108111955158_2_210108111955389_1_2030" srsName="WGS84">
                                <gml:posList count="2" srsDimension="2"></gml:posList>
                                </gml:LineString>
                              </LinkSequenceProjection>
                           </projections>
              <FromPointRef ref="BRA:ScheduledStopPoint:6233600_2" version="1563" />
              <ToPointRef ref="BRA:ScheduledStopPoint:6239930_1" version="1563" />
        </ServiceLink>
    </serviceLinks>
</ServiceFrame>
       """;

  private static final String NETEX_FRAGMENT_SERVICE_LINK_VALID_COORDINATE_LIST =
    """
<ServiceFrame xmlns="http://www.netex.org.uk/netex" xmlns:gml="http://www.opengis.net/gml/3.2">
     <serviceLinks>
        <ServiceLink id="BRA:ServiceLink:210108111955158_2_210108111955389_1_2030" version="1563">
              <Distance>2030.0</Distance>
                           <projections>
                              <LinkSequenceProjection id="BRA:LinkSequenceProjection:210108111955158_2_210108111955389_1_2030" version="1563">
                                <gml:LineString gml:id="id_210108111955158_2_210108111955389_1_2030" srsName="WGS84">
                                  <gml:posList count="2" srsDimension="2">59.551598 9.803644  59.5307 9.805698</gml:posList>
                                </gml:LineString>
                              </LinkSequenceProjection>
                           </projections>
              <FromPointRef ref="BRA:ScheduledStopPoint:6233600_2" version="1563" />
              <ToPointRef ref="BRA:ScheduledStopPoint:6239930_1" version="1563" />
        </ServiceLink>
    </serviceLinks>
</ServiceFrame>
       """;

  private static final String NETEX_FRAGMENT_INVALID_SERVICE_LINK_LESS_THAN_TWO_POINTS =
    """
<ServiceFrame xmlns="http://www.netex.org.uk/netex" xmlns:gml="http://www.opengis.net/gml/3.2">
     <serviceLinks>
        <ServiceLink id="BRA:ServiceLink:210108111955158_2_210108111955389_1_2030" version="1563">
              <Distance>2030.0</Distance>
                           <projections>
                              <LinkSequenceProjection id="BRA:LinkSequenceProjection:210108111955158_2_210108111955389_1_2030" version="1563">
                                <gml:LineString gml:id="id_210108111955158_2_210108111955389_1_2030" srsName="WGS84">
                                   <gml:pos>60.075463 9.121449</gml:pos>
                                </gml:LineString>
                              </LinkSequenceProjection>
                           </projections>
              <FromPointRef ref="BRA:ScheduledStopPoint:6233600_2" version="1563" />
              <ToPointRef ref="BRA:ScheduledStopPoint:6239930_1" version="1563" />
        </ServiceLink>
    </serviceLinks>
</ServiceFrame>
       """;

  private static final String NETEX_FRAGMENT_SERVICE_LINK_VALID_TWO_POINTS =
    """
<ServiceFrame xmlns="http://www.netex.org.uk/netex" xmlns:gml="http://www.opengis.net/gml/3.2">
     <serviceLinks>
        <ServiceLink id="BRA:ServiceLink:210108111955158_2_210108111955389_1_2030" version="1563">
              <Distance>2030.0</Distance>
                           <projections>
                              <LinkSequenceProjection id="BRA:LinkSequenceProjection:210108111955158_2_210108111955389_1_2030" version="1563">
                                <gml:LineString gml:id="id_210108111955158_2_210108111955389_1_2030" srsName="WGS84">
                                   <gml:pos>60.075463 9.121449</gml:pos>
                                    <gml:pos>60.07577 9.121238</gml:pos>
                                </gml:LineString>
                              </LinkSequenceProjection>
                           </projections>
              <FromPointRef ref="BRA:ScheduledStopPoint:6233600_2" version="1563" />
              <ToPointRef ref="BRA:ScheduledStopPoint:6239930_1" version="1563" />
        </ServiceLink>
    </serviceLinks>
</ServiceFrame>
       """;

  private ValidationTree validationTree;

  @BeforeEach
  void setUp() {
    validationTree = new DefaultServiceFrameValidationTreeFactory().builder().build();
  }

  static Stream<String> ruleCodes() {
    return Stream.of(
      CODE_LINE_2,
      CODE_LINE_3,
      CODE_LINE_4,
      CODE_LINE_5,
      CODE_LINE_6,
      CODE_LINE_7,
      CODE_LINE_8,
      CODE_NETWORK_1,
      CODE_NETWORK_2,
      CODE_ROUTE_2,
      CODE_ROUTE_3,
      CODE_ROUTE_4,
      CODE_ROUTE_5,
      CODE_SERVICE_FRAME_1,
      CODE_SERVICE_FRAME_2,
      CODE_SERVICE_FRAME_3,
      CODE_PASSENGER_STOP_ASSIGNMENT_1,
      CODE_PASSENGER_STOP_ASSIGNMENT_2,
      CODE_PASSENGER_STOP_ASSIGNMENT_3,
      CODE_DESTINATION_DISPLAY_1,
      CODE_DESTINATION_DISPLAY_2
    );
  }

  @ParameterizedTest
  @MethodSource("ruleCodes")
  void testInvalidServiceFrame(String code) {
    XPathRuleValidationContext xpathValidationContext = TestValidationContextBuilder
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
  void testValidServiceFrame(String code) {
    XPathRuleValidationContext xpathValidationContext = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_VALID)
      .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      code
    );
    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testMissingProjectionOnServiceLink() {
    XPathRuleValidationContext xpathValidationContext = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_INVALID_SERVICE_LINK_NO_PROJECTION)
      .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_SERVICE_LINK_3
    );
    assertEquals(1, validationIssues.size());
  }

  @Test
  void testMissingCoordinateListOnServiceLink() {
    XPathRuleValidationContext xpathValidationContext = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_INVALID_SERVICE_LINK_NO_COORDINATE_LIST)
      .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_SERVICE_LINK_4
    );
    assertEquals(1, validationIssues.size());
  }

  @Test
  void testValidCoordinateListOnServiceLink() {
    XPathRuleValidationContext xpathValidationContext = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_SERVICE_LINK_VALID_COORDINATE_LIST)
      .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_SERVICE_LINK_4
    );
    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testLessThanTwoPointsOnServiceLink() {
    XPathRuleValidationContext xpathValidationContext = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_INVALID_SERVICE_LINK_LESS_THAN_TWO_POINTS)
      .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_SERVICE_LINK_5
    );
    assertEquals(1, validationIssues.size());
  }

  @Test
  void testServiceLinkWithTwoPoints() {
    XPathRuleValidationContext xpathValidationContext = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_SERVICE_LINK_VALID_TWO_POINTS)
      .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_SERVICE_LINK_5
    );
    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testServiceLinkWithPosList() {
    XPathRuleValidationContext xpathValidationContext = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_SERVICE_LINK_VALID_COORDINATE_LIST)
      .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_SERVICE_LINK_5
    );
    assertTrue(validationIssues.isEmpty());
  }
}

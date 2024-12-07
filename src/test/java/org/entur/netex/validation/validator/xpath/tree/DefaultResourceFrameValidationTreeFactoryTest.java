package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.support.XPathTestSupport.validationContext;
import static org.entur.netex.validation.validator.xpath.tree.DefaultServiceFrameValidationTreeFactory.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultResourceFrameValidationTreeFactoryTest {

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

  private ValidationTree validationTree;

  @BeforeEach
  void setUp() {
    validationTree =
      new DefaultServiceFrameValidationTreeFactory().buildValidationTree();
  }

  static Stream<String> ruleCodes() {
    return Stream.of(
    );
  }

  @ParameterizedTest
  @MethodSource("ruleCodes")
  void testInvalidServiceFrame(String code) {
    XPathRuleValidationContext xpathValidationContext = validationContext(
      NETEX_FRAGMENT_INVALID
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
      NETEX_FRAGMENT_VALID
    );
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      code
    );
    assertTrue(validationIssues.isEmpty());
  }
}

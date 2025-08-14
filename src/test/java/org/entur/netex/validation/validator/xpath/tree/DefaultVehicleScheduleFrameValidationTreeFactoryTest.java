package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.tree.DefaultVehicleScheduleFrameValidationTreeFactory.*;
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

class DefaultVehicleScheduleFrameValidationTreeFactoryTest {

  private static final String NETEX_FRAGMENT_INVALID =
    """
<VehicleScheduleFrame xmlns="http://www.netex.org.uk/netex" version="2024112835803" id="INN:VehicleScheduleFrame:INN">
    <blocks>
        <Block id="INN:Block:1" version="2024112835803">
            <Name>6001</Name>
            <Description>60/E/6001</Description>
            <PrivateCode>6001</PrivateCode>
            <StartTime>07:39:00</StartTime>
            <EndTime>15:16:00</EndTime>
            <StartPointRef ref="INN:ScheduledStopPoint:10013" version="2024112835803"/>
            <EndPointRef ref="INN:ScheduledStopPoint:10094" version="2024112835803"/>
        </Block>
    </blocks>
</VehicleScheduleFrame>
       
        """;

  private static final String NETEX_FRAGMENT_VALID =
    """
<VehicleScheduleFrame xmlns="http://www.netex.org.uk/netex" version="2024112835803" id="INN:VehicleScheduleFrame:INN">
    <blocks>
        <Block id="INN:Block:1" version="2024112835803">
            <Name>6001</Name>
            <Description>60/E/6001</Description>
            <PrivateCode>6001</PrivateCode>
            <StartTime>07:39:00</StartTime>
            <EndTime>15:16:00</EndTime>
            <dayTypes>
                <DayTypeRef ref="INN:DayType:Cal2024112835803-Nov_Wed_27-Apr_Sun_13-MoTuWeThFrSaSu_3" version="2024112835803"/>
            </dayTypes>
            <StartPointRef ref="INN:ScheduledStopPoint:10013" version="2024112835803"/>
            <EndPointRef ref="INN:ScheduledStopPoint:10094" version="2024112835803"/>
            <journeys>
                <VehicleJourneyRef ref="INN:ServiceJourney:409_2_5"/>
                <VehicleJourneyRef ref="INN:ServiceJourney:409_1_1"/>
                <VehicleJourneyRef ref="INN:ServiceJourney:409_2_6"/>
                <VehicleJourneyRef ref="INN:ServiceJourney:409_1_2"/>
                <VehicleJourneyRef ref="INN:ServiceJourney:409_2_7"/>
                <VehicleJourneyRef ref="INN:ServiceJourney:409_1_3"/>
                <VehicleJourneyRef ref="INN:ServiceJourney:409_2_8"/>
                <VehicleJourneyRef ref="INN:ServiceJourney:409_1_4"/>
            </journeys>
        </Block>
    </blocks>
</VehicleScheduleFrame>
              """;

  private ValidationTree validationTree;

  @BeforeEach
  void setUp() {
    validationTree =
      new DefaultVehicleScheduleFrameValidationTreeFactory().builder().build();
  }

  static Stream<String> ruleCodes() {
    return Stream.of(CODE_BLOCK_2, CODE_BLOCK_3);
  }

  @ParameterizedTest
  @MethodSource("ruleCodes")
  void testInvalidVehicleScheduleFrame(String code) {
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
  void testValidScheduleFrame(String code) {
    XPathRuleValidationContext xpathValidationContext = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_VALID)
      .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      code
    );
    assertTrue(validationIssues.isEmpty());
  }
}

package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.tree.DefaultTimetableFrameValidationTreeFactory.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultTimetableFrameValidationTreeFactoryTest {

  private static final String NETEX_FRAGMENT_INVALID =
    """
<TimetableFrame xmlns="http://www.netex.org.uk/netex" version="1" id="AKT:TimetableFrame:484392">
         <vehicleJourneys>
         
           <ServiceJourney version="1" id="AKT:ServiceJourney:36_1_4_31_19_52200_52920_3910363">
             <Name>Birkeland Tomta -&gt; Grødum snuplass, 19</Name>
             <PrivateCode>31</PrivateCode>
             <TransportMode>air</TransportMode>
             <LineRef ref="AKT:Line:36" version="1"></LineRef>
             <calls/>
             <passingTimes>
               
               <!-- Missing departure on first stop -->
               <TimetabledPassingTime version="1" id="AKT:TimetabledPassingTime:569305876-569307020_1_1">
                 <StopPointInJourneyPatternRef ref="AKT:StopPointInJourneyPattern:65692_1_1" version="1"></StopPointInJourneyPatternRef>
                 <ArrivalTime>07:15:00</ArrivalTime>
               </TimetabledPassingTime>
               
               <!-- Missing departure and arrival-->
               <TimetabledPassingTime version="1" id="AKT:TimetabledPassingTime:569305876-569307020_1_1">
                 <StopPointInJourneyPatternRef ref="AKT:StopPointInJourneyPattern:65692_1_1" version="1"></StopPointInJourneyPatternRef>
               </TimetabledPassingTime>
               
                <!-- Identical departure and arrival-->
               <TimetabledPassingTime version="1" id="AKT:TimetabledPassingTime:569305876-569307020_1_1">
                 <StopPointInJourneyPatternRef ref="AKT:StopPointInJourneyPattern:65692_1_1" version="1"></StopPointInJourneyPatternRef>
                  <ArrivalTime>07:16:00</ArrivalTime>
                  <DepartureTime>07:16:00</DepartureTime>
               </TimetabledPassingTime>
               
               <!-- Missing id on TimetabledPassingTime -->
               <TimetabledPassingTime version="1" >
                 <StopPointInJourneyPatternRef ref="AKT:StopPointInJourneyPattern:65692_1_1" version="1"></StopPointInJourneyPatternRef>
                  <DepartureTime>07:17:00</DepartureTime>
               </TimetabledPassingTime>
               
               <!-- Missing version on TimetabledPassingTime -->
               <TimetabledPassingTime  id="AKT:TimetabledPassingTime:569305876-569307020_1_1">
                 <StopPointInJourneyPatternRef ref="AKT:StopPointInJourneyPattern:65692_1_1" version="1"></StopPointInJourneyPatternRef>
                  <ArrivalTime>07:18:00</ArrivalTime>
               </TimetabledPassingTime>
               
               <!-- Missing arrival on last stop-->
               <TimetabledPassingTime version="1" id="AKT:TimetabledPassingTime:569305876-569307020_19_1">
                 <StopPointInJourneyPatternRef ref="AKT:StopPointInJourneyPattern:65692_19_1" version="1"></StopPointInJourneyPatternRef>
                 <DepartureTime>07:27:00</DepartureTime>
               </TimetabledPassingTime>
             </passingTimes>
           </ServiceJourney>
         </vehicleJourneys>
 </TimetableFrame>
        """;

  private static final String NETEX_FRAGMENT_VALID =
    """
<TimetableFrame xmlns="http://www.netex.org.uk/netex" version="1" id="AKT:TimetableFrame:484392">
<vehicleJourneys>
<ServiceJourney version="1" id="AKT:ServiceJourney:36_1_4_31_19_52200_52920_3910363">
 <Name>Birkeland Tomta -&gt; Grødum snuplass, 19</Name>
 <PrivateCode>31</PrivateCode>
 <dayTypes>
   <DayTypeRef ref="AKT:DayType:10_1"></DayTypeRef>
 </dayTypes>
 <JourneyPatternRef ref="AKT:JourneyPattern:65692_1" version="1"></JourneyPatternRef>
 <OperatorRef ref="AKT:Operator:923"></OperatorRef>
 <LineRef ref="AKT:Line:36" version="1"></LineRef>
 <passingTimes>
   <TimetabledPassingTime version="1" id="AKT:TimetabledPassingTime:569305876-569307020_1_1">
     <StopPointInJourneyPatternRef ref="AKT:StopPointInJourneyPattern:65692_1_1" version="1"></StopPointInJourneyPatternRef>
     <DepartureTime>07:15:00</DepartureTime>
   </TimetabledPassingTime>
   <TimetabledPassingTime version="1" id="AKT:TimetabledPassingTime:569305876-569307020_19_1">
     <StopPointInJourneyPatternRef ref="AKT:StopPointInJourneyPattern:65692_19_1" version="1"></StopPointInJourneyPatternRef>
     <ArrivalTime>07:27:00</ArrivalTime>
   </TimetabledPassingTime>
 </passingTimes>
</ServiceJourney>
</vehicleJourneys>
</TimetableFrame>
""";

  private ValidationTree validationTree;

  @BeforeEach
  void setUp() {
    validationTree =
      new DefaultTimetableFrameValidationTreeFactory().builder().build();
  }

  static Stream<String> ruleCodes() {
    return Stream.of(
      CODE_SERVICE_JOURNEY_2,
      CODE_SERVICE_JOURNEY_4,
      CODE_SERVICE_JOURNEY_5,
      CODE_SERVICE_JOURNEY_6,
      CODE_SERVICE_JOURNEY_7,
      CODE_SERVICE_JOURNEY_8,
      CODE_SERVICE_JOURNEY_9,
      CODE_SERVICE_JOURNEY_10,
      CODE_SERVICE_JOURNEY_11,
      CODE_SERVICE_JOURNEY_12,
      CODE_SERVICE_JOURNEY_13
    );
  }

  @ParameterizedTest
  @MethodSource("ruleCodes")
  void testInvalidTimetableFrame(String code) {
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
  void testValidTimetableFrame(String code) {
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

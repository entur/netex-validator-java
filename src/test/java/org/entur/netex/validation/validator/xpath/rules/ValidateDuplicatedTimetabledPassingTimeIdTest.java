package org.entur.netex.validation.validator.xpath.rules;

import java.util.List;
import java.util.Set;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ValidateDuplicatedTimetabledPassingTimeIdTest {

  public static final String TEST_CODESPACE = "FLI";
  private static final NetexXMLParser NETEX_XML_PARSER = new NetexXMLParser(
    Set.of()
  );
  private static final String NETEX_FRAGMENT =
    """
                    <vehicleJourneys xmlns="http://www.netex.org.uk/netex">
                           <ServiceJourney version="1708599352" id="FLI:ServiceJourney:93b4d4ba65">
                               <Name>Oslo (Busterminalen Galleriet)</Name>
                               <dayTypes>
                                    <DayTypeRef ref="FLI:DayType:0319e54a8e"/>
                               </dayTypes>
                               <JourneyPatternRef ref="FLI:JourneyPattern:dc8d8815ee" version="1708599352"/>
                               <OperatorRef ref="FLI:Operator:FlixBus"/>
                               <LineRef ref="FLI:Line:1d81e78284" version="1"/>
                               <passingTimes>
                                   <TimetabledPassingTime version="1708599352" ${ID_1}>
                                       <StopPointInJourneyPatternRef ref="FLI:StopPointInJourneyPattern:276cef3c14-1" version="1708599352"/>
                                       <DepartureTime>18:00:00</DepartureTime>
                                   </TimetabledPassingTime>
                                   <TimetabledPassingTime version="1708599352" ${ID_2}>
                                       <StopPointInJourneyPatternRef ref="FLI:StopPointInJourneyPattern:593de1325d-2" version="1708599352"/>
                                       <ArrivalTime>19:00:00</ArrivalTime>
                                       <DepartureTime>19:05:00</DepartureTime>
                                   </TimetabledPassingTime>
                               </passingTimes>
                           </ServiceJourney>
                           <ServiceJourney version="1708599352" id="FLI:ServiceJourney:ee0431e315">
                               <Name>Oslo (Busterminalen Galleriet)</Name>
                               <dayTypes>
                                    <DayTypeRef ref="FLI:DayType:557828265f"/>
                               </dayTypes>
                               <JourneyPatternRef ref="FLI:JourneyPattern:dc8d8815ee" version="1708599352"/>
                               <OperatorRef ref="FLI:Operator:FlixBus"/>
                               <LineRef ref="FLI:Line:1d81e78284" version="1"/>
                               <passingTimes>
                                   <TimetabledPassingTime version="1708599352" ${ID_3}>
                                       <StopPointInJourneyPatternRef ref="FLI:StopPointInJourneyPattern:276cef3c14-1" version="1708599352"/>
                                       <DepartureTime>18:00:00</DepartureTime>
                                   </TimetabledPassingTime>
                                   <TimetabledPassingTime version="1708599352" ${ID_4}>
                                       <StopPointInJourneyPatternRef ref="FLI:StopPointInJourneyPattern:593de1325d-2" version="1708599352"/>
                                       <ArrivalTime>19:00:00</ArrivalTime>
                                       <DepartureTime>19:05:00</DepartureTime>
                                   </TimetabledPassingTime>
                               </passingTimes>
                           </ServiceJourney>
                    </vehicleJourneys>
                    """;

  static List<Arguments> timetabledPassingTimesIdCases() {
    return List.of(
      Arguments.of(
        List.of(
          "id=\"FLI:TimetabledPassingTime:ID1\"",
          "id=\"FLI:TimetabledPassingTime:ID2\"",
          "id=\"FLI:TimetabledPassingTime:ID3\"",
          "id=\"FLI:TimetabledPassingTime:ID4\""
        ),
        true,
        "Non-duplicated IDs should be accepted"
      ),
      Arguments.of(
        List.of("", "", "", ""),
        true,
        "Missing IDs should be accepted"
      ),
      Arguments.of(
        List.of(
          "id=\"FLI:TimetabledPassingTime:ID1\"",
          "id=\"FLI:TimetabledPassingTime:ID2\"",
          "id=\"FLI:TimetabledPassingTime:ID3\"",
          "id=\"FLI:TimetabledPassingTime:ID1\""
        ),
        false,
        "Duplicated IDs should be rejected"
      )
    );
  }

  /**
   */
  @ParameterizedTest
  @MethodSource("timetabledPassingTimesIdCases")
  void testTimetabledPassingTimesIds(
    List<String> ids,
    boolean accepted,
    String expectedMessage
  ) {
    ValidateDuplicatedTimetabledPassingTimeId validateDuplicatedTimetabledPassingTimeId =
      new ValidateDuplicatedTimetabledPassingTimeId("/");

    String vehicleJourneysFragment = NETEX_FRAGMENT
      .replace("${ID_1}", ids.get(0))
      .replace("${ID_2}", ids.get(1))
      .replace("${ID_3}", ids.get(2))
      .replace("${ID_4}", ids.get(3));

    XdmNode document = NETEX_XML_PARSER.parseStringToXdmNode(
      vehicleJourneysFragment
    );
    XPathRuleValidationContext xpathRuleValidationContext =
      new XPathRuleValidationContext(
        document,
        NETEX_XML_PARSER,
        TEST_CODESPACE,
        null
      );
    List<ValidationIssue> validationIssues =
      validateDuplicatedTimetabledPassingTimeId.validate(
        xpathRuleValidationContext
      );
    Assertions.assertNotNull(validationIssues);
    Assertions.assertEquals(
      accepted,
      validationIssues.isEmpty(),
      expectedMessage
    );
  }
}

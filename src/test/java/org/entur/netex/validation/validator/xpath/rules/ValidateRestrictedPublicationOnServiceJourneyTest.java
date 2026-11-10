package org.entur.netex.validation.validator.xpath.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.test.xpath.support.XPathTestSupport;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ValidateRestrictedPublicationOnServiceJourneyTest {

  private static final String NETEX_FRAGMENT =
    """
    <frames xmlns="http://www.netex.org.uk/netex">
      <ServiceFrame version="1" id="TST:ServiceFrame:1">
        <lines>
          <Line version="1" id="TST:Line:1">
            <TransportMode>bus</TransportMode>
            ${LINE_TRANSPORT_SUBMODE}
          </Line>
        </lines>
      </ServiceFrame>
      <TimetableFrame version="1" id="TST:TimetableFrame:1">
        <vehicleJourneys>
          <ServiceJourney version="1" id="TST:ServiceJourney:1" ${PUBLICATION}>
            ${SERVICE_JOURNEY_TRANSPORT_MODE}
          </ServiceJourney>
        </vehicleJourneys>
      </TimetableFrame>
    </frames>
    """;

  @ParameterizedTest(
    name = "publication={0}, line submode={1}, service journey submode={2} -> {3} issue(s)"
  )
  @CsvSource(
    {
      // no restriction
      ",schoolBus,,0",
      ",localBus,,0",
      "public,localBus,,0",
      "public,schoolAndPublicServiceBus,,0",
      // restricted, submode inherited from the Line
      "restricted,schoolBus,,0",
      "restricted,localBus,,1",
      "restricted,schoolAndPublicServiceBus,,1",
      "restricted,,,1",
      // restricted, submode overridden on the ServiceJourney
      "restricted,localBus,schoolBus,0",
      "restricted,schoolBus,localBus,1",
      "restricted,schoolBus,schoolAndPublicServiceBus,1",
    }
  )
  void testRestrictedPublication(
    String publication,
    String lineSubmode,
    String serviceJourneySubmode,
    int expectedIssues
  ) {
    XPathRuleValidationContext context = createContext(
      publication,
      lineSubmode,
      serviceJourneySubmode
    );

    ValidateRestrictedPublicationOnServiceJourney rule =
      new ValidateRestrictedPublicationOnServiceJourney();
    List<ValidationIssue> validationIssues = rule.validate(context);

    assertEquals(expectedIssues, validationIssues.size());
    assertTrue(validationIssues.stream().allMatch(issue -> issue.rule() == rule.rule()));
  }

  private static XPathRuleValidationContext createContext(
    String publication,
    String lineSubmode,
    String serviceJourneySubmode
  ) {
    String fragment = NETEX_FRAGMENT
      .replace(
        "${PUBLICATION}",
        publication == null ? "" : "publication=\"" + publication + "\""
      )
      .replace("${LINE_TRANSPORT_SUBMODE}", busSubmode(lineSubmode))
      .replace(
        "${SERVICE_JOURNEY_TRANSPORT_MODE}",
        serviceJourneySubmode == null
          ? ""
          : "<TransportMode>bus</TransportMode>" + busSubmode(serviceJourneySubmode)
      );
    XdmNode root = XPathTestSupport.parseDocument(fragment);
    XdmNode timetableFrame = root.children("TimetableFrame").iterator().next();
    return TestValidationContextBuilder.ofDocument(timetableFrame).build();
  }

  private static String busSubmode(String submode) {
    return submode == null
      ? ""
      : "<TransportSubmode><BusSubmode>" + submode + "</BusSubmode></TransportSubmode>";
  }
}

package org.entur.netex.validation.validator.schema;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.entur.netex.validation.validator.ValidationIssue;
import org.junit.jupiter.api.Test;

class NetexSchemaValidatorTest {

  private static final int MAX_VALIDATION_REPORT_ENTRIES = 2;
  private static final String TEST_CODESPACE = "ENT";
  private static final String TEST_FILENAME = "netex.xml";

  private static final String NETEX_FRAGMENT_VALID =
    """
        <PublicationDelivery xmlns="http://www.netex.org.uk/netex" xmlns:ns2="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri" version="1.15:NO-NeTEx-networktimetable:1.5">
          <PublicationTimestamp>2021-10-07T13:40:22.872</PublicationTimestamp>
          <ParticipantRef>RB</ParticipantRef>
        </PublicationDelivery>
        """;

  private static final String NETEX_FRAGMENT_INVALID =
    """
        <PublicationDelivery xmlns="http://www.netex.org.uk/netex" xmlns:ns2="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri" version="1.15:NO-NeTEx-networktimetable:1.5">
          <PublicationTimestamp>2021-10-07T13:40:22.872</PublicationTimestamp>
        </PublicationDelivery>
        """;

  private static final String NETEX_FRAGMENT_INVALID_TOO_MANY_ERRORS =
    """
        <PublicationDelivery xmlns="http://www.netex.org.uk/netex" xmlns:ns2="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri" version="1.15:NO-NeTEx-networktimetable:1.5">
            <dataObjects>
                <CompositeFrame created="2024-11-28T02:58:39.64" version="1" >
                   <validityConditions/>
                </CompositeFrame>
            </dataObjects>
        </PublicationDelivery>
        """;

  @Test
  void validateValidDocument() {
    List<ValidationIssue> validationIssues = validate(NETEX_FRAGMENT_VALID);
    assertTrue(validationIssues.isEmpty());
  }

  @Test
  void validateInvalidDocument() {
    List<ValidationIssue> validationIssues = validate(NETEX_FRAGMENT_INVALID);
    assertEquals(1, validationIssues.size());
  }

  @Test
  void validateInvalidDocumentTooManyErrors() {
    List<ValidationIssue> validationIssues = validate(
      NETEX_FRAGMENT_INVALID_TOO_MANY_ERRORS
    );
    assertEquals(MAX_VALIDATION_REPORT_ENTRIES, validationIssues.size());
  }

  @Test
  void validateMalformedDocument() {
    List<ValidationIssue> validationIssues = validate("x");
    assertEquals(1, validationIssues.size());
  }

  private static List<ValidationIssue> validate(String netexFragment) {
    NetexSchemaValidator validator = new NetexSchemaValidator(
      MAX_VALIDATION_REPORT_ENTRIES
    );
    NetexSchemaValidationContext validationContext = new NetexSchemaValidationContext(
      TEST_FILENAME,
      TEST_CODESPACE,
      netexFragment.getBytes(StandardCharsets.UTF_8)
    );
    return validator.validate(validationContext);
  }
}

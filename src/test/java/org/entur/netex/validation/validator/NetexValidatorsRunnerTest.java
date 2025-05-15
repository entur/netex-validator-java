package org.entur.netex.validation.validator;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.entur.netex.validation.validator.id.VersionOnLocalNetexIdValidator;
import org.entur.netex.validation.validator.schema.NetexSchemaValidationContext;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.Test;

class NetexValidatorsRunnerTest {

  private static final String TEST_CODESPACE = "ENT";
  private static final String TEST_FILENAME = "netex.xml";
  private static final String TEST_VALIDATION_REPORT_ID =
    "validation report id";

  private static final String NETEX_FRAGMENT =
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

  @Test
  void testNoValidator() {
    NetexValidatorsRunner runner = NetexValidatorsRunner.of().build();
    ValidationReport report = validationReport(runner);
    assertTrue(report.getValidationReportEntries().isEmpty());
    assertEquals(TEST_CODESPACE, report.getCodespace());
    assertEquals(TEST_VALIDATION_REPORT_ID, report.getValidationReportId());
  }

  @Test
  void testReportSchemaValidationError() {
    NetexValidatorsRunner runner = NetexValidatorsRunner
      .of()
      .withNetexSchemaValidator(
        new TestNetexSchemaValidator(
          List.of(
            new ValidationIssue(
              NetexSchemaValidator.RULE_ERROR,
              DataLocation.EMPTY_LOCATION,
              "an error"
            )
          )
        )
      )
      .build();
    ValidationReport report = validationReport(runner);

    assertEquals(1, report.getValidationReportEntries().size());
  }

  @Test
  void testSkipXPathValidationOnSchemaError() {
    NetexValidatorsRunner runner = NetexValidatorsRunner
      .of()
      .withNetexSchemaValidator(
        new TestNetexSchemaValidator(
          List.of(
            new ValidationIssue(
              NetexSchemaValidator.RULE_ERROR,
              DataLocation.EMPTY_LOCATION,
              "an error"
            )
          )
        )
      )
      .withNetexXMLParser(new NetexXMLParser())
      .withXPathValidators(
        List.of(
          new XPathValidator() {
            @Override
            public List<ValidationIssue> validate(
              XPathValidationContext validationContext
            ) {
              fail(
                "XPath validator should be skipped when Netex schema validation fails"
              );
              return List.of();
            }

            @Override
            public Set<ValidationRule> getRules() {
              return Set.of();
            }
          }
        )
      )
      .build();
    ValidationReport report = validationReport(runner);

    assertEquals(1, report.getValidationReportEntries().size());
  }

  @Test
  void testRunXPathValidationIfNoSchemaError() {
    AtomicBoolean xpathValidation = new AtomicBoolean();
    NetexValidatorsRunner runner = NetexValidatorsRunner
      .of()
      .withNetexSchemaValidator(
        new NetexSchemaValidator(0) {
          @Override
          public List<ValidationIssue> validate(
            NetexSchemaValidationContext validationContext
          ) {
            return List.of();
          }
        }
      )
      .withNetexXMLParser(new NetexXMLParser())
      .withXPathValidators(
        List.of(
          new XPathValidator() {
            @Override
            public List<ValidationIssue> validate(
              XPathValidationContext validationContext
            ) {
              xpathValidation.set(true);
              return List.of();
            }

            @Override
            public Set<ValidationRule> getRules() {
              return Set.of();
            }
          }
        )
      )
      .build();
    ValidationReport report = validationReport(runner);

    assertEquals(0, report.getValidationReportEntries().size());
    assertTrue(xpathValidation.get());
  }

  @Test
  void testReportValidationError() {
    NetexValidatorsRunner runner = NetexValidatorsRunner
      .of()
      .withNetexXMLParser(new NetexXMLParser())
      .withXPathValidators(List.of(new FailingXPathValidator()))
      .build();
    TestValidationProgressCallBack callback =
      new TestValidationProgressCallBack();
    runner.validate(
      TEST_CODESPACE,
      TEST_VALIDATION_REPORT_ID,
      TEST_FILENAME,
      NETEX_FRAGMENT.getBytes(StandardCharsets.UTF_8),
      false,
      false,
      callback
    );

    assertNotNull(callback.event);
    assertEquals(
      TEST_VALIDATION_REPORT_ID,
      callback.event.validationReportId()
    );
    assertTrue(callback.event.hasError());
  }

  @Test
  void testDescriptions() {
    XPathValidator xPathValidator = new VersionOnLocalNetexIdValidator();
    NetexValidatorsRunner runner = NetexValidatorsRunner
      .of()
      .withNetexXMLParser(new NetexXMLParser())
      .withXPathValidators(List.of(xPathValidator))
      .build();
    Set<String> ruleDescriptions = runner.getRuleDescriptions();
    assertNotNull(ruleDescriptions);
    Set<String> xpathRuleDescriptions = xPathValidator
      .getRules()
      .stream()
      .map(ValidationRule::name)
      .collect(Collectors.toUnmodifiableSet());
    assertEquals(xpathRuleDescriptions, ruleDescriptions);

    assertEquals(1, runner.getRuleDescriptionByCode().size());
    assertEquals(
      VersionOnLocalNetexIdValidator.RULE.name(),
      runner
        .getRuleDescriptionByCode()
        .get(VersionOnLocalNetexIdValidator.RULE.code())
    );
  }

  private static ValidationReport validationReport(
    NetexValidatorsRunner runner
  ) {
    return runner.validate(
      TEST_CODESPACE,
      TEST_VALIDATION_REPORT_ID,
      TEST_FILENAME,
      NETEX_FRAGMENT.getBytes(StandardCharsets.UTF_8)
    );
  }

  private static class TestNetexSchemaValidator extends NetexSchemaValidator {

    private final List<ValidationIssue> issues;

    public TestNetexSchemaValidator(List<ValidationIssue> issues) {
      super(0);
      this.issues = issues;
    }

    @Override
    public List<ValidationIssue> validate(
      NetexSchemaValidationContext validationContext
    ) {
      return issues;
    }
  }

  private static class FailingXPathValidator implements XPathValidator {

    @Override
    public List<ValidationIssue> validate(
      XPathValidationContext validationContext
    ) {
      return List.of(
        new ValidationIssue(
          new ValidationRule("code", "name", Severity.ERROR),
          DataLocation.EMPTY_LOCATION
        )
      );
    }

    @Override
    public Set<ValidationRule> getRules() {
      return Set.of();
    }
  }

  private static class TestValidationProgressCallBack
    implements NetexValidationProgressCallBack {

    ValidationCompleteEvent event;

    @Override
    public void notifyProgress(String aMessage) {}

    @Override
    public void notifyValidationComplete(ValidationCompleteEvent event) {
      this.event = event;
    }
  }
}

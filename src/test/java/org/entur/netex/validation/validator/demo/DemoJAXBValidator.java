package org.entur.netex.validation.validator.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.entur.netex.validation.validator.NetexValidatorsRunner;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.jaxb.JAXBValidationContext;
import org.entur.netex.validation.validator.jaxb.JAXBValidator;
import org.entur.netex.validation.validator.jaxb.SiteFrameStopPlaceRepository;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.rutebanken.netex.model.ServiceJourney;

/**
 * Minimal executable class that demonstrates how to run a validation with JAXB validators.
 */
public class DemoJAXBValidator {

  private static final ValidationRule RULE = new ValidationRule(
    "DEMO_2",
    "A service journey should have at least 2 passing times",
    Severity.ERROR
  );

  private static final String DEMO_FILE_NAME = "/demo/oym_line_105626_4176.xml";

  public static void main(String[] args) throws IOException {
    // create a NeTEx XML Parser
    NetexXMLParser netexXMLParser = new NetexXMLParser();
    // create a NeTEx schema validator, limit the number of findings to 100
    NetexSchemaValidator netexSchemaValidator = new NetexSchemaValidator(100);
    // create a NeTEx validators runner that registers the NeTEx schema validator and the custom NeTEx validator
    JAXBValidator validator = new CustomJaxbValidator();
    NetexValidatorsRunner netexValidatorsRunner = NetexValidatorsRunner
      .of()
      .withNetexXMLParser(netexXMLParser)
      .withNetexSchemaValidator(netexSchemaValidator)
      .withStopPlaceRepository(SiteFrameStopPlaceRepository::new)
      .withJaxbValidators(List.of(validator))
      .build();
    // run the validation for a given codespace, report id, NeTEx filename and file binary content
    String codespace = "ENT";
    String reportId = "XXX";
    byte[] content =
      DemoJAXBValidator.class.getResourceAsStream(DEMO_FILE_NAME).readAllBytes();
    ValidationReport validationReport = netexValidatorsRunner.validate(
      codespace,
      reportId,
      DEMO_FILE_NAME,
      content
    );
    validationReport
      .getValidationReportEntries()
      .stream()
      .map(ValidationReportEntry::getMessage)
      .forEach(System.out::println);
  }

  private static class CustomJaxbValidator implements JAXBValidator {

    @Override
    public List<ValidationIssue> validate(JAXBValidationContext validationContext) {
      List<ValidationIssue> issues = new ArrayList<>();
      for (ServiceJourney serviceJourney : validationContext.serviceJourneys()) {
        if (serviceJourney.getPassingTimes().getTimetabledPassingTime().size() < 2) {
          issues.add(
            new ValidationIssue(
              RULE,
              validationContext.dataLocation(serviceJourney.getId())
            )
          );
        }
      }

      return issues;
    }

    @Override
    public Set<ValidationRule> getRules() {
      return Set.of(RULE);
    }
  }
}

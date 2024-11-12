package org.entur.netex.validation.validator.demo;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmValue;
import org.entur.netex.validation.validator.*;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.entur.netex.validation.xml.NetexXMLParser;

/**
 * Minimal executable class that demonstrates how to run a validation.
 */
public class Demo {

  public static final String DEMO_FILE_NAME = "/demo/_FLB_shared_data.xml";

  public static void main(String[] args) throws IOException {
    // create a NeTEx XML Parser that ignores SiteFrame elements
    NetexXMLParser netexXMLParser = new NetexXMLParser(Set.of("SiteFrame"));
    // create a NeTEx schema validator, limit the number of findings to 100
    NetexSchemaValidator netexSchemaValidator = new NetexSchemaValidator(100);
    // create a custom NeTex validator
    XPathValidator xPathValidator = new CustomNetexValidator();
    // create a NeTEx validators runner that registers the NeTEx schema validator and the custom NeTEx validator
    NetexValidatorsRunner netexValidatorsRunner = NetexValidatorsRunner
      .of()
      .withNetexXMLParser(netexXMLParser)
      .withNetexSchemaValidator(netexSchemaValidator)
      .withXPathValidators(List.of(xPathValidator))
      .build();
    // run the validation for a given codespace, report id, NeTEx filename and file binary content
    String codespace = "ENT";
    String reportId = "XXX";
    byte[] content =
      Demo.class.getResourceAsStream(DEMO_FILE_NAME).readAllBytes();
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

  /**
   * Validate that all URLs start with https://
   */
  private static class CustomNetexValidator implements XPathValidator {

    @Override
    public void validate(
      ValidationReport validationReport,
      XPathValidationContext validationContext
    ) {
      XPathSelector selector;
      try {
        selector =
          validationContext
            .getNetexXMLParser()
            .getXPathCompiler()
            .compile("//Url[not(starts-with(text(),'https://'))]")
            .load();
        selector.setContextItem(validationContext.getXmlNode());
        XdmValue result = selector.evaluate();
        if (!result.isEmpty()) {
          validationReport.addValidationReportEntry(
            new ValidationReportEntry(
              "URL should start with 'https://",
              "DEMO_RULE_1",
              ValidationReportEntrySeverity.WARNING
            )
          );
        }
      } catch (SaxonApiException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public Set<String> getRuleDescriptions() {
      return Set.of("URL should start with 'https://'");
    }
  }
}
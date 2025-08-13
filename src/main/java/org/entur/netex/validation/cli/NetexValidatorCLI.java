package org.entur.netex.validation.cli;

import static java.io.OutputStream.nullOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.entur.netex.validation.configuration.DefaultValidationConfigLoader;
import org.entur.netex.validation.validator.*;
import org.entur.netex.validation.validator.id.DefaultNetexIdRepository;
import org.entur.netex.validation.validator.id.NetexIdUniquenessValidator;
import org.entur.netex.validation.validator.id.NetexReferenceValidator;
import org.entur.netex.validation.validator.id.ReferenceToValidEntityTypeValidator;
import org.entur.netex.validation.validator.id.VersionOnLocalNetexIdValidator;
import org.entur.netex.validation.validator.id.VersionOnRefToLocalNetexIdValidator;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.validator.xpath.XPathRuleValidator;
import org.entur.netex.validation.validator.xpath.tree.PublicationDeliveryValidationTreeFactory;
import org.entur.netex.validation.xml.NetexXMLParser;

/**
 * Command-line interface for NeTEx validation.
 * Usage: java -jar netex-validator-java.jar [-d] [-h] <netex-file>
 */
public class NetexValidatorCLI {

  public static void main(String[] args) {
    boolean debug = false;
    String filePath = null;

    for (String arg : args) {
      if ("--debug".equals(arg) || "-d".equals(arg)) {
        debug = true;
      } else if ("--help".equals(arg) || "-h".equals(arg)) {
        help();
      } else if (filePath == null && !arg.startsWith("-")) {
        filePath = arg;
      }
    }

    if (filePath == null) {
      help();
    }

    File file = new File(filePath);

    if (!file.exists()) {
      System.err.println("File not found: " + filePath);
      help();
    }

    try {
      byte[] content = Files.readAllBytes(file.toPath());
      NetexValidatorsRunner validator = createValidator();

      if (debug) {
        ValidationReport report = validate(validator, file, content);
        printReport(report);
      } else {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        System.setOut(new PrintStream(nullOutputStream()));
        System.setErr(new PrintStream(nullOutputStream()));

        ValidationReport report = validate(validator, file, content);

        System.setOut(originalOut);
        System.setErr(originalErr);

        printReport(report);
      }
    } catch (Exception e) {
      System.err.println("Error reading file: " + e.getMessage());
      System.exit(1);
    }
  }

  private static ValidationReport validate(
    NetexValidatorsRunner validator,
    File file,
    byte[] content
  ) {
    return validator.validate("CLI", "validation", file.getName(), content);
  }

  private static void help() {
    System.out.println("Usage: java NetexValidatorCLI [--debug|-d] <netex-file> ");
    System.exit(1);
  }

  private static void printReport(ValidationReport report) {
    List<ValidationReportEntry> entries = new ArrayList<>(
      report.getValidationReportEntries()
    );

    if (entries.isEmpty()) {
      System.out.println("✅ No validation issues found");
    } else {
      for (ValidationReportEntry entry : entries) {
        var msg = entry.getSeverity() + ": " + entry.getMessage();
        if (entry.getLineNumber() != null) {
          msg += " (line " + entry.getLineNumber() + ")";
        }
        System.out.println(msg);
      }
      System.out.println("⛔️ Found " + entries.size() + " validation issues");
      System.exit(1);
    }
  }

  private static NetexValidatorsRunner createValidator() {
    DefaultValidationConfigLoader configLoader = new DefaultValidationConfigLoader();
    DefaultValidationEntryFactory entryFactory = new DefaultValidationEntryFactory(
      configLoader
    );

    List<XPathValidator> validators = new ArrayList<>();

    validators.add(new NetexIdUniquenessValidator(new DefaultNetexIdRepository()));
    validators.add(new VersionOnLocalNetexIdValidator());
    validators.add(new VersionOnRefToLocalNetexIdValidator());
    validators.add(
      new NetexReferenceValidator(new DefaultNetexIdRepository(), new ArrayList<>())
    );
    validators.add(new ReferenceToValidEntityTypeValidator());
    validators.add(
      new XPathRuleValidator(new PublicationDeliveryValidationTreeFactory())
    );

    return NetexValidatorsRunner
      .of()
      .withNetexSchemaValidator(new NetexSchemaValidator(20))
      .withNetexXMLParser(new NetexXMLParser())
      .withXPathValidators(validators)
      .withValidationReportEntryFactory(entryFactory)
      .build();
  }
}

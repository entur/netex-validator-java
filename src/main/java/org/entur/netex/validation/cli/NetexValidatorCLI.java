package org.entur.netex.validation.cli;

import static java.io.OutputStream.nullOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.entur.netex.validation.configuration.DefaultValidationConfigLoader;
import org.entur.netex.validation.validator.*;
import org.entur.netex.validation.validator.id.*;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.validator.xpath.XPathRuleValidator;
import org.entur.netex.validation.validator.xpath.tree.PublicationDeliveryValidationTreeFactory;
import org.entur.netex.validation.xml.NetexXMLParser;

/**
 * Command-line interface for NeTEx validation.
 */
public class NetexValidatorCLI {

  private boolean debug;
  private boolean verbose;

  public static void main(String[] args) {
    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(args);
  }

  public void run(String[] args) {
    List<String> filePaths = new ArrayList<>();

    for (String arg : args) {
      if ("-d".equals(arg)) {
        debug = true;
      } else if ("-v".equals(arg)) {
        verbose = true;
      } else if ("-h".equals(arg)) {
        help();
      } else if (!arg.startsWith("-")) {
        filePaths.add(arg);
      }
    }

    if (filePaths.isEmpty()) {
      help();
    }

    try {
      List<FileSupplier> fileSuppliers = new ArrayList<>();
      for (String filePath : filePaths) {
        File file = new File(filePath);
        if (!file.exists()) {
          System.err.printf("File not found: %s%n", filePath);
          System.exit(1);
        } else if (isZipFile(file)) {
          fileSuppliers.addAll(processZipFile(file));
        } else {
          fileSuppliers.add(processFile(file));
        }
      }
      processFileEntries(fileSuppliers);
    } catch (Exception e) {
      System.err.printf("Error processing file: %s%n", e.getMessage());
      System.exit(1);
    }
  }

  private void help() {
    System.out.println(
      """
Usage: ./validate-netex.sh [-d] [-v] <file1> [file2] [file3] ...

Supports NeTEx files and ZIP archives containing multiple NeTEx files.
All files are validated sequentially in the same session.

Options:
  -d    Enable debug output
  -v    Show detailed validation issues instead of summary
  -h    Show this help message"""
    );
    System.exit(1);
  }

  NetexValidatorsRunner createValidator() {
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

  private boolean isZipFile(File file) {
    return file.getName().toLowerCase().endsWith(".zip");
  }

  private List<FileSupplier> processZipFile(File zipFile) throws IOException {
    ZipFileSupplier zipSupplier = new ZipFileSupplier(zipFile);

    if (zipSupplier.isEmpty()) {
      System.out.println("⚠️  No XML files found in ZIP archive");
      return new ArrayList<>();
    }

    return zipSupplier.createFileSuppliers().toList();
  }

  private FileSupplier processFile(File file) {
    return () -> new FileEntry(file.getName(), Files.readAllBytes(file.toPath()));
  }

  private void processFileEntries(List<FileSupplier> fileSuppliers) {
    if (fileSuppliers.isEmpty()) {
      System.err.println("No valid files to process.");
      return;
    }
    NetexValidatorsRunner validator = createValidator();
    List<ValidationReport> allReports = new ArrayList<>();
    String codespace = "CLI";
    String validationReportId = "cli-validation";

    fileSuppliers.forEach(fileSupplier -> {
      try {
        FileEntry fileEntry = fileSupplier.get();
        String fileName = fileEntry.fileName();
        byte[] content = fileEntry.content();

        ValidationReport report;
        if (debug) {
          report = validator.validate(codespace, validationReportId, fileName, content);
        } else {
          PrintStream originalOut = System.out;
          PrintStream originalErr = System.err;
          System.setOut(new PrintStream(nullOutputStream()));
          System.setErr(new PrintStream(nullOutputStream()));

          report = validator.validate(codespace, validationReportId, fileName, content);

          System.setOut(originalOut);
          System.setErr(originalErr);
        }
        allReports.add(report);

        printValidationResult(fileName, report);
      } catch (IOException e) {
        System.err.printf("Error reading file: %s%n", e.getMessage());
      } catch (Exception e) {
        System.err.printf("Error processing file: %s%n", e.getMessage());
      }
    });

    if (fileSuppliers.size() > 1) {
      long totalIssues = allReports
        .stream()
        .flatMap(report -> report.getNumberOfValidationEntriesPerRule().values().stream())
        .mapToLong(Long::longValue)
        .sum();

      System.out.printf(
        """

          📊 Dataset Validation Complete:
          Files processed: %d
          Total issues across dataset: %d
          """,
        fileSuppliers.size(),
        totalIssues
      );

      if (!verbose && totalIssues > 0) {
        System.out.println("Use -v for detailed information");
      }
    }
  }

  private void printValidationResult(String fileName, ValidationReport report) {
    var issuesPerRule = report.getNumberOfValidationEntriesPerRule();

    if (issuesPerRule.isEmpty()) {
      System.out.printf("  ✅ %s%n", fileName);
    } else {
      long totalIssues = issuesPerRule.values().stream().mapToLong(Long::longValue).sum();

      System.out.printf("  ❌ %s (%d issue(s))%n", fileName, totalIssues);

      if (verbose) {
        List<ValidationReportEntry> entries = new ArrayList<>(
          report.getValidationReportEntries()
        );
        for (ValidationReportEntry entry : entries) {
          if (entry.getLineNumber() != null) {
            System.out.printf(
              "      %s: %s (line %d)%n",
              entry.getSeverity(),
              entry.getMessage(),
              entry.getLineNumber()
            );
          } else {
            System.out.printf("      %s: %s%n", entry.getSeverity(), entry.getMessage());
          }
        }
      } else {
        for (var entry : issuesPerRule.entrySet()) {
          String ruleName = entry.getKey();
          Long count = entry.getValue();
          System.out.printf("      %s: %d issue(s)%n", ruleName, count);
        }
      }
    }
  }
}

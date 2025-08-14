package org.entur.netex.validation.cli;

import static java.io.OutputStream.nullOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.entur.netex.validation.configuration.DefaultValidationConfigLoader;
import org.entur.netex.validation.validator.*;
import org.entur.netex.validation.validator.id.*;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.validator.xpath.XPathRuleValidator;
import org.entur.netex.validation.validator.xpath.tree.PublicationDeliveryValidationTreeFactory;
import org.entur.netex.validation.xml.NetexXMLParser;

/**
 * Command-line interface for NeTEx validation.
 * Usage: java -jar netex-validator-java.jar [-d] [-h] <netex-file>
 */
public class NetexValidatorCLI {

  private static boolean debug;
  private static boolean verbose;
  private static NetexValidatorsRunner validator;

  public static void main(String[] args) {
    List<String> filePaths = new ArrayList<>();

    // Parse arguments
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
      validator = createValidator();

      if (filePaths.size() == 1) {
        // Single file processing (could be XML or ZIP)
        File file = new File(filePaths.get(0));
        if (!file.exists()) {
          System.err.println("File not found: " + filePaths.get(0));
          help();
        }

        if (isZipFile(file)) {
          processZipFile(file);
        } else {
          processFile(file);
        }
      } else {
        processFiles(filePaths);
      }
    } catch (Exception e) {
      System.err.println("Error processing file: " + e.getMessage());
      System.exit(1);
    }
  }

  private static ValidationReport validate(File file, byte[] content) {
    return validator.validate("CLI", "validation", file.getName(), content);
  }

  private static void help() {
    System.out.println(
      "Usage: java NetexValidatorCLI [-d] [-v] <file1> [file2] [file3] ..."
    );
    System.out.println("Supports:");
    System.out.println("  - Single NeTEx XML file");
    System.out.println("  - ZIP archive containing multiple NeTEx files");
    System.out.println("  - Multiple NeTEx XML files (space separated)");
    System.out.println("Options:");
    System.out.println("  -d    Enable debug output");
    System.out.println("  -v    Show detailed validation issues instead of summary");
    System.out.println("  -h    Show this help message");
    System.exit(1);
  }

  private static void printReport(ValidationReport report) {
    var entriesPerRule = report.getNumberOfValidationEntriesPerRule();

    if (entriesPerRule.isEmpty()) {
      System.out.println("✅ No validation issues found");
      return;
    }

    if (verbose) {
      List<ValidationReportEntry> entries = new ArrayList<>(
        report.getValidationReportEntries()
      );

      for (ValidationReportEntry entry : entries) {
        var msg = entry.getSeverity() + ": " + entry.getMessage();
        if (entry.getLineNumber() != null) {
          msg += " (line " + entry.getLineNumber() + ")";
        }
        System.out.println(msg);
      }
      System.out.println("⛔️ Found " + entries.size() + " validation issue(s)");
    } else {
      long totalIssues = 0;

      for (var entry : entriesPerRule.entrySet()) {
        String ruleName = entry.getKey();
        Long count = entry.getValue();
        totalIssues += count;
        System.out.println(
          "  " + ruleName + ": " + count + " issue" + (count == 1 ? "" : "s")
        );
      }
      System.out.println(
        "⛔️ Found " + totalIssues + " validation issue(s). Use -v for details."
      );
    }

    System.exit(1);
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

  private static boolean isZipFile(File file) {
    String fileName = file.getName().toLowerCase();
    return fileName.endsWith(".zip");
  }

  private static final Comparator<String> SHARED_DATA_FIRST_COMPARATOR = (a, b) -> {
    String nameA = a.toLowerCase();
    String nameB = b.toLowerCase();

    if (nameA.startsWith("_") && !nameB.startsWith("_")) return -1;
    if (!nameA.startsWith("_") && nameB.startsWith("_")) return 1;
    return nameA.compareTo(nameB);
  };

  private static void processZipFile(File zipFile) throws IOException {
    System.out.println("Processing ZIP file as dataset: " + zipFile.getName());

    List<FileEntry> xmlFiles = new ArrayList<>();

    try (ZipInputStream zipStream = new ZipInputStream(new FileInputStream(zipFile))) {
      ZipEntry entry;

      while ((entry = zipStream.getNextEntry()) != null) {
        if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".xml")) {
          String fileName = entry.getName();

          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          byte[] buffer = new byte[4096];
          int length;
          while ((length = zipStream.read(buffer)) > 0) {
            baos.write(buffer, 0, length);
          }
          byte[] content = baos.toByteArray();

          xmlFiles.add(new FileEntry(fileName, content));
        }
      }
    }

    if (xmlFiles.isEmpty()) {
      System.out.println("⚠️  No XML files found in ZIP archive");
      return;
    }

    xmlFiles.sort(Comparator.comparing(fe -> fe.fileName, SHARED_DATA_FIRST_COMPARATOR));
    processFileEntries(xmlFiles, "zip-dataset-validation");
  }

  private static void processFile(File file) throws IOException {
    System.out.println("Processing single file: " + file.getName());

    byte[] content = Files.readAllBytes(file.toPath());
    List<FileEntry> fileEntries = List.of(new FileEntry(file.getName(), content));

    processFileEntries(fileEntries, "single-file-validation");
  }

  private static void processFiles(List<String> filePaths) {
    System.out.println(
      "Processing multiple files as dataset: " + filePaths.size() + " files"
    );

    List<String> sortedFilePaths = new ArrayList<>(filePaths);
    sortedFilePaths.sort(
      Comparator.comparing(path -> new File(path).getName(), SHARED_DATA_FIRST_COMPARATOR)
    );

    List<FileEntry> fileEntries = new ArrayList<>();
    for (String filePath : sortedFilePaths) {
      File file = new File(filePath);
      if (!file.exists()) {
        System.err.println("File not found: " + filePath);
        continue;
      }

      try {
        byte[] content = Files.readAllBytes(file.toPath());
        fileEntries.add(new FileEntry(file.getName(), content));
      } catch (IOException e) {
        System.err.println("Error reading file " + filePath + ": " + e.getMessage());
      }
    }

    if (!fileEntries.isEmpty()) {
      processFileEntries(fileEntries, "multiple-files-validation");
    }
  }

  private static void processFileEntries(
    List<FileEntry> fileEntries,
    String validationReportId
  ) {
    List<ValidationReport> allReports = new ArrayList<>();
    String codespace = "CLI";
    boolean hasAnyIssues = false;

    for (FileEntry fileEntry : fileEntries) {
      String fileName = fileEntry.fileName;
      byte[] content = fileEntry.content;

      System.out.println("  Processing: " + fileName);

      try {
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

        if (!report.getNumberOfValidationEntriesPerRule().isEmpty()) {
          hasAnyIssues = true;
        }

        printFileResult(fileName, report);
      } catch (Exception e) {
        System.err.println("Error processing file " + fileName + ": " + e.getMessage());
        hasAnyIssues = true;
      }
    }

    if (fileEntries.size() == 1) {
      if (!allReports.isEmpty()) {
        ValidationReport report = allReports.get(0);
        if (report.getNumberOfValidationEntriesPerRule().isEmpty()) {
          System.out.println("✅ No validation issues found");
          return;
        } else {
          printReport(report);
        }
      }
    } else {
      long totalIssues = allReports
        .stream()
        .flatMap(report -> report.getNumberOfValidationEntriesPerRule().values().stream())
        .mapToLong(Long::longValue)
        .sum();

      System.out.println("\n📊 Dataset Validation Complete:");
      System.out.println("Files processed: " + fileEntries.size());
      System.out.println("Total issues across dataset: " + totalIssues);
      System.out.println("Cross-file references validated as unified dataset");

      if (!verbose && totalIssues > 0) {
        System.out.println("Use -v for detailed information");
      }

      if (hasAnyIssues) {
        System.exit(1);
      } else {
        System.out.println("🎉 All files in dataset validated successfully!");
        return;
      }
    }

    if (hasAnyIssues) {
      System.exit(1);
    }
  }

  private static void printFileResult(String fileName, ValidationReport report) {
    var entriesPerRule = report.getNumberOfValidationEntriesPerRule();

    if (entriesPerRule.isEmpty()) {
      System.out.println("  ✅ " + fileName + " (no issues)");
    } else {
      long fileIssues = entriesPerRule.values().stream().mapToLong(Long::longValue).sum();
      System.out.println(
        "  ❌ " +
        fileName +
        " (" +
        fileIssues +
        " issue" +
        (fileIssues == 1 ? "" : "s") +
        ")"
      );

      if (verbose) {
        List<ValidationReportEntry> entries = new ArrayList<>(
          report.getValidationReportEntries()
        );
        for (ValidationReportEntry entry : entries) {
          var msg = "      " + entry.getSeverity() + ": " + entry.getMessage();
          if (entry.getLineNumber() != null) {
            msg += " (line " + entry.getLineNumber() + ")";
          }
          System.out.println(msg);
        }
      } else {
        for (var entry : entriesPerRule.entrySet()) {
          String ruleName = entry.getKey();
          Long count = entry.getValue();
          System.out.println(
            "      " + ruleName + ": " + count + " issue" + (count == 1 ? "" : "s")
          );
        }
      }
    }
  }

  private record FileEntry(String fileName, byte[] content) {}
}

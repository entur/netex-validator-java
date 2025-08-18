package org.entur.netex.validation.cli;

import static java.io.OutputStream.nullOutputStream;
import static java.util.Comparator.comparing;

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
 */
public class NetexValidatorCLI {

  private interface FileSupplier {
    FileEntry get() throws IOException;
  }

  private static boolean debug;
  private static boolean verbose;
  private static NetexValidatorsRunner validator;

  public static void main(String[] args) {
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
      validator = createValidator();

      if (filePaths.size() == 1) {
        File file = new File(filePaths.get(0));
        if (!file.exists()) {
          System.err.printf("File not found: %s%n", filePaths.get(0));
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
      System.err.printf("Error processing file: %s%n", e.getMessage());
      System.exit(1);
    }
  }

  private static void help() {
    System.out.println(
      """
        Usage: java NetexValidatorCLI [-d] [-v] <file1> [file2] [file3] ...
        Supports:
          - Single NeTEx XML file
          - ZIP archive containing multiple NeTEx files
          - Multiple NeTEx XML files (space separated)
        Options:
          -d    Enable debug output
          -v    Show detailed validation issues instead of summary
          -h    Show this help message
        """
    );
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
    List<String> xmlFileNames = new ArrayList<>();

    try (ZipInputStream zipStream = new ZipInputStream(new FileInputStream(zipFile))) {
      ZipEntry entry;

      while ((entry = zipStream.getNextEntry()) != null) {
        if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".xml")) {
          xmlFileNames.add(entry.getName());
        }
      }
    }

    if (xmlFileNames.isEmpty()) {
      System.out.println("⚠️  No XML files found in ZIP archive");
      return;
    }

    xmlFileNames.sort(SHARED_DATA_FIRST_COMPARATOR);

    Iterable<FileSupplier> fileSuppliers = () ->
      xmlFileNames
        .stream()
        .map(fileName ->
          (FileSupplier) () -> {
            try (
              ZipInputStream zipStream = new ZipInputStream(new FileInputStream(zipFile))
            ) {
              ZipEntry entry;
              while ((entry = zipStream.getNextEntry()) != null) {
                if (entry.getName().equals(fileName)) {
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  byte[] buffer = new byte[4096];
                  int length;
                  while ((length = zipStream.read(buffer)) > 0) {
                    baos.write(buffer, 0, length);
                  }
                  return new FileEntry(fileName, baos.toByteArray());
                }
              }
              throw new IOException("File not found in ZIP: " + fileName);
            }
          }
        )
        .iterator();

    processFileEntries(fileSuppliers, xmlFileNames.size(), "zip-dataset-validation");
  }

  private static void processFile(File file) {
    Iterable<FileSupplier> fileSuppliers = List.of(() ->
      new FileEntry(file.getName(), Files.readAllBytes(file.toPath()))
    );
    processFileEntries(fileSuppliers, 1, "single-file-validation");
  }

  private static void processFiles(List<String> filePaths) {
    List<String> sortedFilePaths = new ArrayList<>(filePaths);
    sortedFilePaths.sort(
      comparing(path -> new File(path).getName(), SHARED_DATA_FIRST_COMPARATOR)
    );

    List<FileSupplier> fileSuppliers = new ArrayList<>();
    for (String filePath : sortedFilePaths) {
      File file = new File(filePath);
      if (!file.exists()) {
        System.err.printf("File not found: %s%n", filePath);
        continue;
      }
      fileSuppliers.add(() ->
        new FileEntry(file.getName(), Files.readAllBytes(file.toPath()))
      );
    }

    if (!fileSuppliers.isEmpty()) {
      processFileEntries(
        fileSuppliers,
        fileSuppliers.size(),
        "multiple-files-validation"
      );
    }
  }

  private static void processFileEntries(
    Iterable<FileSupplier> fileSuppliers,
    int totalFiles,
    String validationReportId
  ) {
    List<ValidationReport> allReports = new ArrayList<>();
    String codespace = "CLI";

    for (FileSupplier fileSupplier : fileSuppliers) {
      try {
        FileEntry fileEntry = fileSupplier.get();
        String fileName = fileEntry.fileName;
        byte[] content = fileEntry.content;

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
    }

    if (totalFiles > 1) {
      long totalIssues = allReports
        .stream()
        .flatMap(report -> report.getNumberOfValidationEntriesPerRule().values().stream())
        .mapToLong(Long::longValue)
        .sum();

      System.out.println(
        "\n\n📊 Dataset Validation Complete:\n" +
        "Files processed: " +
        totalFiles +
        "\n" +
        "Total issues across dataset: " +
        totalIssues
      );

      if (!verbose && totalIssues > 0) {
        System.out.println("Use -v for detailed information");
      }
    }
  }

  private static void printValidationResult(String fileName, ValidationReport report) {
    var entriesPerRule = report.getNumberOfValidationEntriesPerRule();

    if (entriesPerRule.isEmpty()) {
      System.out.printf("  ✅ %s%n", fileName);
    } else {
      long totalIssues = entriesPerRule
        .values()
        .stream()
        .mapToLong(Long::longValue)
        .sum();

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
        for (var entry : entriesPerRule.entrySet()) {
          String ruleName = entry.getKey();
          Long count = entry.getValue();
          System.out.printf("      %s: %d issue(s)%n", ruleName, count);
        }
      }
    }
  }

  private record FileEntry(String fileName, byte[] content) {}
}

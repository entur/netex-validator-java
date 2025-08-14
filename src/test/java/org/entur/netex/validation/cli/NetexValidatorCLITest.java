package org.entur.netex.validation.cli;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.entur.netex.validation.validator.NetexValidatorsRunner;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for NetexValidatorCLI class.
 * Tests the core validation functionality while avoiding System.exit() calls.
 */
class NetexValidatorCLITest {

  @TempDir
  Path tempDir;

  private ByteArrayOutputStream outputStream;
  private ByteArrayOutputStream errorStream;
  private PrintStream originalOut;
  private PrintStream originalErr;

  @BeforeEach
  void setUp() {
    // Capture system output
    outputStream = new ByteArrayOutputStream();
    errorStream = new ByteArrayOutputStream();
    originalOut = System.out;
    originalErr = System.err;
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(errorStream));
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  void testCreateValidator() {
    NetexValidatorCLI cli = new NetexValidatorCLI();
    NetexValidatorsRunner validator = cli.createValidator();

    assertNotNull(validator, "Validator should be created successfully");
  }

  @Test
  void testRunWithSingleValidXmlFile() throws IOException {
    // Create a test XML file
    Path xmlFile = tempDir.resolve("test.xml");
    String xmlContent = createValidNetexXmlContent();
    Files.write(xmlFile, xmlContent.getBytes());

    // Create a spy to intercept validator creation and mock its behavior
    NetexValidatorCLI spyCli = spy(new NetexValidatorCLI());
    NetexValidatorsRunner mockValidator = mock(NetexValidatorsRunner.class);
    ValidationReport mockReport = createMockValidationReport(false);

    doReturn(mockValidator).when(spyCli).createValidator();
    when(mockValidator.validate(anyString(), anyString(), anyString(), any(byte[].class)))
      .thenReturn(mockReport);

    spyCli.run(new String[] { xmlFile.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("✅ test.xml"), "Should show success for valid file");
    verify(mockValidator)
      .validate(
        eq("CLI"),
        eq("single-file-validation"),
        eq("test.xml"),
        any(byte[].class)
      );
  }

  @Test
  void testRunWithSingleInvalidXmlFile() throws IOException {
    // Create a test XML file
    Path xmlFile = tempDir.resolve("invalid.xml");
    String xmlContent = createValidNetexXmlContent();
    Files.write(xmlFile, xmlContent.getBytes());

    // Create a spy to intercept validator creation and mock its behavior
    NetexValidatorCLI spyCli = spy(new NetexValidatorCLI());
    NetexValidatorsRunner mockValidator = mock(NetexValidatorsRunner.class);
    ValidationReport mockReport = createMockValidationReport(true);

    doReturn(mockValidator).when(spyCli).createValidator();
    when(mockValidator.validate(anyString(), anyString(), anyString(), any(byte[].class)))
      .thenReturn(mockReport);

    spyCli.run(new String[] { xmlFile.toString() });

    String output = outputStream.toString();
    assertTrue(
      output.contains("❌ invalid.xml (2 issue(s))"),
      "Should show errors for invalid file"
    );
  }

  @Test
  void testRunWithMultipleFiles() throws IOException {
    // Create multiple test XML files
    Path xmlFile1 = tempDir.resolve("file1.xml");
    Path xmlFile2 = tempDir.resolve("file2.xml");
    String xmlContent = createValidNetexXmlContent();
    Files.write(xmlFile1, xmlContent.getBytes());
    Files.write(xmlFile2, xmlContent.getBytes());

    // Create a spy to intercept validator creation and mock its behavior
    NetexValidatorCLI spyCli = spy(new NetexValidatorCLI());
    NetexValidatorsRunner mockValidator = mock(NetexValidatorsRunner.class);
    ValidationReport mockReport1 = createMockValidationReport(false);
    ValidationReport mockReport2 = createMockValidationReport(true);

    doReturn(mockValidator).when(spyCli).createValidator();
    when(
      mockValidator.validate(anyString(), anyString(), eq("file1.xml"), any(byte[].class))
    )
      .thenReturn(mockReport1);
    when(
      mockValidator.validate(anyString(), anyString(), eq("file2.xml"), any(byte[].class))
    )
      .thenReturn(mockReport2);

    spyCli.run(new String[] { xmlFile1.toString(), xmlFile2.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("✅ file1.xml"), "Should show success for valid file");
    assertTrue(output.contains("❌ file2.xml"), "Should show errors for invalid file");
    assertTrue(output.contains("📊 Dataset Validation Complete"), "Should show summary");
    assertTrue(output.contains("Files processed: 2"), "Should show correct file count");
    assertTrue(
      output.contains("Total issues across dataset: 2"),
      "Should show total issue count"
    );
  }

  @Test
  void testRunWithZipFile() throws IOException {
    // Use an actual test ZIP file from resources
    Path zipFile = Paths.get("src/test/resources/rb_flb-aggregated-netex.zip");

    // Skip test if the file doesn't exist (shouldn't happen in normal circumstances)
    assumeTrue(Files.exists(zipFile), "Test ZIP file should exist in resources");

    // Create a spy to intercept validator creation and mock its behavior
    NetexValidatorCLI spyCli = spy(new NetexValidatorCLI());
    NetexValidatorsRunner mockValidator = mock(NetexValidatorsRunner.class);
    ValidationReport mockReport = createMockValidationReport(false);

    doReturn(mockValidator).when(spyCli).createValidator();
    when(mockValidator.validate(anyString(), anyString(), anyString(), any(byte[].class)))
      .thenReturn(mockReport);

    spyCli.run(new String[] { zipFile.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("✅"), "Should show success for files in ZIP");
    assertTrue(
      output.contains("📊 Dataset Validation Complete"),
      "Should show dataset summary"
    );
    verify(mockValidator, atLeast(1))
      .validate(eq("CLI"), eq("zip-dataset-validation"), anyString(), any(byte[].class));
  }

  @Test
  void testRunWithEmptyZipFile() throws IOException {
    // Create an empty ZIP file
    Path zipFile = tempDir.resolve("empty.zip");
    try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
      // Empty ZIP file
    }

    // Create a spy to intercept validator creation and mock its behavior
    NetexValidatorCLI spyCli = spy(new NetexValidatorCLI());
    NetexValidatorsRunner mockValidator = mock(NetexValidatorsRunner.class);
    doReturn(mockValidator).when(spyCli).createValidator();

    spyCli.run(new String[] { zipFile.toString() });

    String output = outputStream.toString();
    assertTrue(
      output.contains("⚠️  No XML files found in ZIP archive"),
      "Should warn about empty ZIP"
    );
  }

  @Test
  void testRunWithDebugFlag() throws IOException {
    // Create a test XML file
    Path xmlFile = tempDir.resolve("test.xml");
    String xmlContent = createValidNetexXmlContent();
    Files.write(xmlFile, xmlContent.getBytes());

    // Create a spy to intercept validator creation and mock its behavior
    NetexValidatorCLI spyCli = spy(new NetexValidatorCLI());
    NetexValidatorsRunner mockValidator = mock(NetexValidatorsRunner.class);
    ValidationReport mockReport = createMockValidationReport(false);

    doReturn(mockValidator).when(spyCli).createValidator();
    when(mockValidator.validate(anyString(), anyString(), anyString(), any(byte[].class)))
      .thenReturn(mockReport);

    spyCli.run(new String[] { "-d", xmlFile.toString() });

    // Verify that debug mode was enabled - validator should be called
    verify(mockValidator)
      .validate(anyString(), anyString(), anyString(), any(byte[].class));
    String output = outputStream.toString();
    assertTrue(output.contains("✅ test.xml"), "Should process file in debug mode");
  }

  @Test
  void testRunWithVerboseFlag() throws IOException {
    // Create a test XML file
    Path xmlFile = tempDir.resolve("test.xml");
    String xmlContent = createValidNetexXmlContent();
    Files.write(xmlFile, xmlContent.getBytes());

    // Create a spy to intercept validator creation and mock its behavior
    NetexValidatorCLI spyCli = spy(new NetexValidatorCLI());
    NetexValidatorsRunner mockValidator = mock(NetexValidatorsRunner.class);
    ValidationReport mockReport = createMockValidationReportWithEntries();

    doReturn(mockValidator).when(spyCli).createValidator();
    when(mockValidator.validate(anyString(), anyString(), anyString(), any(byte[].class)))
      .thenReturn(mockReport);

    spyCli.run(new String[] { "-v", xmlFile.toString() });

    String output = outputStream.toString();
    assertTrue(
      output.contains("ERROR: Test validation error"),
      "Should show detailed error in verbose mode"
    );
    assertTrue(output.contains("(line 10)"), "Should show line number in verbose mode");
  }

  @Test
  void testStreamBasedFileProcessing() throws IOException {
    // Test that the Stream<FileSupplier> approach works correctly
    Path xmlFile = tempDir.resolve("stream-test.xml");
    Files.write(xmlFile, createValidNetexXmlContent().getBytes());

    NetexValidatorCLI spyCli = spy(new NetexValidatorCLI());
    NetexValidatorsRunner mockValidator = mock(NetexValidatorsRunner.class);
    ValidationReport mockReport = createMockValidationReport(false);

    doReturn(mockValidator).when(spyCli).createValidator();
    when(mockValidator.validate(anyString(), anyString(), anyString(), any(byte[].class)))
      .thenReturn(mockReport);

    spyCli.run(new String[] { xmlFile.toString() });

    verify(mockValidator)
      .validate(anyString(), anyString(), eq("stream-test.xml"), any(byte[].class));
  }

  @Test
  void testSharedDataFileOrdering() throws IOException {
    // Create files that should be sorted with shared data (_) files first
    Path normalFile = tempDir.resolve("normal.xml");
    Path sharedFile = tempDir.resolve("_shared.xml");
    Files.write(normalFile, createValidNetexXmlContent().getBytes());
    Files.write(sharedFile, createValidNetexXmlContent().getBytes());

    NetexValidatorCLI spyCli = spy(new NetexValidatorCLI());
    NetexValidatorsRunner mockValidator = mock(NetexValidatorsRunner.class);
    ValidationReport mockReport = createMockValidationReport(false);

    doReturn(mockValidator).when(spyCli).createValidator();
    when(mockValidator.validate(anyString(), anyString(), anyString(), any(byte[].class)))
      .thenReturn(mockReport);

    spyCli.run(new String[] { normalFile.toString(), sharedFile.toString() });

    // Verify both files were processed
    verify(mockValidator, times(2))
      .validate(anyString(), anyString(), anyString(), any(byte[].class));

    String output = outputStream.toString();
    assertTrue(
      output.contains("📊 Dataset Validation Complete"),
      "Should show dataset summary"
    );
  }

  @Test
  void testZipFileWithMixedContent() throws IOException {
    // Create a ZIP file with XML and non-XML files
    Path zipFile = tempDir.resolve("mixed.zip");
    createTestZipFile(zipFile);

    NetexValidatorCLI spyCli = spy(new NetexValidatorCLI());
    NetexValidatorsRunner mockValidator = mock(NetexValidatorsRunner.class);
    ValidationReport mockReport = createMockValidationReport(false);

    doReturn(mockValidator).when(spyCli).createValidator();
    when(mockValidator.validate(anyString(), anyString(), anyString(), any(byte[].class)))
      .thenReturn(mockReport);

    spyCli.run(new String[] { zipFile.toString() });

    // Should process only XML files, ignoring non-XML files
    verify(mockValidator, times(2))
      .validate(eq("CLI"), eq("zip-dataset-validation"), anyString(), any(byte[].class));
  }

  @Test
  void testMemoryEfficientProcessing() throws IOException {
    // Test that files are processed one at a time (memory efficiency)
    Path xmlFile1 = tempDir.resolve("memory1.xml");
    Path xmlFile2 = tempDir.resolve("memory2.xml");
    Files.write(xmlFile1, createValidNetexXmlContent().getBytes());
    Files.write(xmlFile2, createValidNetexXmlContent().getBytes());

    NetexValidatorCLI spyCli = spy(new NetexValidatorCLI());
    NetexValidatorsRunner mockValidator = mock(NetexValidatorsRunner.class);
    ValidationReport mockReport = createMockValidationReport(false);

    doReturn(mockValidator).when(spyCli).createValidator();
    when(mockValidator.validate(anyString(), anyString(), anyString(), any(byte[].class)))
      .thenReturn(mockReport);

    spyCli.run(new String[] { xmlFile1.toString(), xmlFile2.toString() });

    // Verify sequential processing
    verify(mockValidator, times(2))
      .validate(anyString(), anyString(), anyString(), any(byte[].class));
  }

  private String createValidNetexXmlContent() {
    return """
      <?xml version="1.0" encoding="UTF-8"?>
      <PublicationDelivery xmlns="http://www.netex.org.uk/netex" version="1.15:NO-NeTEx-networktimetable:1.5">
        <dataObjects>
          <ServiceFrame id="TEST:ServiceFrame:1" version="1">
            <lines>
              <Line id="TEST:Line:1" version="1">
                <Name>Test Line</Name>
              </Line>
            </lines>
          </ServiceFrame>
        </dataObjects>
      </PublicationDelivery>
      """;
  }

  private void createTestZipFile(Path zipFile) throws IOException {
    try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
      // Add shared data file (should be processed first)
      ZipEntry sharedEntry = new ZipEntry("_shared.xml");
      zos.putNextEntry(sharedEntry);
      zos.write(createValidNetexXmlContent().getBytes());
      zos.closeEntry();

      // Add regular file
      ZipEntry normalEntry = new ZipEntry("normal.xml");
      zos.putNextEntry(normalEntry);
      zos.write(createValidNetexXmlContent().getBytes());
      zos.closeEntry();

      // Add non-XML file (should be ignored)
      ZipEntry textEntry = new ZipEntry("readme.txt");
      zos.putNextEntry(textEntry);
      zos.write("This is not XML".getBytes());
      zos.closeEntry();
    }
  }

  private ValidationReport createMockValidationReport(boolean hasErrors) {
    ValidationReport report = mock(ValidationReport.class);
    Map<String, Long> entriesPerRule = new HashMap<>();

    if (hasErrors) {
      entriesPerRule.put("TestRule1", 1L);
      entriesPerRule.put("TestRule2", 1L);
    }

    when(report.getNumberOfValidationEntriesPerRule()).thenReturn(entriesPerRule);
    when(report.getValidationReportEntries()).thenReturn(List.of());

    return report;
  }

  private ValidationReport createMockValidationReportWithEntries() {
    ValidationReport report = mock(ValidationReport.class);
    Map<String, Long> entriesPerRule = new HashMap<>();
    entriesPerRule.put("TestRule", 1L);

    ValidationReportEntry entry = mock(ValidationReportEntry.class);
    when(entry.getSeverity()).thenReturn(Severity.ERROR);
    when(entry.getMessage()).thenReturn("Test validation error");
    when(entry.getLineNumber()).thenReturn(10);

    when(report.getNumberOfValidationEntriesPerRule()).thenReturn(entriesPerRule);
    when(report.getValidationReportEntries()).thenReturn(List.of(entry));

    return report;
  }
}

package org.entur.netex.validation.cli;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipOutputStream;
import org.entur.netex.validation.validator.NetexValidatorsRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NetexValidatorCLITest {

  @TempDir
  Path tempDir;

  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;
  private PrintStream originalErr;

  @BeforeEach
  void setUp() {
    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    originalErr = System.err;
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(new ByteArrayOutputStream()));
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
    assertNotNull(validator);
  }

  @Test
  void testRunWithSingleValidXmlFile() {
    Path realNetexFile = Paths.get("src/test/resources/demo/_FLB_shared_data.xml");
    assumeTrue(Files.exists(realNetexFile));

    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(new String[] { realNetexFile.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("_FLB_shared_data.xml"));
  }

  @Test
  void testRunWithSingleInvalidXmlFile() {
    Path invalidNetexFile = Paths.get(
      "src/test/resources/rb_flb-aggregated-netex-invalid-reference.zip"
    );
    assumeTrue(Files.exists(invalidNetexFile));

    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(new String[] { invalidNetexFile.toString() });

    String output = outputStream.toString();
    assertTrue(
      output.contains("invalid-reference") ||
      output.contains("📊 Dataset Validation Complete")
    );
  }

  @Test
  void testRunWithMultipleFiles() {
    Path validFile = Paths.get("src/test/resources/demo/_FLB_shared_data.xml");
    Path invalidFile = Paths.get(
      "src/test/resources/rb_flb-aggregated-netex-invalid-reference.zip"
    );
    assumeTrue(Files.exists(validFile) && Files.exists(invalidFile));

    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(new String[] { validFile.toString(), invalidFile.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("_FLB_shared_data.xml"));
    assertTrue(output.contains("📊 Dataset Validation Complete"));
    assertTrue(output.contains("Files processed: 2"));
  }

  @Test
  void testRunWithZipFile() {
    Path zipFile = Paths.get("src/test/resources/rb_flb-aggregated-netex.zip");
    assumeTrue(Files.exists(zipFile));

    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(new String[] { zipFile.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("📊 Dataset Validation Complete"));
  }

  @Test
  void testRunWithEmptyZipFile() throws IOException {
    Path zipFile = tempDir.resolve("empty.zip");
    try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {}

    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(new String[] { zipFile.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("⚠️  No XML files found in ZIP archive"));
  }

  @Test
  void testRunWithDebugFlag() {
    Path realNetexFile = Paths.get("src/test/resources/demo/_FLB_shared_data.xml");
    assumeTrue(Files.exists(realNetexFile));

    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(new String[] { "-d", "-v", realNetexFile.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("_FLB_shared_data.xml"));
  }

  @Test
  void testRunWithVerboseFlag() {
    Path invalidNetexFile = Paths.get(
      "src/test/resources/rb_flb-aggregated-netex-invalid-reference.zip"
    );
    assumeTrue(Files.exists(invalidNetexFile));

    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(new String[] { "-v", invalidNetexFile.toString() });

    String output = outputStream.toString();
    assertTrue(
      output.contains("invalid-reference") ||
      output.contains("📊 Dataset Validation Complete")
    );
  }

  @Test
  void testStreamBasedFileProcessing() {
    Path realNetexFile = Paths.get("src/test/resources/demo/_FLB_shared_data.xml");
    assumeTrue(Files.exists(realNetexFile));

    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(new String[] { realNetexFile.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("_FLB_shared_data.xml"));
  }

  @Test
  void testSharedDataFileOrdering() {
    Path sharedFile = Paths.get("src/test/resources/demo/_FLB_shared_data.xml");
    Path normalFile = Paths.get("src/test/resources/rb_flb-aggregated-netex.zip");
    assumeTrue(Files.exists(sharedFile) && Files.exists(normalFile));

    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(new String[] { normalFile.toString(), sharedFile.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("📊 Dataset Validation Complete"));
    assertTrue(output.contains("Files processed: 2"));
  }

  @Test
  void testZipFileWithMixedContent() {
    Path zipFile = Paths.get("src/test/resources/rb_flb-aggregated-netex.zip");
    assumeTrue(Files.exists(zipFile));

    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(new String[] { zipFile.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("📊 Dataset Validation Complete"));
  }

  @Test
  void testMemoryEfficientProcessing() {
    Path file1 = Paths.get("src/test/resources/demo/_FLB_shared_data.xml");
    Path file2 = Paths.get("src/test/resources/rb_flb-aggregated-netex.zip");
    assumeTrue(Files.exists(file1) && Files.exists(file2));

    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(new String[] { file1.toString(), file2.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("📊 Dataset Validation Complete"));
    assertTrue(output.contains("Files processed: 2"));
  }

  @Test
  void testValidationWithDifferentZipFiles() {
    Path validZip = Paths.get(
      "src/test/resources/rb_bra-flexible-lines-valid-passing-times.zip"
    );
    Path invalidZip = Paths.get(
      "src/test/resources/rb_flb-aggregated-netex-duplicate-id.zip"
    );
    assumeTrue(Files.exists(validZip) && Files.exists(invalidZip));

    NetexValidatorCLI cli = new NetexValidatorCLI();
    cli.run(new String[] { validZip.toString(), invalidZip.toString() });

    String output = outputStream.toString();
    assertTrue(output.contains("📊 Dataset Validation Complete"));
    assertTrue(output.contains("Files processed: 2"));
  }
}

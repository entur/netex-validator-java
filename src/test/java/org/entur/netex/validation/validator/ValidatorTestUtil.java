package org.entur.netex.validation.validator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.entur.netex.validation.validator.xpath.XPathRuleValidator;
import org.entur.netex.validation.xml.NetexXMLParser;

public class ValidatorTestUtil {

  public static ValidationReport getReport(
    String codespace,
    String reportId,
    String fileName,
    XPathValidator netexValidator
  ) throws IOException {
    NetexXMLParser netexXMLParser = new NetexXMLParser(Set.of("SiteFrame"));
    NetexSchemaValidator netexSchemaValidator = new NetexSchemaValidator(100);
    NetexValidatorsRunner netexValidatorsRunner = new NetexValidatorsRunner(
      netexXMLParser,
      netexSchemaValidator,
      List.of(netexValidator)
    );
    ValidationReport aggregatedValidationReport = new ValidationReport(
      codespace,
      reportId
    );

    URL dataset = ValidatorTestUtil.class.getResource('/' + fileName);
    assert dataset != null;
    String file = dataset.getFile();
    try (ZipFile zipFile = new ZipFile(file)) {
      zipFile
        .stream()
        // validate common files first
        .sorted(Comparator.comparing(ZipEntry::getName).reversed())
        .forEach(zipEntry ->
          validateEntry(
            codespace,
            reportId,
            netexValidatorsRunner,
            aggregatedValidationReport,
            zipFile,
            zipEntry
          )
        );
    }
    return aggregatedValidationReport;
  }

  private static void validateEntry(
    String codespace,
    String reportId,
    NetexValidatorsRunner netexValidatorsRunner,
    ValidationReport aggregatedValidationReport,
    ZipFile zipFile,
    ZipEntry zipEntry
  ) {
    byte[] content;
    try {
      content = zipFile.getInputStream(zipEntry).readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    ValidationReport validationReport = netexValidatorsRunner.validate(
      codespace,
      reportId,
      zipEntry.getName(),
      content
    );
    aggregatedValidationReport.addAllValidationReportEntries(
      validationReport.getValidationReportEntries()
    );
  }

  public static List<ValidationReportEntry> validateXPath(
    String codespace,
    XPathRuleValidator xPathRuleValidator,
    NetexXMLParser netexXMLParser,
    InputStream testDatasetAsStream
  ) throws IOException {
    assert testDatasetAsStream != null;
    List<ValidationReportEntry> validationReportEntries = new ArrayList<>();

    try (
      ZipInputStream zipInputStream = new ZipInputStream(testDatasetAsStream)
    ) {
      ZipEntry zipEntry = zipInputStream.getNextEntry();
      while (zipEntry != null) {
        byte[] content = zipInputStream.readAllBytes();
        XdmNode document = netexXMLParser.parseByteArrayToXdmNode(content);
        XPathRuleValidationContext xPathRuleValidationContext =
          new XPathRuleValidationContext(
            document,
            netexXMLParser,
            codespace,
            zipEntry.getName()
          );
        validationReportEntries.addAll(
          xPathRuleValidator.validate(xPathRuleValidationContext)
        );
        zipEntry = zipInputStream.getNextEntry();
      }
    }
    return validationReportEntries;
  }
}

package org.entur.netex.validation.validator;

import org.entur.netex.validation.validator.schema.NetexSchemaValidator;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ValidatorTestUtil {

    public static ValidationReport getReport(String codespace, String reportId, String fileName, NetexValidator netexValidator) throws IOException {
        NetexSchemaValidator netexSchemaValidator = new NetexSchemaValidator(100);
        NetexValidatorsRunner netexValidatorsRunner = new NetexValidatorsRunner(netexSchemaValidator, List.of(netexValidator));
        ValidationReport aggregatedValidationReport = new ValidationReport(codespace, reportId);

        URL dataset = ValidatorTestUtil.class.getResource('/' + fileName);
        assert dataset != null;
        String file = dataset.getFile();
        ZipFile zipFile = new ZipFile(file);
        zipFile.stream()
                // validate common files first
                .sorted(Comparator.comparing(ZipEntry::getName).reversed())
                .forEach(zipEntry -> validateEntry(codespace, reportId, netexValidatorsRunner, aggregatedValidationReport, zipFile, zipEntry));
        return aggregatedValidationReport;
    }

    private static void validateEntry(String codespace, String reportId, NetexValidatorsRunner netexValidatorsRunner, ValidationReport aggregatedValidationReport, ZipFile zipFile, ZipEntry zipEntry) {
        byte[] content;
        try {
            content = zipFile.getInputStream(zipEntry).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ValidationReport validationReport = netexValidatorsRunner.validate(codespace, reportId, zipEntry.getName(), content);
        aggregatedValidationReport.addAllValidationReportEntries(validationReport.getValidationReportEntries());
    }
}

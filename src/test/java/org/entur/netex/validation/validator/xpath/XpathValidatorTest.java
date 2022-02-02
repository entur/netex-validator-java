package org.entur.netex.validation.validator.xpath;

import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.configuration.DefaultValidationConfigLoader;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.DefaultValidationEntryFactory;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class XpathValidatorTest {

    private static final String TEST_DATASET_AUTHORITY_VALIDATION_FILE_NAME = "rb_flb-aggregated-netex.zip";

    @Test
    void testValidator() throws IOException {

        ValidationTreeFactory validationTreeFactory = new DefaultValidationTreeFactory();
        XPathValidator xPathValidator = new XPathValidator(validationTreeFactory, new DefaultValidationEntryFactory(new DefaultValidationConfigLoader()));
        NetexXMLParser netexXMLParser = new NetexXMLParser(Set.of("SiteFrame"));

        InputStream testDatasetAsStream = getClass().getResourceAsStream('/' + TEST_DATASET_AUTHORITY_VALIDATION_FILE_NAME);
        assert testDatasetAsStream != null;

        List<ValidationReportEntry> validationReportEntries = new ArrayList<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(testDatasetAsStream)) {

            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                byte[] content = zipInputStream.readAllBytes();
                XdmNode document = netexXMLParser.parseFileToXdmNode(content);
                XPathValidationContext xPathValidationContext = new XPathValidationContext(document, netexXMLParser, "FLB", zipEntry.getName());
                validationReportEntries.addAll(xPathValidator.validate(xPathValidationContext));
                zipEntry = zipInputStream.getNextEntry();
            }
            Assertions.assertFalse(validationReportEntries.isEmpty());
            Assertions.assertTrue(validationReportEntries.stream().allMatch(validationReportEntry -> validationReportEntry.getFileName().endsWith(".xml")));

        }
    }
}
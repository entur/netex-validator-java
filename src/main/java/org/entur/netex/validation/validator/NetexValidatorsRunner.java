package org.entur.netex.validation.validator;

import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.id.IdVersion;
import org.entur.netex.validation.validator.id.NetexIdExtractorHelper;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.validator.xpath.ValidationContext;
import org.entur.netex.validation.xml.XMLParserUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Orchestrate the execution of individual instances of {@link NetexValidator}.
 * The first step in the validation process is the XML Schema validation.
 * The XML Schema validation is a blocking step: further validators downstream are skipped in case of XML Schema validation errors.
 */
public class NetexValidatorsRunner {

    private final NetexSchemaValidator netexSchemaValidator;
    private final List<NetexValidator> netexValidators;

    public NetexValidatorsRunner(NetexSchemaValidator netexSchemaValidator, List<NetexValidator> netexValidators) {
        this.netexSchemaValidator = netexSchemaValidator;
        this.netexValidators = netexValidators;
    }

    public ValidationReport validate(String codespace, String validationReportId, String filename, byte[] fileContent)  {
        ValidationReport validationReport = new ValidationReport(codespace, validationReportId);
        validationReport.addAllValidationReportEntries(netexSchemaValidator.validateSchema(filename, fileContent));
        if (validationReport.hasError()) {
            // do not run subsequent validators if the XML Schema validation fails
            return validationReport;
        }

        XdmNode document = XMLParserUtil.parseFileToXdmNode(fileContent);
        XPathCompiler xPathCompiler = XMLParserUtil.getXPathCompiler();
        Set<IdVersion> localIds = new HashSet<>(NetexIdExtractorHelper.collectEntityIdentifiers(document, xPathCompiler, filename, Set.of("Codespace")));
        List<IdVersion> localRefs = NetexIdExtractorHelper.collectEntityReferences(document, xPathCompiler, filename, null);

        ValidationContext validationContext = new ValidationContext(document, xPathCompiler, codespace, filename, localIds, localRefs);

        netexValidators.forEach(netexValidator -> netexValidator.validate(validationReport, validationContext));

        return validationReport;

    }

}

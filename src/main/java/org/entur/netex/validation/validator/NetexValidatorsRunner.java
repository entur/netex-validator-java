package org.entur.netex.validation.validator;

import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmNode;
import org.apache.commons.lang3.time.StopWatch;
import org.entur.netex.validation.validator.id.IdVersion;
import org.entur.netex.validation.validator.id.NetexIdExtractorHelper;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.validator.xpath.ValidationContext;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


/**
 * Orchestrate the execution of individual instances of {@link NetexValidator}.
 * The first step in the validation process is the XML Schema validation.
 * The XML Schema validation is a blocking step: further validators downstream are skipped in case of XML Schema validation errors.
 */
public class NetexValidatorsRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetexValidatorsRunner.class);
    private static final int VALIDATION_PROGRESS_NOTIFICATION_PERIOD_MILLIS = 10000;
    private static final int MAX_WAITING_LOOPS = 180;


    private final NetexSchemaValidator netexSchemaValidator;
    private final List<NetexValidator> netexValidators;
    private final NetexXMLParser netexXMLParser;

    public NetexValidatorsRunner(NetexXMLParser netexXMLParser, NetexSchemaValidator netexSchemaValidator, List<NetexValidator> netexValidators) {
        this.netexSchemaValidator = netexSchemaValidator;
        this.netexValidators = netexValidators;
        this.netexXMLParser = netexXMLParser;
    }

    public ValidationReport validate(String codespace, String validationReportId, String filename, byte[] fileContent) {
        return validate(codespace, validationReportId, filename, fileContent, false, false, new NoopNetexValidationCallBack());
    }

    public ValidationReport validate(String codespace, String validationReportId, String filename, byte[] fileContent, boolean skipSchemaValidation, boolean skipValidators, NetexValidationProgressCallBack netexValidationProgressCallBack) {
        ValidationReport validationReport = new ValidationReport(codespace, validationReportId);

        if (skipSchemaValidation) {
            LOGGER.info("Skipping schema validation");
        } else {
            runSchemaValidation(codespace, validationReportId, filename, fileContent, netexValidationProgressCallBack, validationReport);
        }

        if (validationReport.hasError()) {
            // do not run subsequent validators if the XML Schema validation fails
            return validationReport;
        }

        if (skipValidators) {
            LOGGER.info("Skipping NeTEx validators");
            return validationReport;
        }

        runNetexValidators(codespace, validationReportId, filename, fileContent, netexValidationProgressCallBack, validationReport);

        return validationReport;
    }

    /**
     * Run the XML schema validation.
     *
     * @param codespace
     * @param validationReportId
     * @param filename
     * @param fileContent
     * @param netexValidationProgressCallBack
     * @param validationReport
     */
    private void runSchemaValidation(String codespace, String validationReportId, String filename, byte[] fileContent, NetexValidationProgressCallBack netexValidationProgressCallBack, ValidationReport validationReport) {
        StopWatch xmlSchemaValidationStopWatch = new StopWatch();
        netexValidationProgressCallBack.notifyProgress("Starting NeTEx Schema validation");
        xmlSchemaValidationStopWatch.start();

        // Periodically notify progress in a separate thread
        AtomicBoolean schemaValidationComplete = new AtomicBoolean(false);
        CompletableFuture.supplyAsync(() -> {
            int counter = 0;
            while (!schemaValidationComplete.get() && counter < MAX_WAITING_LOOPS) {
                counter++;
                netexValidationProgressCallBack.notifyProgress("Running NeTEx Schema validation");
                try {
                    Thread.sleep(VALIDATION_PROGRESS_NOTIFICATION_PERIOD_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
            if (counter >= MAX_WAITING_LOOPS) {
                LOGGER.warn("Schema validation still running after {} milliseconds", counter * VALIDATION_PROGRESS_NOTIFICATION_PERIOD_MILLIS);
            }
            return null;
        });

        try {
            validationReport.addAllValidationReportEntries(netexSchemaValidator.validateSchema(filename, fileContent));
        } finally {
            schemaValidationComplete.set(true);
        }
        xmlSchemaValidationStopWatch.stop();
        LOGGER.debug("XMLSchema validation for {}/{}/{} completed in {} ms", codespace, validationReportId, filename, xmlSchemaValidationStopWatch.getTime());
    }

    /**
     * Run the NeTEx validators.
     *
     * @param codespace
     * @param validationReportId
     * @param filename
     * @param fileContent
     * @param netexValidationProgressCallBack
     * @param validationReport
     */
    private void runNetexValidators(String codespace, String validationReportId, String filename, byte[] fileContent, NetexValidationProgressCallBack netexValidationProgressCallBack, ValidationReport validationReport) {
        XdmNode document = netexXMLParser.parseFileToXdmNode(fileContent);
        XPathCompiler xPathCompiler = netexXMLParser.getXPathCompiler();
        Set<IdVersion> localIds = new HashSet<>(NetexIdExtractorHelper.collectEntityIdentifiers(document, xPathCompiler, filename, Set.of("Codespace")));
        List<IdVersion> localRefs = NetexIdExtractorHelper.collectEntityReferences(document, xPathCompiler, filename, null);

        ValidationContext validationContext = new ValidationContext(document, netexXMLParser, codespace, filename, localIds, localRefs);

        for (NetexValidator netexValidator : netexValidators) {
            netexValidationProgressCallBack.notifyProgress("Running validator " + netexValidator.getClass().getName());
            StopWatch netexValidatorStopWatch = new StopWatch();
            netexValidatorStopWatch.start();
            netexValidator.validate(validationReport, validationContext);
            netexValidatorStopWatch.stop();
            if (netexValidatorStopWatch.getTime() > 30000) {
                LOGGER.warn("Validator {} for {}/{}/{} completed in {} ms", netexValidator.getClass().getName(), codespace, validationReportId, filename, netexValidatorStopWatch.getTime());
            } else {
                LOGGER.debug("Validator {} for {}/{}/{} completed in {} ms", netexValidator.getClass().getName(), codespace, validationReportId, filename, netexValidatorStopWatch.getTime());
            }
        }
    }


    public Set<String> getRuleDescriptions() {
        return netexValidators.stream().map(NetexValidator::getRuleDescriptions).flatMap(Collection::stream).collect(Collectors.toSet());
    }

}

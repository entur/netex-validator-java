package org.entur.netex.validation.validator;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmNode;
import org.apache.commons.lang3.time.StopWatch;
import org.entur.netex.NetexParser;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.validation.validator.id.IdVersion;
import org.entur.netex.validation.validator.id.NetexIdExtractorHelper;
import org.entur.netex.validation.validator.jaxb.*;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Orchestrate the execution of individual instances of {@link NetexValidator}.
 * The first step in the validation process is the XML Schema validation.
 * The XML Schema validation is a blocking step: further validators downstream are skipped in case of XML Schema validation errors.
 */
public class NetexValidatorsRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    NetexValidatorsRunner.class
  );
  private static final int VALIDATION_PROGRESS_NOTIFICATION_PERIOD_MILLIS =
    10000;
  private static final int MAX_WAITING_LOOPS = 180;

  private final NetexSchemaValidator netexSchemaValidator;
  private final List<XPathValidator> xPathValidators;
  private final List<JAXBValidator> jaxbValidators;
  private final List<DatasetValidator> datasetValidators;
  private final List<NetexDataCollector> netexDataCollectors;
  private final NetexDataRepository netexDataRepository;
  private final StopPlaceRepository stopPlaceRepository;

  private final NetexXMLParser netexXMLParser;

  NetexValidatorsRunner(
    NetexXMLParser netexXMLParser,
    NetexSchemaValidator netexSchemaValidator,
    List<XPathValidator> xPathValidators,
    List<JAXBValidator> jaxbValidators,
    List<DatasetValidator> datasetValidators,
    List<NetexDataCollector> netexDataCollectors,
    NetexDataRepository netexDataRepository,
    StopPlaceRepository stopPlaceRepository
  ) {
    this.netexXMLParser = netexXMLParser;
    this.netexSchemaValidator = netexSchemaValidator;
    this.xPathValidators = xPathValidators;
    this.jaxbValidators = jaxbValidators;
    this.datasetValidators = datasetValidators;
    this.netexDataCollectors = netexDataCollectors;
    this.netexDataRepository = netexDataRepository;
    this.stopPlaceRepository = stopPlaceRepository;
  }

  public static NetexValidatorsRunnerBuilder of() {
    return new NetexValidatorsRunnerBuilder();
  }

  public ValidationReport validate(
    String codespace,
    String validationReportId,
    String filename,
    byte[] fileContent
  ) {
    return validate(
      codespace,
      validationReportId,
      filename,
      fileContent,
      false,
      false,
      new NoopNetexValidationCallBack()
    );
  }

  public ValidationReport validate(
    String codespace,
    String validationReportId,
    String filename,
    byte[] fileContent,
    boolean skipSchemaValidation,
    boolean skipValidators,
    NetexValidationProgressCallBack netexValidationProgressCallBack
  ) {
    ValidationReport validationReport = new ValidationReport(
      codespace,
      validationReportId
    );

    if (netexSchemaValidator == null || skipSchemaValidation) {
      LOGGER.info("Skipping schema validation");
    } else {
      runSchemaValidation(
        codespace,
        validationReportId,
        filename,
        fileContent,
        netexValidationProgressCallBack,
        validationReport
      );
    }

    if (validationReport.hasError()) {
      // do not run subsequent validators if the XML Schema validation fails
      return validationReport;
    }

    if (skipValidators) {
      LOGGER.info("Skipping NeTEx validators");
      return validationReport;
    }

    XPathValidationContext xPathValidationContext =
      prepareXPathValidationContext(codespace, filename, fileContent);

    runXPathValidators(
      codespace,
      validationReportId,
      filename,
      xPathValidationContext,
      netexValidationProgressCallBack,
      validationReport
    );

    if (validationReport.hasError()) {
      // do not run subsequent validators if the XPath validation fails
      return validationReport;
    }

    JAXBValidationContext jaxbValidationContext = prepareJAXBValidationContext(
      validationReportId,
      codespace,
      filename,
      fileContent,
      xPathValidationContext.getLocalIdsMap()
    );
    postPrepareXPathValidationContext(jaxbValidationContext);
    runJAXBValidators(
      codespace,
      validationReportId,
      filename,
      jaxbValidationContext,
      netexValidationProgressCallBack,
      validationReport
    );

    return validationReport;
  }

  protected void postPrepareXPathValidationContext(
    JAXBValidationContext validationContext
  ) {
    LOGGER.info(
      "Collecting NeTEx data for file {}",
      validationContext.getFileName()
    );
    if (!validationContext.isCommonFile()) {
      netexDataCollectors.forEach(netexDataCollector ->
        netexDataCollector.collect(validationContext)
      );
    }
  }

  protected XPathValidationContext prepareXPathValidationContext(
    String codespace,
    String filename,
    byte[] fileContent
  ) {
    XdmNode document = netexXMLParser.parseByteArrayToXdmNode(fileContent);
    XPathCompiler xPathCompiler = netexXMLParser.getXPathCompiler();
    Set<IdVersion> localIds = new HashSet<>(
      NetexIdExtractorHelper.collectEntityIdentifiers(
        document,
        xPathCompiler,
        filename,
        Set.of("Codespace")
      )
    );
    List<IdVersion> localRefs = NetexIdExtractorHelper.collectEntityReferences(
      document,
      xPathCompiler,
      filename,
      null
    );

    return new XPathValidationContext(
      document,
      netexXMLParser,
      codespace,
      filename,
      localIds,
      localRefs
    );
  }

  protected JAXBValidationContext prepareJAXBValidationContext(
    String validationReportId,
    String codespace,
    String filename,
    byte[] fileContent,
    Map<String, IdVersion> localIdMap
  ) {
    NetexParser netexParser = new NetexParser();
    NetexEntitiesIndex netexEntitiesIndex = netexParser.parse(
      new ByteArrayInputStream(fileContent)
    );

    return new JAXBValidationContext(
      validationReportId,
      netexEntitiesIndex,
      netexDataRepository,
      stopPlaceRepository,
      codespace,
      filename,
      localIdMap
    );
  }

  /**
   * Run the XML schema validation.
   */
  private void runSchemaValidation(
    String codespace,
    String validationReportId,
    String filename,
    byte[] fileContent,
    NetexValidationProgressCallBack netexValidationProgressCallBack,
    ValidationReport validationReport
  ) {
    StopWatch xmlSchemaValidationStopWatch = new StopWatch();
    netexValidationProgressCallBack.notifyProgress(
      "Starting NeTEx Schema validation"
    );
    xmlSchemaValidationStopWatch.start();

    AtomicBoolean schemaValidationComplete = new AtomicBoolean(false);
    notifyProgressAsync(
      netexValidationProgressCallBack,
      "NeTEx Schema validation",
      schemaValidationComplete
    );

    try {
      validationReport.addAllValidationReportEntries(
        netexSchemaValidator.validateSchema(filename, fileContent)
      );
    } finally {
      schemaValidationComplete.set(true);
    }
    xmlSchemaValidationStopWatch.stop();
    LOGGER.debug(
      "XMLSchema validation for {}/{}/{} completed in {} ms",
      codespace,
      validationReportId,
      filename,
      xmlSchemaValidationStopWatch.getTime()
    );
  }

  /**
   * Run the NeTEx validators.
   */
  private void runXPathValidators(
    String codespace,
    String validationReportId,
    String filename,
    XPathValidationContext xPathValidationContext,
    NetexValidationProgressCallBack netexValidationProgressCallBack,
    ValidationReport validationReport
  ) {
    for (XPathValidator xPathValidator : xPathValidators) {
      String netexValidatorName = xPathValidator.getClass().getName();
      netexValidationProgressCallBack.notifyProgress(
        "Starting validator " + netexValidatorName
      );
      StopWatch xpathValidatorStopWatch = new StopWatch();
      xpathValidatorStopWatch.start();

      AtomicBoolean netexValidatorComplete = new AtomicBoolean(false);
      notifyProgressAsync(
        netexValidationProgressCallBack,
        netexValidatorName,
        netexValidatorComplete
      );

      try {
        xPathValidator.validate(validationReport, xPathValidationContext);
      } finally {
        netexValidatorComplete.set(true);
      }

      xpathValidatorStopWatch.stop();
      if (xpathValidatorStopWatch.getTime() > 30000) {
        LOGGER.warn(
          "Validator {} for {}/{}/{} completed in {} ms",
          netexValidatorName,
          codespace,
          validationReportId,
          filename,
          xpathValidatorStopWatch.getTime()
        );
      } else {
        LOGGER.debug(
          "Validator {} for {}/{}/{} completed in {} ms",
          netexValidatorName,
          codespace,
          validationReportId,
          filename,
          xpathValidatorStopWatch.getTime()
        );
      }
    }
  }

  /**
   * Run the NeTEx validators.
   */
  private void runJAXBValidators(
    String codespace,
    String validationReportId,
    String filename,
    JAXBValidationContext jaxbValidationContext,
    NetexValidationProgressCallBack netexValidationProgressCallBack,
    ValidationReport validationReport
  ) {
    for (JAXBValidator jaxbValidator : jaxbValidators) {
      String netexValidatorName = jaxbValidator.getClass().getName();
      netexValidationProgressCallBack.notifyProgress(
        "Starting validator " + netexValidatorName
      );
      StopWatch jaxbValidatorStopWatch = new StopWatch();
      jaxbValidatorStopWatch.start();

      AtomicBoolean netexValidatorComplete = new AtomicBoolean(false);
      notifyProgressAsync(
        netexValidationProgressCallBack,
        netexValidatorName,
        netexValidatorComplete
      );

      try {
        jaxbValidator.validate(validationReport, jaxbValidationContext);
      } finally {
        netexValidatorComplete.set(true);
      }

      jaxbValidatorStopWatch.stop();
      if (jaxbValidatorStopWatch.getTime() > 30000) {
        LOGGER.warn(
          "Validator {} for {}/{}/{} completed in {} ms",
          netexValidatorName,
          codespace,
          validationReportId,
          filename,
          jaxbValidatorStopWatch.getTime()
        );
      } else {
        LOGGER.debug(
          "Validator {} for {}/{}/{} completed in {} ms",
          netexValidatorName,
          codespace,
          validationReportId,
          filename,
          jaxbValidatorStopWatch.getTime()
        );
      }
    }
  }

  /**
   * Run the NeTEx validators.
   */
  public ValidationReport runNetexDatasetValidators(
    ValidationReport validationReport,
    NetexValidationProgressCallBack netexValidationProgressCallBack
  ) {
    for (DatasetValidator datasetValidator : datasetValidators) {
      String netexValidatorName = datasetValidator.getClass().getName();
      netexValidationProgressCallBack.notifyProgress(
        "Starting validator " + netexValidatorName
      );
      StopWatch datasetValidatorStopWatch = new StopWatch();
      datasetValidatorStopWatch.start();

      AtomicBoolean netexValidatorComplete = new AtomicBoolean(false);
      notifyProgressAsync(
        netexValidationProgressCallBack,
        netexValidatorName,
        netexValidatorComplete
      );

      try {
        datasetValidator.validate(validationReport);
      } finally {
        netexValidatorComplete.set(true);
      }

      datasetValidatorStopWatch.stop();
      if (datasetValidatorStopWatch.getTime() > 30000) {
        LOGGER.warn(
          "Validator {} for {}/{} completed in {} ms",
          netexValidatorName,
          validationReport.getCodespace(),
          validationReport.getValidationReportId(),
          datasetValidatorStopWatch.getTime()
        );
      } else {
        LOGGER.debug(
          "Validator {} for {}/{} completed in {} ms",
          netexValidatorName,
          validationReport.getCodespace(),
          validationReport.getValidationReportId(),
          datasetValidatorStopWatch.getTime()
        );
      }
    }
    return validationReport;
  }

  /**
   * Notify a validation task progress in a separate thread.
   */
  private void notifyProgressAsync(
    NetexValidationProgressCallBack netexValidationProgressCallBack,
    String taskName,
    AtomicBoolean taskComplete
  ) {
    CompletableFuture.supplyAsync(() -> {
      int counter = 0;
      while (!taskComplete.get() && counter < MAX_WAITING_LOOPS) {
        counter++;
        netexValidationProgressCallBack.notifyProgress("Running " + taskName);
        try {
          Thread.sleep(VALIDATION_PROGRESS_NOTIFICATION_PERIOD_MILLIS);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new RuntimeException(e);
        }
      }
      if (counter >= MAX_WAITING_LOOPS) {
        LOGGER.warn(
          "Task {} still running after {} milliseconds",
          taskName,
          counter * VALIDATION_PROGRESS_NOTIFICATION_PERIOD_MILLIS
        );
      }
      return null;
    });
  }

  public Set<String> getRuleDescriptions() {
    return Stream
      .concat(xPathValidators.stream(), jaxbValidators.stream())
      .map(NetexValidator::getRuleDescriptions)
      .flatMap(Collection::stream)
      .collect(Collectors.toSet());
  }
}

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
import org.entur.netex.validation.validator.schema.NetexSchemaValidationContext;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Orchestrate the execution of individual instances of {@link NetexValidator}.
 * The first step in the validation process is the XML Schema validation.
 * The XML Schema validation is a blocking step: further validators downstream are skipped in case of XML Schema validation errors.
 * The second step runs XPath-based validators.
 * The third step runs JAXB-based validators.
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

  NetexValidatorsRunner(NetexValidatorsRunnerBuilder builder) {
    this.netexXMLParser = builder.getNetexXMLParser();
    this.netexSchemaValidator = builder.getNetexSchemaValidator();

    this.xPathValidators = builder.getXPathValidators();
    if (!xPathValidators.isEmpty() && netexXMLParser == null) {
      throw new IllegalArgumentException(
        "XPath validators require a NeTEx XML parser"
      );
    }

    this.jaxbValidators = builder.getJaxbValidators();
    this.datasetValidators = builder.getDatasetValidators();
    this.netexDataCollectors = builder.getNetexDataCollectors();
    this.netexDataRepository = builder.getNetexDataRepository();
    this.stopPlaceRepository = builder.getStopPlaceRepository();
  }

  public static NetexValidatorsRunnerBuilder of() {
    return new NetexValidatorsRunnerBuilder();
  }

  /**
   * Validate a NeTEx file.
   */
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

  /**
   * Validate a NeTEx file.
   * Optionally skip the NeTEx schema validation or the NeTEx validators
   */
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
      LOGGER.debug("Skipping schema validation");
    } else {
      NetexSchemaValidationContext netexSchemaValidationContext =
        new NetexSchemaValidationContext(filename, codespace, fileContent);
      runSchemaValidation(
        validationReportId,
        netexSchemaValidationContext,
        netexValidationProgressCallBack,
        validationReport
      );
    }

    if (validationReport.hasError()) {
      // do not run subsequent validators if the XML Schema validation fails
      return validationReport;
    }

    if (skipValidators || !hasNetexValidators()) {
      LOGGER.debug("Skipping NeTEx validators");
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

  /**
   *
   * @return true if this validator runner is configured to run the XML schema validation.
   */
  public boolean hasSchemaValidator() {
    return netexSchemaValidator != null;
  }

  /**
   *
   * @return true if this validator runner is configured to run the XPath and JAXB validators.
   */
  public boolean hasNetexValidators() {
    return !xPathValidators.isEmpty() || !jaxbValidators.isEmpty();
  }

  /**
   *
   * @return true if this validator runner is configured to run dataset-wide validators.
   */
  public boolean hasDatasetValidators() {
    return !datasetValidators.isEmpty();
  }

  protected void postPrepareXPathValidationContext(
    JAXBValidationContext validationContext
  ) {
    LOGGER.info(
      "Collecting NeTEx data for file {}",
      validationContext.getFileName()
    );
    netexDataCollectors.forEach(netexDataCollector ->
      netexDataCollector.collect(validationContext)
    );
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
    String validationReportId,
    NetexSchemaValidationContext netexSchemaValidationContext,
    NetexValidationProgressCallBack netexValidationProgressCallBack,
    ValidationReport validationReport
  ) {
    runValidator(
      netexSchemaValidationContext.getCodespace(),
      validationReportId,
      netexSchemaValidationContext.getFileName(),
      netexSchemaValidator,
      validationReport,
      netexValidationProgressCallBack,
      netexSchemaValidationContext
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
      runValidator(
        codespace,
        validationReportId,
        filename,
        xPathValidator,
        validationReport,
        netexValidationProgressCallBack,
        xPathValidationContext
      );
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
      runValidator(
        codespace,
        validationReportId,
        filename,
        jaxbValidator,
        validationReport,
        netexValidationProgressCallBack,
        jaxbValidationContext
      );
    }
  }

  /**
   * Run the NeTEx dataset validators.
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

  private <C extends ValidationContext> void runValidator(
    String codespace,
    String validationReportId,
    String filename,
    NetexValidator<C> validator,
    ValidationReport validationReport,
    NetexValidationProgressCallBack progressCallback,
    C validationContext
  ) {
    String netexValidatorName = validator.getClass().getName();
    progressCallback.notifyProgress("Starting validator " + netexValidatorName);

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    AtomicBoolean validatorComplete = new AtomicBoolean(false);
    notifyProgressAsync(
      progressCallback,
      netexValidatorName,
      validatorComplete
    );

    try {
      validator.validate(validationReport, validationContext);
    } finally {
      validatorComplete.set(true);
    }

    stopWatch.stop();
    if (stopWatch.getTime() > 30000) {
      LOGGER.warn(
        "Validator {} for {}/{}/{} completed in {} ms",
        netexValidatorName,
        codespace,
        validationReportId,
        filename,
        stopWatch.getTime()
      );
    } else {
      LOGGER.debug(
        "Validator {} for {}/{}/{} completed in {} ms",
        netexValidatorName,
        codespace,
        validationReportId,
        filename,
        stopWatch.getTime()
      );
    }
  }

  public Set<String> getRuleDescriptions() {
    return Stream
      .concat(xPathValidators.stream(), jaxbValidators.stream())
      .map(NetexValidator::getRuleDescriptions)
      .flatMap(Collection::stream)
      .collect(Collectors.toSet());
  }
}

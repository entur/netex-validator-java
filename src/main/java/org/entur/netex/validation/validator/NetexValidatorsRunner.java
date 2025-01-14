package org.entur.netex.validation.validator;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
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
  private final CommonDataRepositoryLoader commonDataRepository;
  private final Function<JAXBValidationContext, StopPlaceRepository> stopPlaceRepository;
  private final NetexXMLParser netexXMLParser;
  private final ValidationReportEntryFactory validationReportEntryFactory;

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
    this.commonDataRepository = builder.getCommonDataRepository();
    this.stopPlaceRepository = builder.getStopPlaceRepository();
    this.validationReportEntryFactory =
      Objects.requireNonNull(builder.getValidationReportEntryFactory());
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
      validationReport.addAllValidationReportEntries(
        runSchemaValidation(
          validationReportId,
          netexSchemaValidationContext,
          netexValidationProgressCallBack
        )
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
      prepareXPathValidationContext(
        validationReportId,
        codespace,
        filename,
        fileContent
      );

    validationReport.addAllValidationReportEntries(
      runXPathValidators(
        codespace,
        validationReportId,
        filename,
        xPathValidationContext,
        netexValidationProgressCallBack
      )
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

    if (jaxbValidationContext.isCommonFile() && commonDataRepository != null) {
      LOGGER.info(
        "Collecting common data for common file {}",
        jaxbValidationContext.getFileName()
      );
      commonDataRepository.collect(
        jaxbValidationContext.getValidationReportId(),
        jaxbValidationContext.getNetexEntitiesIndex()
      );
    }
    LOGGER.info(
      "Collecting data for file {}",
      jaxbValidationContext.getFileName()
    );
    netexDataCollectors.forEach(netexDataCollector ->
      netexDataCollector.collect(jaxbValidationContext)
    );

    validationReport.addAllValidationReportEntries(
      runJAXBValidators(
        codespace,
        validationReportId,
        filename,
        jaxbValidationContext,
        netexValidationProgressCallBack
      )
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

  protected XPathValidationContext prepareXPathValidationContext(
    String validationReportId,
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
      localRefs,
      validationReportId
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
      commonDataRepository,
      stopPlaceRepository,
      codespace,
      filename,
      localIdMap
    );
  }

  /**
   * Run the XML schema validation.
   */
  private List<ValidationReportEntry> runSchemaValidation(
    String validationReportId,
    NetexSchemaValidationContext netexSchemaValidationContext,
    NetexValidationProgressCallBack netexValidationProgressCallBack
  ) {
    return runValidator(
      netexSchemaValidationContext.getCodespace(),
      validationReportId,
      netexSchemaValidationContext.getFileName(),
      netexSchemaValidator,
      netexValidationProgressCallBack,
      netexSchemaValidationContext
    );
  }

  /**
   * Run the NeTEx validators.
   */
  private List<ValidationReportEntry> runXPathValidators(
    String codespace,
    String validationReportId,
    String filename,
    XPathValidationContext xPathValidationContext,
    NetexValidationProgressCallBack netexValidationProgressCallBack
  ) {
    return xPathValidators
      .stream()
      .map(xPathValidator ->
        runValidator(
          codespace,
          validationReportId,
          filename,
          xPathValidator,
          netexValidationProgressCallBack,
          xPathValidationContext
        )
      )
      .flatMap(Collection::stream)
      .toList();
  }

  /**
   * Run the NeTEx validators.
   */
  private List<ValidationReportEntry> runJAXBValidators(
    String codespace,
    String validationReportId,
    String filename,
    JAXBValidationContext jaxbValidationContext,
    NetexValidationProgressCallBack netexValidationProgressCallBack
  ) {
    return jaxbValidators
      .stream()
      .map(jaxbValidator ->
        runValidator(
          codespace,
          validationReportId,
          filename,
          jaxbValidator,
          netexValidationProgressCallBack,
          jaxbValidationContext
        )
      )
      .flatMap(Collection::stream)
      .toList();
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
        LOGGER.info(
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

  private <
    C extends ValidationContext
  > List<ValidationReportEntry> runValidator(
    String codespace,
    String validationReportId,
    String filename,
    NetexValidator<C> validator,
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
      return validator
        .validate(validationContext)
        .stream()
        .map(validationReportEntryFactory::createValidationReportEntry)
        .toList();
    } finally {
      validatorComplete.set(true);
      stopWatch.stop();
      if (stopWatch.getTime() > 30000) {
        LOGGER.info(
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
  }

  public Map<String, String> getRuleDescriptionByCode() {
    return Stream
      .concat(xPathValidators.stream(), jaxbValidators.stream())
      .map(NetexValidator::getRules)
      .flatMap(Collection::stream)
      .collect(
        Collectors.toMap(
          ValidationRule::code,
          validationRule ->
            validationReportEntryFactory
              .templateValidationReportEntry(validationRule)
              .getName(),
          (a, b) -> {
            throw new IllegalStateException(
              "Duplicate validation rule: " + a + ", " + b
            );
          },
          TreeMap::new
        )
      );
  }

  /**
   *
   * @deprecated use {@link #getRuleDescriptionByCode()}
   */
  @Deprecated
  public Set<String> getRuleDescriptions() {
    return new LinkedHashSet<>(getRuleDescriptionByCode().values());
  }
}

package org.entur.netex.validation.validator;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
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
 * Orchestrate the execution of individual instances of {@link XPathValidator}.
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
  private final List<XPathValidator> netexValidators;
  private final List<JAXBValidator> jaxbValidators;
  private final List<NetexDatasetValidator> netexDatasetValidators;
  private final List<CommonDataScraper> commonDataScrapers;
  private final CommonDataRepository commonDataRepository;
  private final StopPlaceRepository stopPlaceRepository;

  private final NetexXMLParser netexXMLParser;

  public NetexValidatorsRunner(
    NetexXMLParser netexXMLParser,
    List<XPathValidator> netexValidators
  ) {
    this(netexXMLParser, null, netexValidators);
  }

  public NetexValidatorsRunner(
    NetexXMLParser netexXMLParser,
    NetexSchemaValidator netexSchemaValidator,
    List<XPathValidator> netexValidators
  ) {
    this(netexXMLParser, netexSchemaValidator, netexValidators, null, null);
  }

  public NetexValidatorsRunner(
    NetexXMLParser netexXMLParser,
    NetexSchemaValidator netexSchemaValidator,
    List<XPathValidator> netexValidators,
    CommonDataRepository commonDataRepository,
    StopPlaceRepository stopPlaceRepository
  ) {
    this(
      netexXMLParser,
      netexSchemaValidator,
      netexValidators,
      List.of(),
      commonDataRepository,
      stopPlaceRepository
    );
  }

  public NetexValidatorsRunner(
    NetexXMLParser netexXMLParser,
    NetexSchemaValidator netexSchemaValidator,
    List<XPathValidator> netexValidators,
    List<NetexDatasetValidator> netexDatasetValidators,
    CommonDataRepository commonDataRepository,
    StopPlaceRepository stopPlaceRepository
  ) {
    this(
      netexXMLParser,
      netexSchemaValidator,
      netexValidators,
      List.of(),
      netexDatasetValidators,
      List.of(),
      commonDataRepository,
      stopPlaceRepository
    );
  }

  public NetexValidatorsRunner(
    NetexXMLParser netexXMLParser,
    NetexSchemaValidator netexSchemaValidator,
    List<XPathValidator> netexValidators,
    List<JAXBValidator> jaxbValidators,
    List<NetexDatasetValidator> netexDatasetValidators,
    List<CommonDataScraper> commonDataScrapers,
    CommonDataRepository commonDataRepository,
    StopPlaceRepository stopPlaceRepository
  ) {
    this.netexXMLParser = netexXMLParser;
    this.netexSchemaValidator = netexSchemaValidator;
    this.netexValidators = netexValidators;
    this.jaxbValidators = jaxbValidators;
    this.netexDatasetValidators = netexDatasetValidators;
    this.commonDataScrapers = commonDataScrapers;
    this.commonDataRepository = commonDataRepository;
    this.stopPlaceRepository = stopPlaceRepository;
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

    runNetexValidators(
      codespace,
      validationReportId,
      filename,
      xPathValidationContext,
      netexValidationProgressCallBack,
      validationReport
    );

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
      "Starting data scraping for file {}",
      validationContext.getFileName()
    );
    if (!validationContext.isCommonFile()) {
      commonDataScrapers.forEach(commonDataScraper ->
        commonDataScraper.scrapeData(validationContext)
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
  private void runNetexValidators(
    String codespace,
    String validationReportId,
    String filename,
    XPathValidationContext xPathValidationContext,
    NetexValidationProgressCallBack netexValidationProgressCallBack,
    ValidationReport validationReport
  ) {
    for (XPathValidator netexValidator : netexValidators) {
      String netexValidatorName = netexValidator.getClass().getName();
      netexValidationProgressCallBack.notifyProgress(
        "Starting validator " + netexValidatorName
      );
      StopWatch netexValidatorStopWatch = new StopWatch();
      netexValidatorStopWatch.start();

      AtomicBoolean netexValidatorComplete = new AtomicBoolean(false);
      notifyProgressAsync(
        netexValidationProgressCallBack,
        netexValidatorName,
        netexValidatorComplete
      );

      try {
        netexValidator.validate(validationReport, xPathValidationContext);
      } finally {
        netexValidatorComplete.set(true);
      }

      netexValidatorStopWatch.stop();
      if (netexValidatorStopWatch.getTime() > 30000) {
        LOGGER.warn(
          "Validator {} for {}/{}/{} completed in {} ms",
          netexValidatorName,
          codespace,
          validationReportId,
          filename,
          netexValidatorStopWatch.getTime()
        );
      } else {
        LOGGER.debug(
          "Validator {} for {}/{}/{} completed in {} ms",
          netexValidatorName,
          codespace,
          validationReportId,
          filename,
          netexValidatorStopWatch.getTime()
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
    for (JAXBValidator netexValidator : jaxbValidators) {
      String netexValidatorName = netexValidator.getClass().getName();
      netexValidationProgressCallBack.notifyProgress(
        "Starting validator " + netexValidatorName
      );
      StopWatch netexValidatorStopWatch = new StopWatch();
      netexValidatorStopWatch.start();

      AtomicBoolean netexValidatorComplete = new AtomicBoolean(false);
      notifyProgressAsync(
        netexValidationProgressCallBack,
        netexValidatorName,
        netexValidatorComplete
      );

      try {
        netexValidator.validate(validationReport, jaxbValidationContext);
      } finally {
        netexValidatorComplete.set(true);
      }

      netexValidatorStopWatch.stop();
      if (netexValidatorStopWatch.getTime() > 30000) {
        LOGGER.warn(
          "Validator {} for {}/{}/{} completed in {} ms",
          netexValidatorName,
          codespace,
          validationReportId,
          filename,
          netexValidatorStopWatch.getTime()
        );
      } else {
        LOGGER.debug(
          "Validator {} for {}/{}/{} completed in {} ms",
          netexValidatorName,
          codespace,
          validationReportId,
          filename,
          netexValidatorStopWatch.getTime()
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
    for (NetexDatasetValidator netexDatasetValidator : netexDatasetValidators) {
      String netexValidatorName = netexDatasetValidator.getClass().getName();
      netexValidationProgressCallBack.notifyProgress(
        "Starting validator " + netexValidatorName
      );
      StopWatch netexValidatorStopWatch = new StopWatch();
      netexValidatorStopWatch.start();

      AtomicBoolean netexValidatorComplete = new AtomicBoolean(false);
      notifyProgressAsync(
        netexValidationProgressCallBack,
        netexValidatorName,
        netexValidatorComplete
      );

      try {
        netexDatasetValidator.validate(validationReport);
      } finally {
        netexValidatorComplete.set(true);
      }

      netexValidatorStopWatch.stop();
      if (netexValidatorStopWatch.getTime() > 30000) {
        LOGGER.warn(
          "Validator {} for {}/{} completed in {} ms",
          netexValidatorName,
          validationReport.getCodespace(),
          validationReport.getValidationReportId(),
          netexValidatorStopWatch.getTime()
        );
      } else {
        LOGGER.debug(
          "Validator {} for {}/{} completed in {} ms",
          netexValidatorName,
          validationReport.getCodespace(),
          validationReport.getValidationReportId(),
          netexValidatorStopWatch.getTime()
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
    return netexValidators
      .stream()
      .map(XPathValidator::getRuleDescriptions)
      .flatMap(Collection::stream)
      .collect(Collectors.toSet());
  }
}

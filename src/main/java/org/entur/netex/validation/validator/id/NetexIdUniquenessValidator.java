package org.entur.netex.validation.validator.id;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.entur.netex.validation.validator.AbstractNetexValidator;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntryFactory;
import org.entur.netex.validation.validator.xpath.ValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verify that NeTEx ids in the current file are not present in one of the files already validated.
 */
public class NetexIdUniquenessValidator extends AbstractNetexValidator {

  static final String RULE_CODE_NETEX_ID_1 = "NETEX_ID_1";
  static final String RULE_CODE_NETEX_ID_10 = "NETEX_ID_10";
  /**
   * Set of NeTEx elements for which id-uniqueness across lines is not verified.
   * These IDs need not be stored.
   */
  private static final Set<String> DEFAULT_IGNORABLE_ELEMENTS = Set.copyOf(
    Arrays.asList(
      "ResourceFrame",
      "SiteFrame",
      "CompositeFrame",
      "TimetableFrame",
      "ServiceFrame",
      "ServiceCalendarFrame",
      "VehicleScheduleFrame",
      "Block",
      "RoutePoint",
      "PointProjection",
      "ScheduledStopPoint",
      "PassengerStopAssignment",
      "NoticeAssignment",
      "ServiceLinkInJourneyPattern"
    )
  );

  private static final String MESSAGE_FORMAT_DUPLICATE_ID_ACROSS_FILES =
    "Duplicate element identifiers across files";
  private static final String MESSAGE_FORMAT_DUPLICATE_ID_ACROSS_COMMON_FILES =
    "Duplicate element identifiers across common files";

  private static final Logger LOGGER = LoggerFactory.getLogger(
    NetexIdUniquenessValidator.class
  );

  private final NetexIdRepository netexIdRepository;
  private final Set<String> ignorableElements;

  public NetexIdUniquenessValidator(
    NetexIdRepository netexIdRepository,
    ValidationReportEntryFactory validationReportEntryFactory
  ) {
    this(
      netexIdRepository,
      validationReportEntryFactory,
      DEFAULT_IGNORABLE_ELEMENTS
    );
  }

  public NetexIdUniquenessValidator(
    NetexIdRepository netexIdRepository,
    ValidationReportEntryFactory validationReportEntryFactory,
    Set<String> ignorableElements
  ) {
    super(validationReportEntryFactory);
    this.netexIdRepository = Objects.requireNonNull(netexIdRepository);
    this.ignorableElements = ignorableElements;
  }

  @Override
  public void validate(
    ValidationReport validationReport,
    ValidationContext validationContext
  ) {
    LOGGER.debug(
      "Validating file {} in report {}",
      validationContext.getFileName(),
      validationReport.getValidationReportId()
    );
    validationReport.addAllValidationReportEntries(
      validate(
        validationReport.getValidationReportId(),
        validationContext.getFileName(),
        validationContext.getLocalIds(),
        validationContext.isCommonFile()
      )
    );
  }

  protected List<ValidationReportEntry> validate(
    String reportId,
    String fileName,
    Set<IdVersion> netexFileLocalIds,
    boolean isCommonFile
  ) {
    List<ValidationReportEntry> validationReportEntries = new ArrayList<>();
    final Map<String, IdVersion> netexIds;
    if (netexFileLocalIds == null) {
      // no ids were stored if the XMLSchema validation failed
      LOGGER.debug("No ids added for file {}", fileName);
      netexIds = Collections.emptyMap();
    } else {
      // collect the subset of NeTEx ids for which duplicate check is performed.
      // if the file contains several times the same id with a different version, only one is kept
      netexIds =
        netexFileLocalIds
          .stream()
          .filter(idVersion ->
            !ignorableElements.contains(idVersion.getElementName())
          )
          .collect(
            Collectors.toMap(
              IdVersion::getId,
              Function.identity(),
              (idVersion, idVersionDuplicate) -> idVersion
            )
          );
    }
    Set<String> duplicateIds = netexIdRepository.getDuplicateNetexIds(
      reportId,
      fileName,
      netexIds.keySet()
    );
    if (!duplicateIds.isEmpty()) {
      if (isCommonFile) {
        for (String id : duplicateIds) {
          DataLocation dataLocation = getIdVersionLocation(netexIds.get(id));
          validationReportEntries.add(
            createValidationReportEntry(
              RULE_CODE_NETEX_ID_10,
              dataLocation,
              MESSAGE_FORMAT_DUPLICATE_ID_ACROSS_COMMON_FILES
            )
          );
        }
      } else {
        for (String id : duplicateIds) {
          DataLocation dataLocation = getIdVersionLocation(netexIds.get(id));
          validationReportEntries.add(
            createValidationReportEntry(
              RULE_CODE_NETEX_ID_1,
              dataLocation,
              MESSAGE_FORMAT_DUPLICATE_ID_ACROSS_FILES
            )
          );
        }
      }
    }
    return validationReportEntries;
  }

  @Override
  public Set<String> getRuleDescriptions() {
    return Set.of(
      createRuleDescription(
        RULE_CODE_NETEX_ID_1,
        MESSAGE_FORMAT_DUPLICATE_ID_ACROSS_FILES
      ),
      createRuleDescription(
        RULE_CODE_NETEX_ID_10,
        MESSAGE_FORMAT_DUPLICATE_ID_ACROSS_COMMON_FILES
      )
    );
  }

  public static Set<String> getDefaultIgnorableElements() {
    return DEFAULT_IGNORABLE_ELEMENTS;
  }
}

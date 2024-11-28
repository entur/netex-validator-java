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
import org.entur.netex.validation.validator.AbstractXPathValidator;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verify that NeTEx ids in the current file are not present in one of the files already validated.
 */
public class NetexIdUniquenessValidator extends AbstractXPathValidator {

  static final ValidationRule RULE_DUPLICATE_ID_ACROSS_FILES =
    new ValidationRule(
      "NETEX_ID_1",
      "NeTEx ID duplicated across files",
      "Duplicate element identifiers across files",
      Severity.ERROR
    );

  static final ValidationRule RULE_DUPLICATE_ID_ACROSS_COMMON_FILES =
    new ValidationRule(
      "NETEX_ID_10",
      " Duplicate NeTEx ID across common files",
      "Duplicate element identifiers across common files",
      Severity.WARNING
    );

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
      "ServiceLinkInJourneyPattern",
      "ServiceFacilitySet",
      "AvailabilityCondition"
    )
  );

  private static final Logger LOGGER = LoggerFactory.getLogger(
    NetexIdUniquenessValidator.class
  );

  private final NetexIdRepository netexIdRepository;
  private final Set<String> ignorableElements;

  public NetexIdUniquenessValidator(NetexIdRepository netexIdRepository) {
    this(netexIdRepository, DEFAULT_IGNORABLE_ELEMENTS);
  }

  public NetexIdUniquenessValidator(
    NetexIdRepository netexIdRepository,
    Set<String> ignorableElements
  ) {
    this.netexIdRepository = Objects.requireNonNull(netexIdRepository);
    this.ignorableElements = ignorableElements;
  }

  @Override
  public List<ValidationIssue> validate(
    XPathValidationContext xPathValidationContext
  ) {
    LOGGER.debug(
      "Validating file {} in report {}",
      xPathValidationContext.getFileName(),
      xPathValidationContext.getValidationReportId()
    );
    return validate(
      xPathValidationContext.getValidationReportId(),
      xPathValidationContext.getFileName(),
      xPathValidationContext.getLocalIds(),
      xPathValidationContext.isCommonFile()
    );
  }

  protected List<ValidationIssue> validate(
    String reportId,
    String fileName,
    Set<IdVersion> netexFileLocalIds,
    boolean isCommonFile
  ) {
    List<ValidationIssue> validationIssues = new ArrayList<>();
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
          validationIssues.add(
            new ValidationIssue(
              RULE_DUPLICATE_ID_ACROSS_COMMON_FILES,
              dataLocation
            )
          );
        }
      } else {
        for (String id : duplicateIds) {
          DataLocation dataLocation = getIdVersionLocation(netexIds.get(id));
          validationIssues.add(
            new ValidationIssue(RULE_DUPLICATE_ID_ACROSS_FILES, dataLocation)
          );
        }
      }
    }
    return validationIssues;
  }

  @Override
  public Set<ValidationRule> getRules() {
    return Set.of(
      RULE_DUPLICATE_ID_ACROSS_FILES,
      RULE_DUPLICATE_ID_ACROSS_COMMON_FILES
    );
  }

  public static Set<String> getDefaultIgnorableElements() {
    return DEFAULT_IGNORABLE_ELEMENTS;
  }
}

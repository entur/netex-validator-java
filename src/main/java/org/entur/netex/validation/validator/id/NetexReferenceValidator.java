package org.entur.netex.validation.validator.id;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.XPathValidator;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validate that references refer to an existing element.
 */
public class NetexReferenceValidator implements XPathValidator {

  static final ValidationRule RULE = new ValidationRule(
    "NETEX_ID_5",
    "NeTEx ID unresolved reference",
    "Unresolved reference to external reference data",
    Severity.ERROR
  );

  private static final Logger LOGGER = LoggerFactory.getLogger(
    NetexReferenceValidator.class
  );

  private final List<ExternalReferenceValidator> externalReferenceValidators;
  private final NetexIdRepository netexIdRepository;

  public NetexReferenceValidator(
    NetexIdRepository netexIdRepository,
    List<ExternalReferenceValidator> externalReferenceValidators
  ) {
    this.netexIdRepository = Objects.requireNonNull(netexIdRepository);
    this.externalReferenceValidators =
      Objects.requireNonNull(externalReferenceValidators);
  }

  @Override
  public List<ValidationIssue> validate(XPathValidationContext xPathValidationContext) {
    LOGGER.debug(
      "Validating file {} in report {}",
      xPathValidationContext.getFileName(),
      xPathValidationContext.getValidationReportId()
    );
    return validate(
      xPathValidationContext.getValidationReportId(),
      xPathValidationContext.getLocalRefs(),
      xPathValidationContext.getLocalIds(),
      xPathValidationContext.isCommonFile()
    );
  }

  protected List<ValidationIssue> validate(
    String reportId,
    List<IdVersion> netexRefs,
    Set<IdVersion> localIds,
    boolean isCommonFile
  ) {
    List<ValidationIssue> validationIssues = new ArrayList<>();

    // Remove duplicates, that is: references that have the same id and version (see #IdVersion.equals)
    Set<IdVersion> possibleExternalReferences = new HashSet<>(netexRefs);
    // Remove references that are found in local ids, comparing by id and version
    possibleExternalReferences.removeAll(localIds);
    if (!possibleExternalReferences.isEmpty()) {
      // Remove references that are found in the common files, comparing only by id, not by id and version
      Set<String> commonIds = netexIdRepository.getSharedNetexIds(reportId);
      possibleExternalReferences.removeIf(ref -> commonIds.contains(ref.getId()));
      if (!possibleExternalReferences.isEmpty()) {
        // Remove references that are valid according to the external id validators
        externalReferenceValidators.forEach(validator ->
          possibleExternalReferences.removeAll(
            validator.validateReferenceIds(possibleExternalReferences)
          )
        );
        if (!possibleExternalReferences.isEmpty()) {
          for (IdVersion id : possibleExternalReferences) {
            LOGGER.debug("Unable to validate external reference {}", id);
            validationIssues.add(new ValidationIssue(RULE, id.dataLocation()));
          }
        }
      }
    }

    if (isCommonFile) {
      netexIdRepository.addSharedNetexIds(reportId, localIds);
    }

    return validationIssues;
  }

  @Override
  public Set<ValidationRule> getRules() {
    return Set.of(RULE);
  }
}

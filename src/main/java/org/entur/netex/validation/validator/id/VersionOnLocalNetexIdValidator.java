package org.entur.netex.validation.validator.id;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.XPathValidator;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validate that local NeTEX IDs have a version attribute.
 */
public class VersionOnLocalNetexIdValidator implements XPathValidator {

  static final ValidationRule RULE = new ValidationRule(
    "NETEX_ID_8",
    "NeTEx ID missing version on elements",
    "Missing version attribute on elements with id attribute",
    Severity.ERROR
  );

  private static final Logger LOGGER = LoggerFactory.getLogger(
    VersionOnLocalNetexIdValidator.class
  );

  @Override
  public List<ValidationIssue> validate(
    XPathValidationContext xPathValidationContext
  ) {
    LOGGER.debug(
      "Validating file {} in report {}",
      xPathValidationContext.getFileName(),
      xPathValidationContext.getValidationReportId()
    );
    return validate(xPathValidationContext.getLocalIds());
  }

  protected List<ValidationIssue> validate(Set<IdVersion> localIds) {
    List<ValidationIssue> validationIssues = new ArrayList<>();
    Set<IdVersion> nonVersionedLocalIds = localIds
      .stream()
      .filter(e -> e.getVersion() == null)
      .collect(Collectors.toSet());
    if (!nonVersionedLocalIds.isEmpty()) {
      for (IdVersion id : nonVersionedLocalIds) {
        DataLocation dataLocation = id.dataLocation();
        validationIssues.add(new ValidationIssue(RULE, dataLocation));
        LOGGER.debug("Id {} does not have version attribute set", id.getId());
      }
    }
    return validationIssues;
  }

  @Override
  public Set<ValidationRule> getRules() {
    return Set.of(RULE);
  }
}

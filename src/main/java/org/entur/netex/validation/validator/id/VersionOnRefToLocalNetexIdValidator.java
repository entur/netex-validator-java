package org.entur.netex.validation.validator.id;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
 * Validate that references to local elements have a version attribute.
 */
public class VersionOnRefToLocalNetexIdValidator
  extends AbstractXPathValidator {

  static final ValidationRule RULE = new ValidationRule(
    "NETEX_ID_9",
    "NeTEx ID missing version on reference",
    "Missing version attribute on reference to local elements",
    Severity.ERROR
  );

  private static final Logger LOGGER = LoggerFactory.getLogger(
    VersionOnRefToLocalNetexIdValidator.class
  );

  @Override
  public List<ValidationIssue> validate(
    XPathValidationContext xPathValidationContext
  ) {
    List<ValidationIssue> validationIssues = new ArrayList<>();
    Set<IdVersion> localIds = xPathValidationContext.getLocalIds();
    List<IdVersion> localRefs = xPathValidationContext.getLocalRefs();

    List<IdVersion> nonVersionedLocalRefs = localRefs
      .stream()
      .filter(e -> e.getVersion() == null)
      .toList();
    Set<String> localIdsWithoutVersion = localIds
      .stream()
      .map(IdVersion::getId)
      .collect(Collectors.toSet());
    for (IdVersion id : nonVersionedLocalRefs) {
      if (localIdsWithoutVersion.contains(id.getId())) {
        DataLocation dataLocation = getIdVersionLocation(id);
        validationIssues.add(new ValidationIssue(RULE, dataLocation));
        LOGGER.debug(
          "Found local reference to {} in line file without use of version-attribute",
          id.getId()
        );
      }
    }
    return validationIssues;
  }

  @Override
  public Set<ValidationRule> getRules() {
    return Set.of(RULE);
  }
}

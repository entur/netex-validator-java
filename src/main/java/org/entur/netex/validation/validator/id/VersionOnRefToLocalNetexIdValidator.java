package org.entur.netex.validation.validator.id;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.entur.netex.validation.validator.AbstractXPathValidator;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntryFactory;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validate that references to local elements have a version attribute.
 */
public class VersionOnRefToLocalNetexIdValidator
  extends AbstractXPathValidator {

  static final String RULE_CODE_NETEX_ID_9 = "NETEX_ID_9";

  private static final Logger LOGGER = LoggerFactory.getLogger(
    VersionOnRefToLocalNetexIdValidator.class
  );

  private static final String MESSAGE_FORMAT_MISSING_VERSION_ON_REF_TO_LOCAL_ID =
    "Missing version attribute on reference to local elements";

  public VersionOnRefToLocalNetexIdValidator(
    ValidationReportEntryFactory validationReportEntryFactory
  ) {
    super(validationReportEntryFactory);
  }

  @Override
  public void validate(
    ValidationReport validationReport,
    XPathValidationContext xPathValidationContext
  ) {
    List<ValidationReportEntry> validationReportEntries = new ArrayList<>();
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
        validationReportEntries.add(
          createValidationReportEntry(
            RULE_CODE_NETEX_ID_9,
            dataLocation,
            MESSAGE_FORMAT_MISSING_VERSION_ON_REF_TO_LOCAL_ID
          )
        );
        LOGGER.debug(
          "Found local reference to {} in line file without use of version-attribute",
          id.getId()
        );
      }
    }
    validationReport.addAllValidationReportEntries(validationReportEntries);
  }

  @Override
  public Set<String> getRuleDescriptions() {
    return Set.of(
      createRuleDescription(
        RULE_CODE_NETEX_ID_9,
        MESSAGE_FORMAT_MISSING_VERSION_ON_REF_TO_LOCAL_ID
      )
    );
  }
}

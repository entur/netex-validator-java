package org.entur.netex.validation.validator.id;

import org.entur.netex.validation.validator.AbstractNetexValidator;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntryFactory;
import org.entur.netex.validation.validator.xpath.ValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validate that local NeTEX IDs have a version attribute.
 */
public class VersionOnLocalNetexIdValidator extends AbstractNetexValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionOnLocalNetexIdValidator.class);

    private static final String MESSAGE_FORMAT_MISSING_VERSION = "Missing version attribute on elements with id attribute";
    private static final String RULE_CODE_NETEX_ID_8 = "NETEX_ID_8";

    public VersionOnLocalNetexIdValidator(ValidationReportEntryFactory validationReportEntryFactory) {
        super(validationReportEntryFactory);
    }

    @Override
    public void validate(ValidationReport validationReport, ValidationContext validationContext) {
        LOGGER.debug("Validating file {} in report {}", validationContext.getFileName(), validationReport.getValidationReportId());
        validationReport.addAllValidationReportEntries(validate(validationContext.getLocalIds()));
    }

    protected List<ValidationReportEntry> validate(Set<IdVersion> localIds) {
        List<ValidationReportEntry> validationReportEntries = new ArrayList<>();
        Set<IdVersion> nonVersionedLocalIds = localIds.stream().filter(e -> e.getVersion() == null).collect(Collectors.toSet());
        if (!nonVersionedLocalIds.isEmpty()) {
            for (IdVersion id : nonVersionedLocalIds) {
                DataLocation dataLocation = getIdVersionLocation(id);
                validationReportEntries.add(createValidationReportEntry(RULE_CODE_NETEX_ID_8, dataLocation, MESSAGE_FORMAT_MISSING_VERSION));
                LOGGER.debug("Id {} does not have version attribute set", id.getId());
            }
        }
        return validationReportEntries;

    }

    @Override
    public Set<String> getRuleDescriptions() {
        return Set.of(createRuleDescription(RULE_CODE_NETEX_ID_8, MESSAGE_FORMAT_MISSING_VERSION));
    }

}

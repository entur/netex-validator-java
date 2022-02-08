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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validate that references refer to an existing element.
 */
public class NeTexReferenceValidator extends AbstractNetexValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeTexReferenceValidator.class);

    private static final String MESSAGE_FORMAT_UNRESOLVED_EXTERNAL_REFERENCE = "Unresolved reference to external reference data";
    private static final String RULE_CODE_NETEX_ID_5 = "NETEX_ID_5";
    private final List<ExternalReferenceValidator> externalReferenceValidators;
    private final NetexIdRepository netexIdRepository;

    public NeTexReferenceValidator(NetexIdRepository netexIdRepository, List<ExternalReferenceValidator> externalReferenceValidators, ValidationReportEntryFactory validationReportEntryFactory) {
        super(validationReportEntryFactory);
        this.netexIdRepository = netexIdRepository;
        this.externalReferenceValidators = externalReferenceValidators;
    }

    @Override
    public void validate(ValidationReport validationReport, ValidationContext validationContext) {
        LOGGER.debug("Validating file {} in report {}", validationContext.getFileName(), validationReport.getValidationReportId());
        validationReport.addAllValidationReportEntries(validate(validationReport.getValidationReportId(), validationContext.getLocalRefs(), validationContext.getLocalIds(), validationContext.isCommonFile()));
    }

    protected List<ValidationReportEntry> validate(String reportId, List<IdVersion> netexRefs, Set<IdVersion> localIds, boolean isCommonFile) {

        List<ValidationReportEntry> validationReportEntries = new ArrayList<>();

        // Remove duplicates, that is: references that have the same id and version (see #IdVersion.equals)
        Set<IdVersion> possibleExternalReferences = new HashSet<>(netexRefs);
        // Remove references that are found in local ids, comparing by id and version
        possibleExternalReferences.removeAll(localIds);
        if (!possibleExternalReferences.isEmpty()) {
            // Remove references that are found in the common files, comparing only by id, not by id and version
            Set<String> commonIds = netexIdRepository.getSharedNetexIds(reportId);
            possibleExternalReferences.removeIf(ref -> commonIds.stream().anyMatch(commonId -> commonId.equals(ref.getId())));
            if (!possibleExternalReferences.isEmpty()) {
                // Remove references that are valid according to the external id validators
                externalReferenceValidators.forEach(validator -> possibleExternalReferences.removeAll(validator.validateReferenceIds(possibleExternalReferences)));
                if (!possibleExternalReferences.isEmpty()) {
                    for (IdVersion id : possibleExternalReferences) {
                        LOGGER.debug("Unable to validate external reference {}", id);
                        validationReportEntries.add(createValidationReportEntry(id));
                    }
                }
            }
        }

        if (isCommonFile) {
            netexIdRepository.addSharedNetexIds(reportId, localIds);
        }

        return validationReportEntries;
    }

    private ValidationReportEntry createValidationReportEntry(IdVersion id) {
        DataLocation dataLocation = getIdVersionLocation(id);
        return createValidationReportEntry(RULE_CODE_NETEX_ID_5, dataLocation, MESSAGE_FORMAT_UNRESOLVED_EXTERNAL_REFERENCE);
    }

    @Override
    public Set<String> getRuleDescriptions() {
        return Set.of(createRuleDescription(RULE_CODE_NETEX_ID_5, MESSAGE_FORMAT_UNRESOLVED_EXTERNAL_REFERENCE));
    }

}

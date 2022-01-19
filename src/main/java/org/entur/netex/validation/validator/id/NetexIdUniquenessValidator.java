package org.entur.netex.validation.validator.id;

import org.entur.netex.validation.validator.NetexValidator;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntrySeverity;
import org.entur.netex.validation.validator.xpath.ValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Verify that NeTEx ids in the current file are not present in one of the files already validated.
 */
public class NetexIdUniquenessValidator implements NetexValidator {

    /**
     * Set of NeTEx elements for which id-uniqueness across lines is not verified.
     * These IDs need not be stored.
     */
    private static final HashSet<String> IGNORABLE_ELEMENTS = new HashSet<>(Arrays.asList("ResourceFrame", "SiteFrame", "CompositeFrame", "TimetableFrame", "ServiceFrame", "ServiceCalendarFrame", "VehicleScheduleFrame", "Block", "RoutePoint", "PointProjection", "ScheduledStopPoint", "PassengerStopAssignment", "NoticeAssignment"));

    private static final String MESSAGE_FORMAT_DUPLICATE_ID_ACROSS_FILES = "Duplicate element identifiers across files";

    private static final Logger LOGGER = LoggerFactory.getLogger(NetexIdUniquenessValidator.class);


    private final NetexIdRepository netexIdRepository;

    public NetexIdUniquenessValidator(NetexIdRepository netexIdRepository) {
        this.netexIdRepository = netexIdRepository;
    }

    @Override
    public void validate(ValidationReport validationReport, ValidationContext validationContext) {
        LOGGER.debug("Validating file {} in report {}", validationContext.getFileName(), validationReport.getValidationReportId());
        validationReport.addAllValidationReportEntries(validate(validationReport.getValidationReportId(), validationContext.getFileName(), validationContext.getLocalIds()));
    }

    protected List<ValidationReportEntry> validate(String reportId, String fileName, Set<IdVersion> netexFileLocalIds) {
        List<ValidationReportEntry> validationReportEntries = new ArrayList<>();
        final Map<String, IdVersion> netexIds;
        if (netexFileLocalIds == null) {
            // no ids were stored if the XMLSchema validation failed
            LOGGER.debug("No ids added for file {}", fileName);
            netexIds = Collections.emptyMap();
        } else {
            // collect the subset of NeTEx ids for which duplicate check is performed.
            // if the file contains several times the same id with a different version, only one is kept
            netexIds = netexFileLocalIds.stream()
                    .filter(idVersion -> !IGNORABLE_ELEMENTS.contains(idVersion.getElementName()))
                    .collect(Collectors.toMap(IdVersion::getId, Function.identity(), (idVersion, idVersionDuplicate) -> idVersion));
        }
        Set<String> duplicateIds = netexIdRepository.getDuplicateNetexIds(reportId, fileName, netexIds.keySet());
        if (!duplicateIds.isEmpty()) {
            for (String id : duplicateIds) {
                String validationReportEntryMessage = getIdVersionLocation(netexIds.get(id)) + MESSAGE_FORMAT_DUPLICATE_ID_ACROSS_FILES;
                validationReportEntries.add(new ValidationReportEntry(validationReportEntryMessage, "NETEX_ID_1", ValidationReportEntrySeverity.ERROR, fileName));
            }
        }
        return validationReportEntries;
    }

    private String getIdVersionLocation(IdVersion id) {
        return "[Line " + id.getLineNumber() + ", Column " + id.getColumnNumber() + ", Id " + id.getId() + "] ";
    }

}

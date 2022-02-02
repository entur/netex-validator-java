package org.entur.netex.validation.validator.id;

import org.entur.netex.validation.validator.AbstractNetexValidator;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntryFactory;
import org.entur.netex.validation.validator.xpath.ValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validate that NeTEX references point to a valid element type.
 */
public class ReferenceToValidEntityTypeValidator extends AbstractNetexValidator {

    private static final String MESSAGE_FORMAT_INVALID_REFERENCE = "Reference to %s is not allowed from element %s. Generally an element named XXXXRef may only reference elements if type XXXX";
    private static final String MESSAGE_FORMAT_INVALID_ID_STRUCTURE = "Invalid id structure on element";

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceToValidEntityTypeValidator.class);


    private final Map<String, Set<String>> allowedSubstitutions;

    public ReferenceToValidEntityTypeValidator(ValidationReportEntryFactory validationReportEntryFactory) {
        super(validationReportEntryFactory);
        this.allowedSubstitutions = getAllowedSubstitutions();
    }

    @Override
    public void validate(ValidationReport validationReport, ValidationContext validationContext) {
        LOGGER.debug("Validating file {} in report {}", validationContext.getFileName(), validationReport.getValidationReportId());
        validationReport.addAllValidationReportEntries(validate(validationContext.getLocalRefs()));
    }

    protected List<ValidationReportEntry> validate(List<IdVersion> localRefs) {
        List<ValidationReportEntry> validationReportEntries = new ArrayList<>();

        for (IdVersion id : localRefs) {
            String referencingElement = id.getElementName();
            String[] idParts = id.getId().split(":");
            if (idParts.length == 3) {
                String referencedElement = idParts[1];
                // The general rule is that an element <XXX> should be referenced from an element <XXXRef> or <DefaultXXXRef>
                // unless it is explicitly listed in the allowed substitutions.
                if (!(referencedElement + "Ref").equals(referencingElement)
                        && !("Default" + referencedElement + "Ref").equals(referencingElement)
                        && !canSubstitute(referencingElement, referencedElement)) {
                    String validationReportEntryMessage = getIdVersionLocation(id) + String.format(MESSAGE_FORMAT_INVALID_REFERENCE, referencedElement, referencingElement);
                    validationReportEntries.add(createValidationReportEntry("NETEX_ID_6",  id.getFilename(), validationReportEntryMessage));
                }
            } else {
                String validationReportEntryMessage = getIdVersionLocation(id) + MESSAGE_FORMAT_INVALID_ID_STRUCTURE;
                validationReportEntries.add(createValidationReportEntry("NETEX_ID_7",  id.getFilename(), validationReportEntryMessage));
            }
        }
        return validationReportEntries;
    }

    private boolean canSubstitute(String referencingElement, String referencedElement) {
        Set<String> possibleSubstitutions = allowedSubstitutions.get(referencingElement);
        return (possibleSubstitutions != null && possibleSubstitutions.contains(referencedElement));
    }

    private Map<String, Set<String>> getAllowedSubstitutions() {
        Map<String, Set<String>> substitutions = new HashMap<>();

        Set<String> groupOfLinesRefSubstitutions = new HashSet<>();
        groupOfLinesRefSubstitutions.add("Network");
        groupOfLinesRefSubstitutions.add("GroupOfLines");
        substitutions.put("RepresentedByGroupRef", groupOfLinesRefSubstitutions);

        Set<String> inverseRouteRefSubstitutions = new HashSet<>();
        inverseRouteRefSubstitutions.add("Route");
        substitutions.put("InverseRouteRef", inverseRouteRefSubstitutions);

        Set<String> projectedPointRefSubstitutions = new HashSet<>();
        projectedPointRefSubstitutions.add("ScheduledStopPoint");
        projectedPointRefSubstitutions.add("RoutePoint");
        projectedPointRefSubstitutions.add("TimingPoint");
        substitutions.put("ProjectToPointRef", projectedPointRefSubstitutions);
        substitutions.put("ProjectedPointRef", projectedPointRefSubstitutions);
        substitutions.put("ToPointRef", projectedPointRefSubstitutions);
        substitutions.put("FromPointRef", projectedPointRefSubstitutions);
        substitutions.put("StartPointRef", projectedPointRefSubstitutions);
        substitutions.put("EndPointRef", projectedPointRefSubstitutions);

        Set<String> noticedObjectRefSubstitutions = new HashSet<>();
        noticedObjectRefSubstitutions.add("Line");
        noticedObjectRefSubstitutions.add("FlexibleLine");
        noticedObjectRefSubstitutions.add("ServiceJourney");
        noticedObjectRefSubstitutions.add("JourneyPattern");
        noticedObjectRefSubstitutions.add("ServiceJourneyPattern");
        noticedObjectRefSubstitutions.add("StopPointInJourneyPattern");
        noticedObjectRefSubstitutions.add("TimetabledPassingTime");
        substitutions.put("NoticedObjectRef", noticedObjectRefSubstitutions);

        Set<String> toAndFromJourneyRefSubstitutions = new HashSet<>();
        toAndFromJourneyRefSubstitutions.add("ServiceJourney");
        toAndFromJourneyRefSubstitutions.add("DatedServiceJourney");
        substitutions.put("ToJourneyRef", toAndFromJourneyRefSubstitutions);
        substitutions.put("FromJourneyRef", toAndFromJourneyRefSubstitutions);

        Set<String> vehicleScheduleJourneyRefSubstitutions = new HashSet<>(toAndFromJourneyRefSubstitutions);
        vehicleScheduleJourneyRefSubstitutions.add("VehicleJourney");
        vehicleScheduleJourneyRefSubstitutions.add("DeadRun");
        substitutions.put("VehicleJourneyRef", vehicleScheduleJourneyRefSubstitutions);

        Set<String> serviceJourneyPatternRefSubstitutions = new HashSet<>();
        serviceJourneyPatternRefSubstitutions.add("ServiceJourneyPattern");
        substitutions.put("JourneyPatternRef", serviceJourneyPatternRefSubstitutions);

        Set<String> lineRefSubstitutions = new HashSet<>();
        lineRefSubstitutions.add("FlexibleLine");
        substitutions.put("LineRef", lineRefSubstitutions);

        Set<String> mainPartRefSubstitutions = new HashSet<>();
        mainPartRefSubstitutions.add("JourneyPart");
        substitutions.put("MainPartRef", mainPartRefSubstitutions);

        Set<String> fromStopPointRefSubstitutions = new HashSet<>();
        fromStopPointRefSubstitutions.add("ScheduledStopPoint");
        substitutions.put("FromStopPointRef", fromStopPointRefSubstitutions);

        Set<String> toStopPointRefSubstitutions = new HashSet<>();
        toStopPointRefSubstitutions.add("ScheduledStopPoint");
        substitutions.put("ToStopPointRef", toStopPointRefSubstitutions);
        return substitutions;
    }

}

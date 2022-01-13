package org.entur.netex.validation.validator.id;

import java.util.HashSet;
import java.util.Set;

/**
 * Validate references to Blocks as valid by default.
 */
public class BlockJourneyReferencesIgnorer implements ExternalReferenceValidator {

    private static final Set<String> JOURNEY_REF_TYPES = Set.of("JourneyRef", "VehicleJourneyRef", "ServiceJourneyRef", "DeadRunRef");

    @Override
    public Set<IdVersion> validateReferenceIds(Set<IdVersion> externalIdsToValidate) {

        // All references of supported type should be returned as validated
        Set<IdVersion> ignoredReferences = new HashSet<>(externalIdsToValidate);
        ignoredReferences.retainAll(isOfSupportedTypes(externalIdsToValidate));

        return ignoredReferences;

    }

    private Set<IdVersion> isOfSupportedTypes(Set<IdVersion> references) {
        Set<IdVersion> supportedTypes = new HashSet<>();
        for (IdVersion ref : references) {
            if (JOURNEY_REF_TYPES.contains(ref.getElementName()) && (ref.getId().contains("DeadRun") || ref.getId().contains("ServiceJourney")) && ref.getParentElementNames().contains("Block")) {
                supportedTypes.add(ref);
            }
        }

        return supportedTypes;
    }
}

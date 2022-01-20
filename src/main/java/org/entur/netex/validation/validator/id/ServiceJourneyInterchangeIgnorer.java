package org.entur.netex.validation.validator.id;

import java.util.HashSet;
import java.util.Set;

/**
 * Mark references to ServiceJourney from an ServiceJourneyInterchange as valid by default.
 */
public class ServiceJourneyInterchangeIgnorer implements ExternalReferenceValidator {

    @Override
    public Set<IdVersion> validateReferenceIds(Set<IdVersion> externalIdsToValidate) {
        Set<IdVersion> ignoredReferences = new HashSet<>(externalIdsToValidate);
        ignoredReferences.retainAll(isOfSupportedTypes(externalIdsToValidate));
        return ignoredReferences;
    }

    private Set<IdVersion> isOfSupportedTypes(Set<IdVersion> references) {
        Set<IdVersion> supportedTypes = new HashSet<>();
        for (IdVersion ref : references) {
            if (("FromPointRef".equals(ref.getElementName()) && ref.getId().contains("ScheduledStopPoint")) || ("FromJourneyRef".equals(ref.getElementName()) && ref.getId().contains("ServiceJourney"))) {
                supportedTypes.add(ref);
            }
        }
        return supportedTypes;
    }
}

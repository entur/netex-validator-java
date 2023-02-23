package org.entur.netex.validation.validator.id;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mark references to ServiceJourney or ScheduledStopPoint from a ServiceJourneyInterchange as valid by default.
 * This can be used to prevent false positive when ServiceJourneyInterchanges refer to elements out of the current PublicationDelivery.
 */
public class ServiceJourneyInterchangeIgnorer implements ExternalReferenceValidator {

    @Override
    public Set<IdVersion> validateReferenceIds(Set<IdVersion> externalIdsToValidate) {
        Objects.requireNonNull(externalIdsToValidate);
        return externalIdsToValidate.stream().filter(ServiceJourneyInterchangeIgnorer::isIgnorableReferenceFromInterchange).collect(Collectors.toUnmodifiableSet());
    }

    private static boolean
    isIgnorableReferenceFromInterchange(IdVersion ref) {
        return ("FromPointRef".equals(ref.getElementName()) && ref.getId().contains("ScheduledStopPoint"))
                || ("FromJourneyRef".equals(ref.getElementName()) && ref.getId().contains("ServiceJourney"));
    }
}

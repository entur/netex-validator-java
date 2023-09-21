package org.entur.netex.validation.validator.id;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mark references from within a Block to a ServiceJourney or a DeadRun as valid by default.
 * This can be used to prevent false positive when Blocks refer to elements out of the current PublicationDelivery.
 */
public class BlockJourneyReferencesIgnorer
  implements ExternalReferenceValidator {

  private static final Set<String> JOURNEY_REF_TYPES = Set.of(
    "JourneyRef",
    "VehicleJourneyRef",
    "ServiceJourneyRef",
    "DeadRunRef"
  );

  @Override
  public Set<IdVersion> validateReferenceIds(
    Set<IdVersion> externalIdsToValidate
  ) {
    Objects.requireNonNull(externalIdsToValidate);
    return externalIdsToValidate
      .stream()
      .filter(BlockJourneyReferencesIgnorer::isIgnorableReferenceFromBlock)
      .collect(Collectors.toUnmodifiableSet());
  }

  private static boolean isIgnorableReferenceFromBlock(IdVersion ref) {
    return (
      JOURNEY_REF_TYPES.contains(ref.getElementName()) &&
      (
        ref.getId().contains("DeadRun") ||
        ref.getId().contains("ServiceJourney")
      ) &&
      (
        ref.getParentElementNames() != null &&
        ref.getParentElementNames().contains("Block")
      )
    );
  }
}

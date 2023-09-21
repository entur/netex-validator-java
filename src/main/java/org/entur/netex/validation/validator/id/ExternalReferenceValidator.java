package org.entur.netex.validation.validator.id;

import java.util.Set;

/**
 * Validate reference to external NeTEx objects, i.e. objects not declared in the current NeTEx file.
 */
public interface ExternalReferenceValidator {
  /**
   * Return a set of IDs that are valid according to this external reference validator.
   *
   * @return the IDs that were validated.
   */
  Set<IdVersion> validateReferenceIds(Set<IdVersion> externalIdsToValidate);
}

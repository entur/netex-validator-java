package org.entur.netex.validation.validator;

/**
 * Validator applied to the whole dataset (not individual files).
 * This includes validators that require data collected from different files within the dataset.
 */
public interface DatasetValidator {
  ValidationReport validate(ValidationReport validationReport);
}

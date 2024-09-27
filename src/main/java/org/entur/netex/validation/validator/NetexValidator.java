package org.entur.netex.validation.validator;

import java.util.Set;

/**
 *  A Validator applied to a NeTEx file.
 */
public interface NetexValidator<V extends ValidationContext> {
  /**
   * Validate NeTEx data provided by the validation context and report issues in the validation report.
   */
  void validate(ValidationReport validationReport, V validationContext);

  /**
   * Return the textual descriptions of the validation rules verified by this validator.
   */
  Set<String> getRuleDescriptions();
}

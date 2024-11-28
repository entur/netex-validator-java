package org.entur.netex.validation.validator;

import java.util.List;
import java.util.Set;

/**
 *  A Validator applied to a NeTEx file.
 */
public interface NetexValidator<V extends ValidationContext> {
  /**
   * Validate NeTEx data provided by the validation context and report issues in the validation report.
   */
  List<ValidationIssue> validate(V validationContext);

  /**
   * Return the  validation rules verified by this validator.
   */
  Set<ValidationRule> getRules();
}

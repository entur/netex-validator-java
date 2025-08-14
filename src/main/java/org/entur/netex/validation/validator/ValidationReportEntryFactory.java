package org.entur.netex.validation.validator;

/**
 * Factory for validation report entries.
 * Concrete implementation may override the default configuration provided by the validation rule.
 */
public interface ValidationReportEntryFactory {
  /**
   * Create a validation report entry for a validation issue.
   * Concrete implementations may override the default configuration provided by the validation rule.
   */
  ValidationReportEntry createValidationReportEntry(ValidationIssue validationIssue);

  /**
   * Create a template validation report entry for a validation rule, for documentation purpose.
   * The data location is empty and the message placeholders are not resolved.
   * Concrete implementations may override the default configuration provided by the validation rule.
   */
  default ValidationReportEntry templateValidationReportEntry(
    ValidationRule validationRule
  ) {
    return new ValidationReportEntry(
      validationRule.message(),
      validationRule.name(),
      validationRule.severity()
    );
  }
}

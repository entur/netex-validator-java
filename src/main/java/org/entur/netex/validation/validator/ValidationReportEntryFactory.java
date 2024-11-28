package org.entur.netex.validation.validator;

/**
 * Create validation entries based on the rule execution result (code and message) and the configuration file (name and severity)
 */
public interface ValidationReportEntryFactory {
  ValidationReportEntry createValidationReportEntry(
    ValidationIssue validationIssue
  );
}

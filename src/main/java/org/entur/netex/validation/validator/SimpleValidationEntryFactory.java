package org.entur.netex.validation.validator;

/**
 * Simple ValidationEntryFactory that .
 */
public class SimpleValidationEntryFactory
  implements ValidationReportEntryFactory {

  @Override
  public ValidationReportEntry createValidationReportEntry(
    ValidationIssue validationIssue
  ) {
    return new ValidationReportEntry(
      validationIssue.message(),
      validationIssue.rule().name(),
      validationIssue.rule().severity(),
      validationIssue.dataLocation()
    );
  }
}

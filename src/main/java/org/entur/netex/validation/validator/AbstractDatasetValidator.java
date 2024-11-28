package org.entur.netex.validation.validator;

import java.util.Objects;

/**
 * Base class for DatasetValidators.
 */
public abstract class AbstractDatasetValidator implements DatasetValidator {

  private final ValidationReportEntryFactory validationReportEntryFactory;

  protected AbstractDatasetValidator(
    ValidationReportEntryFactory validationReportEntryFactory
  ) {
    this.validationReportEntryFactory =
      Objects.requireNonNull(validationReportEntryFactory);
  }

  protected ValidationReportEntry createValidationReportEntry(
    ValidationIssue validationIssue
  ) {
    return validationReportEntryFactory.createValidationReportEntry(
      validationIssue
    );
  }
}

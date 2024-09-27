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
    String code,
    DataLocation dataLocation,
    String validationReportEntryMessage
  ) {
    return validationReportEntryFactory.createValidationReportEntry(
      code,
      validationReportEntryMessage,
      dataLocation
    );
  }
}

package org.entur.netex.validation.validator;

import java.util.Objects;

public abstract class NetexDatasetValidator {

  private final ValidationReportEntryFactory validationReportEntryFactory;

  protected NetexDatasetValidator(
    ValidationReportEntryFactory validationReportEntryFactory
  ) {
    this.validationReportEntryFactory =
      Objects.requireNonNull(validationReportEntryFactory);
  }

  public abstract ValidationReport validate(ValidationReport validationReport);

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

package org.entur.netex.validation.validator;

import java.util.Objects;
import org.entur.netex.validation.validator.id.IdVersion;

/**
 * Base class for NeTEx validators.
 */
public abstract class AbstractNetexValidator implements NetexValidator {

  private final ValidationReportEntryFactory validationReportEntryFactory;

  protected AbstractNetexValidator(
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

  /**
   * Return the location of a NeTEx element in the XML document.
   */
  protected DataLocation getIdVersionLocation(IdVersion id) {
    Objects.requireNonNull(id);
    return new DataLocation(
      id.getId(),
      id.getFilename(),
      id.getLineNumber(),
      id.getColumnNumber()
    );
  }

  /**
   * Create a textual description of the rule.
   */
  protected String createRuleDescription(String code, String message) {
    return '[' + code + "] " + message;
  }
}

package org.entur.netex.validation.validator.jaxb;

import java.util.Objects;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntryFactory;
import org.entur.netex.validation.validator.id.IdVersion;

/**
 * Base class for JAXB-based validators.
 */
public abstract class AbstractJAXBValidator implements JAXBValidator {

  private final ValidationReportEntryFactory validationReportEntryFactory;

  protected AbstractJAXBValidator(
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

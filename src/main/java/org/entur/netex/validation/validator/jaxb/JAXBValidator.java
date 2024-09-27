package org.entur.netex.validation.validator.jaxb;

import org.entur.netex.validation.validator.ValidationReport;

/**
 * Validator using a JAXB representation of the NeTEx data.
 */
public interface JAXBValidator {
  void validate(
    ValidationReport validationReport,
    JAXBValidationContext jaxbValidationContext
  );
}

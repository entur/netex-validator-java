package org.entur.netex.validation.validator.jaxb;

import org.entur.netex.validation.validator.ValidationReport;

public interface JAXBValidator {
  void validate(
    ValidationReport validationReport,
    JAXBValidationContext jaxbValidationContext
  );
}

package org.entur.netex.validation.validator.jaxb;

/**
 * Collect NeTEx data while validating a NeTEx file.
 * This is used for validation rules that require data across several files.
 */
public interface NetexDataCollector {
  void collect(JAXBValidationContext validationContext);
}

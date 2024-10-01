package org.entur.netex.validation.validator.jaxb;

/**
 * Collect NeTEx data while validating a NeTEx file.
 * This is used for validation rules that require data across several files.
 */
public abstract class NetexDataCollector {

  public final void collect(JAXBValidationContext validationContext) {
    if (validationContext.isCommonFile()) {
      collectDataFromCommonFile(validationContext);
    } else {
      collectDataFromLineFile(validationContext);
    }
  }

  protected abstract void collectDataFromLineFile(
    JAXBValidationContext validationContext
  );

  protected abstract void collectDataFromCommonFile(
    JAXBValidationContext validationContext
  );
}

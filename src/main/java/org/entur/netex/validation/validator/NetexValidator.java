package org.entur.netex.validation.validator;

import org.entur.netex.validation.validator.xpath.ValidationContext;

/**
 * A validator that updates a {@link ValidationReport} according to a {@link ValidationContext}.
 */
public interface NetexValidator {

    void validate(ValidationReport validationReport, ValidationContext validationContext);
}

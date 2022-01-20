package org.entur.netex.validation.validator;

import org.entur.netex.validation.validator.xpath.ValidationContext;

/**
 * A validator that updates a {@link ValidationReport} according to a {@link ValidationContext}.
 */
public interface NetexValidator {

    /**
     * Validate the document specified in the {@link ValidationContext} and update the given {@link ValidationReport}
     * @param validationReport the validation report to be updated.
     * @param validationContext the current validation context.
     */
    void validate(ValidationReport validationReport, ValidationContext validationContext);
}

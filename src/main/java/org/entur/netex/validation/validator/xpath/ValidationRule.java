package org.entur.netex.validation.validator.xpath;

import org.entur.netex.validation.validator.ValidationReportEntry;

import java.util.List;

/**
 * A single XPath validation rule that produces a list of {@link ValidationReportEntry}.
 */
public interface ValidationRule {

    /**
     * Validate a NeTEx document or document part.
     *
     * @param validationContext the current validation context.
     * @return a list of validation entries for the current context.
     */
    List<XPathValidationReportEntry> validate(XPathValidationContext validationContext);

    /**
     * The name of the validation rule.
     *
     * @return the name of the validation rule.
     */
    String getCode();

    /**
     * The context specific validation message
     *
     * @return the context-specific validation message
     */
    String getMessage();
}

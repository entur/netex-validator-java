package org.entur.netex.validation.validator;

import org.entur.netex.validation.validator.id.IdVersion;

/**
 * Base class for NeTEx validators.
 */
public abstract class AbstractNetexValidator implements NetexValidator {

    private final ValidationReportEntryFactory validationReportEntryFactory;

    protected AbstractNetexValidator(ValidationReportEntryFactory validationReportEntryFactory) {
        this.validationReportEntryFactory = validationReportEntryFactory;
    }


    protected ValidationReportEntry createValidationReportEntry(String code, String fileName, String validationReportEntryMessage) {
        return validationReportEntryFactory.createValidationReportEntry(code, validationReportEntryMessage, fileName);
    }

    /**
     * Return the location of a NeTEx element in the XML document.
     * @param id
     * @return
     */
    protected String getIdVersionLocation(IdVersion id) {
        return "[Line " + id.getLineNumber() + ", Column " + id.getColumnNumber() + ", Id " + id.getId() + "] ";
    }

    /**
     * Create a textual description of the rule.
     * @param code
     * @param message
     * @return a textual description of the rule
     */
    protected String createRuleDescription(String code, String message) {
        return '[' + code + "] " + message;
    }
}

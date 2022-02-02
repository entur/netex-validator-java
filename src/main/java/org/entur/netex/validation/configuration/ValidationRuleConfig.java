package org.entur.netex.validation.configuration;

import org.entur.netex.validation.validator.ValidationReportEntrySeverity;

/**
 * An element in the validation rule configuration file.
 */
public class ValidationRuleConfig {


    private String code;
    private String name;
    private ValidationReportEntrySeverity severity;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ValidationReportEntrySeverity getSeverity() {
        return severity;
    }

    public void setSeverity(ValidationReportEntrySeverity severity) {
        this.severity = severity;
    }

}

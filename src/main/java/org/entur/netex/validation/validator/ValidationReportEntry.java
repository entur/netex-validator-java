package org.entur.netex.validation.validator;

/**
 * Single entry in a validation report.
 */
public class ValidationReportEntry {

    private String name;
    private String message;
    private ValidationReportEntrySeverity severity;
    private String fileName;

    public ValidationReportEntry() {
    }

    public ValidationReportEntry(String message, String name, ValidationReportEntrySeverity severity) {
        this(message, name, severity, "");
    }

    public ValidationReportEntry(String message, String name, ValidationReportEntrySeverity severity, String fileName) {
        this.message = message;
        this.name = name;
        this.severity = severity;
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public ValidationReportEntrySeverity getSeverity() {
        return severity;
    }

    public String getFileName() {
        return fileName;
    }
}

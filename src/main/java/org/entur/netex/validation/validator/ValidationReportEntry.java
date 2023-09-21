package org.entur.netex.validation.validator;

/**
 * Single entry in a validation report.
 */
public class ValidationReportEntry {

  private String name;
  private String message;
  private ValidationReportEntrySeverity severity;
  private String objectId;
  private String fileName;
  private Integer lineNumber;

  private Integer columnNumber;

  public ValidationReportEntry() {}

  public ValidationReportEntry(
    String message,
    String name,
    ValidationReportEntrySeverity severity
  ) {
    this(message, name, severity, DataLocation.EMPTY_LOCATION);
  }

  public ValidationReportEntry(
    String message,
    String name,
    ValidationReportEntrySeverity severity,
    DataLocation dataLocation
  ) {
    this.message = message;
    this.name = name;
    this.severity = severity;
    this.objectId = dataLocation.getObjectId();
    this.fileName = dataLocation.getFileName();
    this.lineNumber = dataLocation.getLineNumber();
    this.columnNumber = dataLocation.getColumNumber();
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

  public String getObjectId() {
    return objectId;
  }

  public Integer getLineNumber() {
    return lineNumber;
  }

  public Integer getColumnNumber() {
    return columnNumber;
  }
}

package org.entur.netex.validation.validator;

import java.util.List;

/**
 * Event published when the execution of a single validator is complete.
 */
public class ValidationCompleteEvent {

  private final ValidationContext validationContext;
  private final String validationReportId;
  private final List<ValidationReportEntry> validationReportEntries;

  public <C extends ValidationContext> ValidationCompleteEvent(
    C validationContext,
    String validationReportId,
    List<ValidationReportEntry> validationReportEntries
  ) {
    this.validationContext = validationContext;
    this.validationReportId = validationReportId;
    this.validationReportEntries = validationReportEntries;
  }

  /**
   * Return true if at least one error or critical issue is reported.
   */
  public boolean hasError() {
    return validationReportEntries
      .stream()
      .anyMatch(validationReportEntry ->
        validationReportEntry.getSeverity().isErrorOrCritical()
      );
  }

  public ValidationContext validationContext() {
    return validationContext;
  }

  public String validationReportId() {
    return validationReportId;
  }
}

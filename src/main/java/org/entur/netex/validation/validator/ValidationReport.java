package org.entur.netex.validation.validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * NeTEx validation report.
 */
public class ValidationReport {

    private String codespace;
    private String validationReportId;
    private LocalDateTime creationDate;
    private Map<String, Long> numberOfValidationEntriesPerRule;
    private Collection<ValidationReportEntry> validationReportEntries;

    public ValidationReport() {
    }

    public ValidationReport(String codespace, String validationReportId) {
        this.codespace = codespace;
        this.validationReportId = validationReportId;
        this.creationDate = LocalDateTime.now();
        this.validationReportEntries = new ArrayList<>();
        this.numberOfValidationEntriesPerRule = new HashMap<>();
    }

    public ValidationReport(String codespace,
                            String validationReportId,
                            Collection<ValidationReportEntry> validationReportEntries) {
        this.codespace = codespace;
        this.validationReportId = validationReportId;
        this.creationDate = LocalDateTime.now();
        this.validationReportEntries = new ArrayList<>(validationReportEntries);
        this.numberOfValidationEntriesPerRule = new HashMap<>();
        this.validationReportEntries.forEach(this::addValidationReportEntryToMap);
    }

    public ValidationReport(String codespace,
                            String validationReportId,
                            Collection<ValidationReportEntry> validationReportEntries,
                            Map<String, Long> numberOfValidationEntriesPerRule) {
        this.codespace = codespace;
        this.validationReportId = validationReportId;
        this.creationDate = LocalDateTime.now();
        this.validationReportEntries = new ArrayList<>(validationReportEntries);
        this.numberOfValidationEntriesPerRule = new HashMap<>(numberOfValidationEntriesPerRule);
    }

    public boolean hasError() {
        return validationReportEntries.stream()
                .anyMatch(validationReportEntry -> ValidationReportEntrySeverity.ERROR == validationReportEntry.getSeverity() || ValidationReportEntrySeverity.CRITICAL == validationReportEntry.getSeverity());
    }

    public void addValidationReportEntry(ValidationReportEntry validationReportEntry) {
        this.validationReportEntries.add(validationReportEntry);
        addValidationReportEntryToMap(validationReportEntry);
    }

    public void addAllValidationReportEntries(Collection<ValidationReportEntry> validationReportEntries) {
        this.validationReportEntries.addAll(validationReportEntries);
        validationReportEntries.forEach(this::addValidationReportEntryToMap);
    }

    private void addValidationReportEntryToMap(ValidationReportEntry validationReportEntry) {
        numberOfValidationEntriesPerRule.merge(validationReportEntry.getName(), 1L, Long::sum);
    }

    public Collection<ValidationReportEntry> getValidationReportEntries() {
        return validationReportEntries;
    }

    public Map<String, Long> getNumberOfValidationEntriesPerRule() {
        return numberOfValidationEntriesPerRule;
    }

    public String getCodespace() {
        return codespace;
    }

    public String getValidationReportId() {
        return validationReportId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}

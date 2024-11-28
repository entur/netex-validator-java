package org.entur.netex.validation.validator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ValidationReportTest {

  @Test
  void addValidationReportEntry() {
    ValidationReport validationReport = new ValidationReport("TST", "TST01");
    validationReport.addValidationReportEntry(
      new ValidationReportEntry("", "Rule1", Severity.WARNING)
    );
    validationReport.addValidationReportEntry(
      new ValidationReportEntry("", "Rule2", Severity.ERROR)
    );
    validationReport.addValidationReportEntry(
      new ValidationReportEntry("", "Rule1", Severity.WARNING)
    );
    validationReport.addValidationReportEntry(
      new ValidationReportEntry("", "Rule3", Severity.WARNING)
    );

    Map<String, Long> numberOfValidationEntriesPerRule =
      validationReport.getNumberOfValidationEntriesPerRule();

    assertEquals(2, numberOfValidationEntriesPerRule.get("Rule1"));
    assertEquals(1, numberOfValidationEntriesPerRule.get("Rule2"));
    assertEquals(1, numberOfValidationEntriesPerRule.get("Rule3"));
  }

  @Test
  void addAllValidationReportEntries() {
    ValidationReport validationReport = new ValidationReport("TST", "TST01");
    validationReport.addAllValidationReportEntries(
      List.of(
        new ValidationReportEntry("", "Rule1", Severity.WARNING),
        new ValidationReportEntry("", "Rule2", Severity.ERROR),
        new ValidationReportEntry("", "Rule1", Severity.WARNING),
        new ValidationReportEntry("", "Rule3", Severity.WARNING)
      )
    );

    Map<String, Long> numberOfValidationEntriesPerRule =
      validationReport.getNumberOfValidationEntriesPerRule();

    assertEquals(2, numberOfValidationEntriesPerRule.get("Rule1"));
    assertEquals(1, numberOfValidationEntriesPerRule.get("Rule2"));
    assertEquals(1, numberOfValidationEntriesPerRule.get("Rule3"));
  }

  @Test
  void shouldCorrectlyConstructWithGivenValidationReportEntries() {
    ValidationReport validationReport = new ValidationReport(
      "TST",
      "TST01",
      List.of(
        new ValidationReportEntry("", "Rule1", Severity.WARNING),
        new ValidationReportEntry("", "Rule2", Severity.ERROR),
        new ValidationReportEntry("", "Rule1", Severity.WARNING),
        new ValidationReportEntry("", "Rule3", Severity.WARNING)
      )
    );

    Map<String, Long> numberOfValidationEntriesPerRule =
      validationReport.getNumberOfValidationEntriesPerRule();

    assertEquals(2, numberOfValidationEntriesPerRule.get("Rule1"));
    assertEquals(1, numberOfValidationEntriesPerRule.get("Rule2"));
    assertEquals(1, numberOfValidationEntriesPerRule.get("Rule3"));
  }

  @Test
  void shouldBePossibleToHaveDifferentNumberOfEntriesAndActualEntries() {
    ValidationReport validationReport = new ValidationReport(
      "TST",
      "TST01",
      List.of(
        new ValidationReportEntry("", "Rule1", Severity.WARNING),
        new ValidationReportEntry("", "Rule2", Severity.ERROR),
        new ValidationReportEntry("", "Rule1", Severity.WARNING),
        new ValidationReportEntry("", "Rule3", Severity.WARNING)
      ),
      Map.of("Rule1", 1L, "Rule2", 2L)
    );

    Map<String, Long> numberOfValidationEntriesPerRule =
      validationReport.getNumberOfValidationEntriesPerRule();

    assertEquals(4, validationReport.getValidationReportEntries().size());
    assertEquals(1L, numberOfValidationEntriesPerRule.get("Rule1"));
    assertEquals(2L, numberOfValidationEntriesPerRule.get("Rule2"));
  }
}

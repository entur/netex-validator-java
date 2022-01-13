package org.entur.netex.validation.validator.xpath;

import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntrySeverity;

import java.util.List;

/**
 * A single XPath validation rule that produces a list of {@link ValidationReportEntry}.
 */
public interface ValidationRule {

    List<ValidationReportEntry> validate(XPathValidationContext validationContext) ;

    String getMessage();

    String getName();

    ValidationReportEntrySeverity getSeverity();
}

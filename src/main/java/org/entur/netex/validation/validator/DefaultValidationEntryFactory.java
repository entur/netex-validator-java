package org.entur.netex.validation.validator;

import org.entur.netex.validation.configuration.ValidationConfigLoader;
import org.entur.netex.validation.configuration.ValidationRuleConfig;

/**
 * Default implementation of the validation report entry factory.
 * The name, message and severity are retrieved from the validation configuration file and override the default name,
 * message and severity of the validation rule.
 */
public class DefaultValidationEntryFactory implements ValidationReportEntryFactory {

  private final ValidationConfigLoader validationConfigLoader;

  public DefaultValidationEntryFactory(ValidationConfigLoader validationConfigLoader) {
    this.validationConfigLoader = validationConfigLoader;
  }

  @Override
  public ValidationReportEntry createValidationReportEntry(
    ValidationIssue validationIssue
  ) {
    ValidationRuleConfig validationRuleConfig = validationConfigLoader
      .getValidationRuleConfigs()
      .get(validationIssue.rule().code());
    if (validationRuleConfig == null) {
      return new ValidationReportEntry(
        validationIssue.message(),
        validationIssue.rule().name(),
        validationIssue.rule().severity(),
        validationIssue.dataLocation()
      );
    }

    String message = validationRuleConfig.getMessage() != null
      ? validationRuleConfig.getMessage().formatted(validationIssue.arguments())
      : validationIssue.message();
    String name = validationRuleConfig.getName() != null
      ? validationRuleConfig.getName()
      : validationIssue.rule().name();
    Severity severity = validationRuleConfig.getSeverity() != null
      ? validationRuleConfig.getSeverity()
      : validationIssue.rule().severity();

    return new ValidationReportEntry(
      message,
      name,
      severity,
      validationIssue.dataLocation()
    );
  }

  @Override
  public ValidationReportEntry templateValidationReportEntry(
    ValidationRule validationRule
  ) {
    ValidationRuleConfig validationRuleConfig = validationConfigLoader
      .getValidationRuleConfigs()
      .get(validationRule.code());
    if (validationRuleConfig == null) {
      return ValidationReportEntryFactory.super.templateValidationReportEntry(
        validationRule
      );
    }

    String message = validationRuleConfig.getMessage() != null
      ? validationRuleConfig.getMessage()
      : validationRule.message();
    String name = validationRuleConfig.getName() != null
      ? validationRuleConfig.getName()
      : validationRule.name();
    Severity severity = validationRuleConfig.getSeverity() != null
      ? validationRuleConfig.getSeverity()
      : validationRule.severity();

    return new ValidationReportEntry(message, name, severity);
  }
}

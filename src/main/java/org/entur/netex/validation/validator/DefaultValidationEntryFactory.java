package org.entur.netex.validation.validator;

import org.entur.netex.validation.configuration.ValidationConfigLoader;
import org.entur.netex.validation.configuration.ValidationRuleConfig;
import org.entur.netex.validation.exception.NetexValidationException;

/**
 * Default implementation of the validation report entry factory.
 * The entry name and severity are retrieved from the validation configuration file.
 */
public class DefaultValidationEntryFactory implements ValidationReportEntryFactory {

    private final ValidationConfigLoader validationConfigLoader;

    public DefaultValidationEntryFactory(ValidationConfigLoader validationConfigLoader) {
        this.validationConfigLoader = validationConfigLoader;
    }

    @Override
    public ValidationReportEntry createValidationReportEntry(String code, String validationReportEntryMessage, String fileName) {
        ValidationRuleConfig validationRuleConfig = validationConfigLoader.getValidationRuleConfigs().get(code);
        if(validationRuleConfig == null) {
            throw new NetexValidationException("Configuration not found for rule " + code);
        }
        return new ValidationReportEntry(validationReportEntryMessage, validationRuleConfig.getName(), validationRuleConfig.getSeverity(), fileName);
    }
}

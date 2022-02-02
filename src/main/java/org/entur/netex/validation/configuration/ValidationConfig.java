package org.entur.netex.validation.configuration;

import java.util.List;

/**
 * Containder for validation rule configuration entries.
 */
public class ValidationConfig {

    private List<ValidationRuleConfig> validationRuleConfigs;
    
    public List<ValidationRuleConfig> getValidationRuleConfigs() {
        return validationRuleConfigs;
    }

    public void setValidationRuleConfigs(List<ValidationRuleConfig> validationRuleConfigs) {
        this.validationRuleConfigs = validationRuleConfigs;
    }
    
}

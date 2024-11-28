package org.entur.netex.validation.configuration;

import java.util.Map;

/**
 * Load the validation rules configuration.
 */
public interface ValidationConfigLoader {
  /**
   * Return a mapping of rule configuration by rule code.
   */
  Map<String, ValidationRuleConfig> getValidationRuleConfigs();

  /**
   * Return a validation rule configuration for a given rule code.
   */
  default ValidationRuleConfig getValidationRuleConfig(String ruleCode) {
    return getValidationRuleConfigs().get(ruleCode);
  }
}

package org.entur.netex.validation.configuration;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Default implementation of the validation configuration loader that loads configurations from a hierarchy of yaml files.
 */
public class DefaultValidationConfigLoader implements ValidationConfigLoader {

  private static final String DEFAULT_VALIDATION_CONFIG_FILE =
    "configuration.default.yaml";

  private final Map<String, ValidationRuleConfig> validationRuleConfigs;

  public DefaultValidationConfigLoader() {
    this(Collections.emptyList());
  }

  public DefaultValidationConfigLoader(String configurationFile) {
    this(List.of(configurationFile));
  }

  public DefaultValidationConfigLoader(List<String> configurationFiles) {
    this.validationRuleConfigs =
      loadConfigurationFile(DEFAULT_VALIDATION_CONFIG_FILE);
    for (String configurationFile : configurationFiles) {
      this.validationRuleConfigs.putAll(
          loadConfigurationFile(configurationFile)
        );
    }
  }

  private Map<String, ValidationRuleConfig> loadConfigurationFile(
    String configurationFile
  ) {
    InputStream inputStream = Thread
      .currentThread()
      .getContextClassLoader()
      .getResourceAsStream(configurationFile);
    Yaml yaml = new Yaml(new Constructor(ValidationConfig.class));
    ValidationConfig validationConfig = yaml.load(inputStream);
    return validationConfig
      .getValidationRuleConfigs()
      .stream()
      .collect(
        Collectors.toMap(ValidationRuleConfig::getCode, Function.identity())
      );
  }

  @Override
  public Map<String, ValidationRuleConfig> getValidationRuleConfigs() {
    return validationRuleConfigs;
  }

  @Override
  public ValidationRuleConfig getValidationRuleConfig(String ruleCode) {
    return validationRuleConfigs.get(ruleCode);
  }
}

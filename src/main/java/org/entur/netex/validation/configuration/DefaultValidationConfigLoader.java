package org.entur.netex.validation.configuration;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Default implementation of the validation configuration loader that loads configurations from a hierarchy of yaml files.
 */
public class DefaultValidationConfigLoader implements ValidationConfigLoader {

    private static final String DEFAULT_VALIDATION_CONFIG_FILE = "configuration.default.yaml";

    private final Map<String, ValidationRuleConfig> validationRuleConfigs;

    public DefaultValidationConfigLoader() {
        this(null);
    }

    public DefaultValidationConfigLoader(String configurationFile) {
        this.validationRuleConfigs = loadConfigurationFile(DEFAULT_VALIDATION_CONFIG_FILE);
        if (configurationFile != null) {
            this.validationRuleConfigs.putAll(loadConfigurationFile(configurationFile));
        }
    }

    private Map<String, ValidationRuleConfig> loadConfigurationFile(String configurationFile) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configurationFile);
        Yaml yaml = new Yaml(new Constructor(ValidationConfig.class));
        ValidationConfig validationConfig = yaml.load(inputStream);
        return validationConfig.getValidationRuleConfigs().stream().collect(Collectors.toMap(ValidationRuleConfig::getCode, Function.identity()));
    }

    @Override
    public Map<String, ValidationRuleConfig> getValidationRuleConfigs() {
        return validationRuleConfigs;
    }

}

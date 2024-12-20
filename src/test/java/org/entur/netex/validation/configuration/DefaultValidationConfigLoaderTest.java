package org.entur.netex.validation.configuration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.Severity;
import org.junit.jupiter.api.Test;

class DefaultValidationConfigLoaderTest {

  private static final String RULE_CODE = "RULE_1";
  private static final String CONFIGURATION_FILE_NAME =
    "configuration.test.yaml";
  private static final String CONFIGURATION_FILE_NAME_OVERLOAD =
    "configuration.test.overloaded.yaml";

  @Test
  void loadConfiguration() {
    DefaultValidationConfigLoader loader = new DefaultValidationConfigLoader(
      CONFIGURATION_FILE_NAME
    );
    ValidationRuleConfig validationRuleConfig = loader.getValidationRuleConfig(
      RULE_CODE
    );
    assertNotNull(validationRuleConfig);
    assertEquals(Severity.ERROR, validationRuleConfig.getSeverity());
  }

  @Test
  void loadConfigurationNonExistingFile() {
    assertThrows(
      NetexValidationException.class,
      () -> new DefaultValidationConfigLoader("missing.yaml")
    );
  }

  @Test
  void loadOverloadedConfiguration() {
    DefaultValidationConfigLoader loader = new DefaultValidationConfigLoader(
      List.of(CONFIGURATION_FILE_NAME, CONFIGURATION_FILE_NAME_OVERLOAD)
    );
    ValidationRuleConfig validationRuleConfig = loader.getValidationRuleConfig(
      RULE_CODE
    );
    assertNotNull(validationRuleConfig);
    assertEquals(Severity.WARNING, validationRuleConfig.getSeverity());
  }
}

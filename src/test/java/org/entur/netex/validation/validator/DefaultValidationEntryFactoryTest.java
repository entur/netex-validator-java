package org.entur.netex.validation.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.entur.netex.validation.configuration.ValidationConfigLoader;
import org.entur.netex.validation.configuration.ValidationRuleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultValidationEntryFactoryTest {

  public static final String LINE_ID = "ENT:Line:1";
  public static final String RULE_CODE = "LINE_NAME";

  public static final String ORIGINAL_RULE_NAME = "A line must have a name";
  public static final String ORIGINAL_RULE_MESSAGE =
    "Line %s does not have a name";
  public static final Severity ORIGINAL_RULE_SEVERITY = Severity.ERROR;

  public static final String OVERRIDDEN_RULE_NAME =
    "Une ligne doit avoir un nom";
  public static final String OVERRIDDEN_RULE_MESSAGE =
    "La ligne %s n'a pas de nom";
  private static final Severity OVERRIDEN_RULE_SEVERITY = Severity.WARNING;

  private ValidationIssue validationIssue;

  @BeforeEach
  void setUp() {
    ValidationRule validationRule = new ValidationRule(
      RULE_CODE,
      ORIGINAL_RULE_NAME,
      ORIGINAL_RULE_MESSAGE,
      ORIGINAL_RULE_SEVERITY
    );
    DataLocation dataLocation = new DataLocation(LINE_ID, "netex.xml", 1, 2);
    validationIssue =
      new ValidationIssue(validationRule, dataLocation, LINE_ID);
  }

  /**
   * When the overriding configuration is missing, the report entry uses the default rule configuration.
   */
  @Test
  void testMissingConfiguration() {
    ValidationConfigLoader validationConfigLoader = getValidationConfigLoader(
      Set.of()
    );
    ValidationReportEntryFactory factory = new DefaultValidationEntryFactory(
      validationConfigLoader
    );
    ValidationReportEntry validationReportEntry =
      factory.createValidationReportEntry(validationIssue);
    assertNotNull(validationReportEntry);
    assertEquals(ORIGINAL_RULE_NAME, validationReportEntry.getName());
    assertEquals(
      "Line " + LINE_ID + " does not have a name",
      validationReportEntry.getMessage()
    );
    assertEquals(ORIGINAL_RULE_SEVERITY, validationReportEntry.getSeverity());
  }

  /**
   * When the overriding configuration is present, the report entry uses the overridden rule configuration.
   */
  @Test
  void testValidConfiguration() {
    ValidationRuleConfig config = new ValidationRuleConfig();
    config.setCode(RULE_CODE);
    config.setName(OVERRIDDEN_RULE_NAME);
    config.setMessage(OVERRIDDEN_RULE_MESSAGE);
    config.setSeverity(OVERRIDEN_RULE_SEVERITY);

    ValidationConfigLoader validationConfigLoader = getValidationConfigLoader(
      Set.of(config)
    );
    ValidationReportEntryFactory factory = new DefaultValidationEntryFactory(
      validationConfigLoader
    );
    ValidationReportEntry validationReportEntry =
      factory.createValidationReportEntry(validationIssue);
    assertNotNull(validationReportEntry);
    assertEquals(OVERRIDDEN_RULE_NAME, validationReportEntry.getName());
    assertEquals(
      "La ligne " + LINE_ID + " n'a pas de nom",
      validationReportEntry.getMessage()
    );
    assertEquals(OVERRIDEN_RULE_SEVERITY, validationReportEntry.getSeverity());
  }

  private static ValidationConfigLoader getValidationConfigLoader(
    Set<ValidationRuleConfig> configs
  ) {
    return () ->
      configs
        .stream()
        .collect(
          Collectors.toUnmodifiableMap(
            ValidationRuleConfig::getCode,
            Function.identity()
          )
        );
  }
}

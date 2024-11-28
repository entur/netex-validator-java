package org.entur.netex.validation.validator;

/**
 * A validation rule, identified by a unique code.
 * The validation rule has a default name, message and severity that can be overridden by configuration.
 */
public class ValidationRule {

  private final String code;
  private final String message;
  private final String name;
  private final Severity severity;

  public ValidationRule(String code, String name, Severity severity) {
    this(code, name, name, severity);
  }

  public ValidationRule(
    String code,
    String name,
    String message,
    Severity severity
  ) {
    this.code = code;
    this.name = name;
    this.message = message;
    this.severity = severity;
  }

  /**
   * The unique code of the validation rule.
   * The code is not meant to be displayed in the validation report.
   */
  public String code() {
    return code;
  }

  /**
   * A default descriptive name for this validation rule.
   * Can be overridden by configuration.
   */
  public String name() {
    return name;
  }

  /**
   * A default message for this validation rule.
   * The message can contain placeholders that follows the String.format() conventions.
   * Can be overridden by configuration.
   */
  public String message() {
    return message;
  }

  /**
   * A default severity for this validation rule.
   * Can be overridden by configuration.
   */
  public Severity severity() {
    return severity;
  }
}

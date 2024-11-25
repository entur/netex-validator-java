package org.entur.netex.validation.validator;

/**
 * An issue reported by a validator.
 */
public class ValidationIssue {

  private static final Object[] NO_ARGS = new Object[0];

  private final ValidationRule rule;
  private final DataLocation dataLocation;
  private final Object[] arguments;

  public ValidationIssue(ValidationRule rule, DataLocation dataLocation) {
    this(rule, dataLocation, NO_ARGS);
  }

  public ValidationIssue(
    ValidationRule rule,
    DataLocation dataLocation,
    Object... arguments
  ) {
    this.rule = rule;
    this.dataLocation = dataLocation;
    this.arguments = arguments;
  }

  public ValidationRule rule() {
    return rule;
  }

  public DataLocation dataLocation() {
    return dataLocation;
  }

  /**
   * arguments corresponding to the placeholders in the rule message.
   */
  public Object[] arguments() {
    return arguments;
  }

  /**
   * Return the rule message with the placeholders replaced by the arguments.
   */
  public String message() {
    return rule.message().formatted(arguments);
  }
}

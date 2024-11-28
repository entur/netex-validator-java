package org.entur.netex.validation.configuration;

import org.entur.netex.validation.validator.Severity;

/**
 * An element in the validation rule configuration file.
 */
public class ValidationRuleConfig {

  private String code;
  private String name;
  private String message;
  private Severity severity;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity severity) {
    this.severity = severity;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

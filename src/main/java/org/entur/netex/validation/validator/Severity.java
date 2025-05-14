package org.entur.netex.validation.validator;

/**
 * Severity of a validation issues.
 */
public enum Severity {
  INFO,
  WARNING,
  ERROR,
  CRITICAL;

  public boolean isErrorOrCritical() {
    return this == Severity.ERROR || this == Severity.CRITICAL;
  }
}

package org.entur.netex.validation.exception;

public class RetryableNetexValidationException
  extends NetexValidationException {

  public RetryableNetexValidationException(Throwable t) {
    super(t);
  }
}

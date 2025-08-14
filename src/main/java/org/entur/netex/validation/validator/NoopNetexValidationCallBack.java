package org.entur.netex.validation.validator;

/**
 * No-op Validation callback.
 */
public class NoopNetexValidationCallBack implements NetexValidationProgressCallBack {

  @Override
  public void notifyProgress(String message) {
    // NOOP
  }
}

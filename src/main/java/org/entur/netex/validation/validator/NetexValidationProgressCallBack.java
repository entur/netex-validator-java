package org.entur.netex.validation.validator;

/**
 * Callback to report back the validation progress to the validation client.
 */
public interface NetexValidationProgressCallBack {
  /**
   * Notify the validation progress
   * @param message progress message.
   */
  void notifyProgress(String message);

  /**
   * Notify that validation is complete for a single validator.
   */
  default void notifyValidationComplete(ValidationCompleteEvent event) {
    // NO-OP
  }
}

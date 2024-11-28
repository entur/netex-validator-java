package org.entur.netex.validation.validator;

/**
 * Context of the validation for the current NeTEx file.
 */
public interface ValidationContext {
  /**
   * The name of the file being validated.
   */
  String getFileName();

  /**
   * The codespace of the current dataset.
   */
  String getCodespace();

  /**
   * Return true if the current file is a common (shared) file.
   */
  default boolean isCommonFile() {
    return getFileName() != null && getFileName().startsWith("_");
  }
}

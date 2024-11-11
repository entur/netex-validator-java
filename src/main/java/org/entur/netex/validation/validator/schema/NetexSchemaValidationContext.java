package org.entur.netex.validation.validator.schema;

import org.entur.netex.validation.validator.ValidationContext;

/**
 * Validation context for NeTEx XML schema validation.
 */
public class NetexSchemaValidationContext implements ValidationContext {

  private final String fileName;
  private final String codespace;
  private final byte[] fileContent;

  public NetexSchemaValidationContext(
    String fileName,
    String codespace,
    byte[] fileContent
  ) {
    this.fileName = fileName;
    this.codespace = codespace;
    this.fileContent = fileContent;
  }

  @Override
  public String getFileName() {
    return fileName;
  }

  @Override
  public String getCodespace() {
    return codespace;
  }

  public byte[] getFileContent() {
    return fileContent;
  }
}

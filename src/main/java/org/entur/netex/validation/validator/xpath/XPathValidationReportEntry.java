package org.entur.netex.validation.validator.xpath;

import org.entur.netex.validation.validator.DataLocation;

/**
 * A validation finding returned by an XPath validation rule.
 */
public class XPathValidationReportEntry {

  private final String code;
  private final String message;
  private final DataLocation dataLocation;

  public XPathValidationReportEntry(
    String message,
    String code,
    DataLocation dataLocation
  ) {
    this.code = code;
    this.message = message;
    this.dataLocation = dataLocation;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public DataLocation getDataLocation() {
    return dataLocation;
  }
}

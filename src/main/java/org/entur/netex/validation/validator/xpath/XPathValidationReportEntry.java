package org.entur.netex.validation.validator.xpath;

import org.entur.netex.validation.validator.DataLocation;

/**
 * A validation finding returned by an XPath validation rule.
 */
public record XPathValidationReportEntry(
  String message,
  String code,
  DataLocation dataLocation
) {}

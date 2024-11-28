package org.entur.netex.validation.validator;

import java.util.Objects;
import org.entur.netex.validation.validator.id.IdVersion;

/**
 * Base class for XPath-based validators.
 */
public abstract class AbstractXPathValidator implements XPathValidator {

  /**
   * Return the location of a NeTEx element in the XML document.
   */
  protected DataLocation getIdVersionLocation(IdVersion id) {
    Objects.requireNonNull(id);
    return new DataLocation(
      id.getId(),
      id.getFilename(),
      id.getLineNumber(),
      id.getColumnNumber()
    );
  }
}

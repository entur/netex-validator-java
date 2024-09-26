package org.entur.netex.validation.validator;

import java.util.Set;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;

/**
 * An XPath-based validator that updates a {@link ValidationReport} according to a {@link XPathValidationContext}.
 */
public interface XPathNetexValidator {
  /**
   * Validate the document specified in the {@link XPathValidationContext} and update the given {@link ValidationReport}
   * @param validationReport the validation report to be updated.
   * @param xPathValidationContext the current validation context.
   */
  void validate(
    ValidationReport validationReport,
    XPathValidationContext xPathValidationContext
  );

  /**
   * Return the textual descriptions of the validation rules verified by this validator.
   * @return the textual descriptions of the rules
   */
  Set<String> getRuleDescriptions();
}

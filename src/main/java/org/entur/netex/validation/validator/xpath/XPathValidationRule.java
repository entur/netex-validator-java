package org.entur.netex.validation.validator.xpath;

import java.util.List;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;

/**
 * A single XPath validation rule that produces a list of {@link ValidationIssue}.
 */
public interface XPathValidationRule {
  /**
   * Validate a NeTEx document or document part.
   *
   * @param validationContext the current validation context.
   * @return a list of validation entries for the current context.
   */
  List<ValidationIssue> validate(XPathRuleValidationContext validationContext);

  ValidationRule rule();
}

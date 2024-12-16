package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

/**
 * Construct a validation tree builder for rules applied at the PublicationDelivery level (top-level).
 */
public class DefaultRootValidationTreeFactory implements ValidationTreeFactory {

  public static final String CODE_VERSION_NON_NUMERIC = "VERSION_NON_NUMERIC";

  @Override
  public ValidationTreeBuilder builder() {
    return new ValidationTreeBuilder("Root", "PublicationDelivery")
      .withRule(
        new ValidateNotExist(
          ".//*[@version != 'any' and number(@version) != number(@version)]",
          CODE_VERSION_NON_NUMERIC,
          "Non-numeric NeTEx version",
          "Non-numeric NeTEx version",
          Severity.WARNING
        )
      );
  }
}

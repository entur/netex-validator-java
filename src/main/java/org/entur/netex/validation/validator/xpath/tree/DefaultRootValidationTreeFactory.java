package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

public class DefaultRootValidationTreeFactory implements ValidationTreeFactory {

  public static final String CODE_VERSION_NON_NUMERIC = "VERSION_NON_NUMERIC";

  @Override
  public ValidationTree buildValidationTree() {
    ValidationTreeBuilder builder = new ValidationTreeBuilder("Root", ".");

    return builder
      .withRule(
        new ValidateNotExist(
          ".//*[@version != 'any' and number(@version) != number(@version)]",
          CODE_VERSION_NON_NUMERIC,
          "Non-numeric NeTEx version",
          "Non-numeric NeTEx version",
          Severity.WARNING
        )
      )
      .build();
  }
}

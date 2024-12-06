package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;

public class DefaultRootValidationTreeFactory implements ValidationTreeFactory {

  @Override
  public ValidationTree buildValidationTree() {
    return new ValidationTree("Root", ".");
  }
}

package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;

public class PublicationDeliveryValidationTreeFactory
  implements ValidationTreeFactory {

  @Override
  public ValidationTree buildValidationTree() {
    ValidationTree validationTree = new ValidationTree(
      "PublicationDelivery",
      "/"
    );

    validationTree.addSubTree(
      new DefaultRootValidationTreeFactory().buildValidationTree()
    );

    ValidationTree allFramesValidationTree = new ValidationTree(
      "All Frames",
      "(PublicationDelivery/dataObjects/frames|PublicationDelivery/dataObjects/CompositeFrame/frames)"
    );

    validationTree.addSubTree(allFramesValidationTree);

    allFramesValidationTree.addSubTree(
      new DefaultServiceFrameValidationTreeFactory().buildValidationTree()
    );
    allFramesValidationTree.addSubTree(
      new DefaultTimetableFrameValidationTreeFactory().buildValidationTree()
    );

    return validationTree;
  }
}

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

    ValidationTree singleFramesValidationTree = new ValidationTree(
      "All Single Frames",
      "PublicationDelivery/dataObjects/frames"
    );

    ValidationTree framesInCompositeFramesValidationTree = new ValidationTree(
      "All Frames in Composite Frame",
      "PublicationDelivery/dataObjects/CompositeFrame/frames"
    );

    validationTree.addSubTree(singleFramesValidationTree);
    validationTree.addSubTree(framesInCompositeFramesValidationTree);

    ValidationTree serviceFrameValidationTree =
      new DefaultServiceFrameValidationTreeFactory().buildValidationTree();
    ValidationTree timetableFrameValidationTree =
      new DefaultTimetableFrameValidationTreeFactory().buildValidationTree();
    ValidationTree noticeValidationTree =
      new DefaultNoticeValidationTreeFactory().buildValidationTree();

    singleFramesValidationTree.addSubTree(serviceFrameValidationTree);
    singleFramesValidationTree.addSubTree(timetableFrameValidationTree);
    singleFramesValidationTree.addSubTree(noticeValidationTree);

    framesInCompositeFramesValidationTree.addSubTree(
      serviceFrameValidationTree
    );
    framesInCompositeFramesValidationTree.addSubTree(
      timetableFrameValidationTree
    );
    framesInCompositeFramesValidationTree.addSubTree(noticeValidationTree);

    return validationTree;
  }
}

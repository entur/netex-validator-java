package org.entur.netex.validation.validator.xpath.tree;

import java.util.stream.Stream;
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

    Stream
      .of(
        new DefaultResourceFrameValidationTreeFactory().buildValidationTree(),
        new DefaultServiceFrameValidationTreeFactory().buildValidationTree(),
        new DefaultTimetableFrameValidationTreeFactory().buildValidationTree(),
        new DefaultVehicleScheduleFrameValidationTreeFactory()
          .buildValidationTree()
      )
      .forEach(tree -> {
        singleFramesValidationTree.addSubTree(tree);
        framesInCompositeFramesValidationTree.addSubTree(tree);
      });

    ValidationTree noticeValidationTree =
      new DefaultNoticeValidationTreeFactory().buildValidationTree();

    singleFramesValidationTree.addSubTree(noticeValidationTree);
    framesInCompositeFramesValidationTree.addSubTree(noticeValidationTree);

    return validationTree;
  }
}

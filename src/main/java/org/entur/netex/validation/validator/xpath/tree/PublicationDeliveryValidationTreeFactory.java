package org.entur.netex.validation.validator.xpath.tree;

import java.util.stream.Stream;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;

/**
 * Build the top-level XPath validation tree.
 * The root element of the tree is the PublicationDelivery node.
 * The Nordic NeTEx Profile allows frames to be nested either directly under
 * PublicationDelivery/dataObjects or within a CompositeFrame.
 * This validation tree supports both structures.
 */
public class PublicationDeliveryValidationTreeFactory
  implements ValidationTreeFactory {

  private ValidationTree rootValidationTree;
  private ValidationTree compositeFrameValidationTree;
  private ValidationTree resourceFrameValidationTree;
  private ValidationTree serviceFrameValidationTree;
  private ValidationTree serviceCalendarFrameValidationTree;
  private ValidationTree timetableFrameValidationTree;
  private ValidationTree vehicleScheduleFrameValidationTree;
  private ValidationTree noticeValidationTree;

  public PublicationDeliveryValidationTreeFactory() {
    rootValidationTree =
      new DefaultRootValidationTreeFactory().buildValidationTree();
    compositeFrameValidationTree =
      new DefaultCompositeFrameTreeFactory().buildValidationTree();
    resourceFrameValidationTree =
      new DefaultResourceFrameValidationTreeFactory().buildValidationTree();
    serviceFrameValidationTree =
      new DefaultServiceFrameValidationTreeFactory().buildValidationTree();
    serviceCalendarFrameValidationTree =
      new DefaultServiceCalendarFrameValidationTreeFactory()
        .buildValidationTree();
    timetableFrameValidationTree =
      new DefaultTimetableFrameValidationTreeFactory().buildValidationTree();
    vehicleScheduleFrameValidationTree =
      new DefaultVehicleScheduleFrameValidationTreeFactory()
        .buildValidationTree();
    noticeValidationTree =
      new DefaultNoticeValidationTreeFactory().buildValidationTree();
  }

  @Override
  public ValidationTree buildValidationTree() {
    ValidationTree validationTree = new ValidationTree(
      "PublicationDelivery",
      "/"
    );

    validationTree.addSubTree(rootValidationTree);
    validationTree.addSubTree(compositeFrameValidationTree);

    ValidationTree singleFramesValidationTree = new ValidationTree(
      "All Single Frames",
      "PublicationDelivery/dataObjects"
    );

    ValidationTree framesInCompositeFramesValidationTree = new ValidationTree(
      "All Frames in Composite Frame",
      "PublicationDelivery/dataObjects/CompositeFrame/frames"
    );

    validationTree.addSubTree(singleFramesValidationTree);
    validationTree.addSubTree(framesInCompositeFramesValidationTree);

    Stream
      .of(
        resourceFrameValidationTree,
        serviceFrameValidationTree,
        serviceCalendarFrameValidationTree,
        timetableFrameValidationTree,
        vehicleScheduleFrameValidationTree,
        noticeValidationTree
      )
      .forEach(tree -> {
        singleFramesValidationTree.addSubTree(tree);
        framesInCompositeFramesValidationTree.addSubTree(tree);
      });

    return validationTree;
  }
}

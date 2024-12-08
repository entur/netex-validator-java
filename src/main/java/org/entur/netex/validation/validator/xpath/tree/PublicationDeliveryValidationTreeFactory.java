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

  private ValidationTree rootValidationTree = ValidationTree.empty();
  private ValidationTree compositeFrameValidationTree = ValidationTree.empty();
  private ValidationTree resourceFrameValidationTree = ValidationTree.empty();
  private ValidationTree serviceFrameValidationTree = ValidationTree.empty();
  private ValidationTree serviceCalendarFrameValidationTree =
    ValidationTree.empty();
  private ValidationTree timetableFrameValidationTree = ValidationTree.empty();
  private ValidationTree vehicleScheduleFrameValidationTree =
    ValidationTree.empty();
  private ValidationTree noticeValidationTree = ValidationTree.empty();

  public static PublicationDeliveryValidationTreeFactory ofDefaults() {
    PublicationDeliveryValidationTreeFactory factory =
      new PublicationDeliveryValidationTreeFactory();
    factory.setRootValidationTree(
      new DefaultRootValidationTreeFactory().buildValidationTree()
    );
    factory.setCompositeFrameValidationTree(
      new DefaultCompositeFrameTreeFactory().buildValidationTree()
    );
    factory.setResourceFrameValidationTree(
      new DefaultResourceFrameValidationTreeFactory().buildValidationTree()
    );
    factory.setServiceFrameValidationTree(
      new DefaultServiceFrameValidationTreeFactory().buildValidationTree()
    );
    factory.setServiceCalendarFrameValidationTree(
      new DefaultServiceCalendarFrameValidationTreeFactory()
        .buildValidationTree()
    );
    factory.setTimetableFrameValidationTree(
      new DefaultTimetableFrameValidationTreeFactory().buildValidationTree()
    );
    factory.setVehicleScheduleFrameValidationTree(
      new DefaultVehicleScheduleFrameValidationTreeFactory()
        .buildValidationTree()
    );
    factory.setNoticeValidationTree(
      new DefaultNoticeValidationTreeFactory().buildValidationTree()
    );
    return factory;
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

  public ValidationTree rootValidationTree() {
    return rootValidationTree;
  }

  public PublicationDeliveryValidationTreeFactory setRootValidationTree(
    ValidationTree rootValidationTree
  ) {
    this.rootValidationTree = rootValidationTree;
    return this;
  }

  public ValidationTree compositeFrameValidationTree() {
    return compositeFrameValidationTree;
  }

  public PublicationDeliveryValidationTreeFactory setCompositeFrameValidationTree(
    ValidationTree compositeFrameValidationTree
  ) {
    this.compositeFrameValidationTree = compositeFrameValidationTree;
    return this;
  }

  public ValidationTree resourceFrameValidationTree() {
    return resourceFrameValidationTree;
  }

  public PublicationDeliveryValidationTreeFactory setResourceFrameValidationTree(
    ValidationTree resourceFrameValidationTree
  ) {
    this.resourceFrameValidationTree = resourceFrameValidationTree;
    return this;
  }

  public ValidationTree serviceFrameValidationTree() {
    return serviceFrameValidationTree;
  }

  public PublicationDeliveryValidationTreeFactory setServiceFrameValidationTree(
    ValidationTree serviceFrameValidationTree
  ) {
    this.serviceFrameValidationTree = serviceFrameValidationTree;
    return this;
  }

  public ValidationTree serviceCalendarFrameValidationTree() {
    return serviceCalendarFrameValidationTree;
  }

  public PublicationDeliveryValidationTreeFactory setServiceCalendarFrameValidationTree(
    ValidationTree serviceCalendarFrameValidationTree
  ) {
    this.serviceCalendarFrameValidationTree =
      serviceCalendarFrameValidationTree;
    return this;
  }

  public ValidationTree timetableFrameValidationTree() {
    return timetableFrameValidationTree;
  }

  public PublicationDeliveryValidationTreeFactory setTimetableFrameValidationTree(
    ValidationTree timetableFrameValidationTree
  ) {
    this.timetableFrameValidationTree = timetableFrameValidationTree;
    return this;
  }

  public ValidationTree vehicleScheduleFrameValidationTree() {
    return vehicleScheduleFrameValidationTree;
  }

  public PublicationDeliveryValidationTreeFactory setVehicleScheduleFrameValidationTree(
    ValidationTree vehicleScheduleFrameValidationTree
  ) {
    this.vehicleScheduleFrameValidationTree =
      vehicleScheduleFrameValidationTree;
    return this;
  }

  public ValidationTree noticeValidationTree() {
    return noticeValidationTree;
  }

  public PublicationDeliveryValidationTreeFactory setNoticeValidationTree(
    ValidationTree noticeValidationTree
  ) {
    this.noticeValidationTree = noticeValidationTree;
    return this;
  }
}

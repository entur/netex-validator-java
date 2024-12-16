package org.entur.netex.validation.validator.xpath.tree;

import java.util.stream.Stream;
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

  private ValidationTreeBuilder rootValidationTreeBuilder =
    new DefaultRootValidationTreeFactory().builder();
  private ValidationTreeBuilder singleFramesValidationTreeBuilder =
    new DefaultSingleFramesValidationTreeFactory().builder();
  private ValidationTreeBuilder compositeFrameValidationTreeBuilder =
    new DefaultCompositeFrameTreeFactory().builder();
  private ValidationTreeBuilder siteFrameValidationTreeBuilder =
    new DefaultSiteFrameValidationTreeFactory().builder();

  private ValidationTreeBuilder resourceFrameValidationTreeBuilder =
    new DefaultResourceFrameValidationTreeFactory().builder();
  private ValidationTreeBuilder serviceFrameValidationTreeBuilder =
    new DefaultServiceFrameValidationTreeFactory().builder();
  private ValidationTreeBuilder serviceCalendarFrameValidationTreeBuilder =
    new DefaultServiceCalendarFrameValidationTreeFactory().builder();
  private ValidationTreeBuilder timetableFrameValidationTreeBuilder =
    new DefaultTimetableFrameValidationTreeFactory().builder();
  private ValidationTreeBuilder vehicleScheduleFrameValidationTreeBuilder =
    new DefaultVehicleScheduleFrameValidationTreeFactory().builder();
  private ValidationTreeBuilder multipleFramesValidationTreeBuilder =
    new DefaultMultipleFramesValidationTreeFactory().builder();

  @Override
  public ValidationTreeBuilder builder() {
    ValidationTreeBuilder validationTreeBuilder = new ValidationTreeBuilder(
      "PublicationDelivery",
      "/"
    );

    validationTreeBuilder.withSubTreeBuilder(rootValidationTreeBuilder);
    validationTreeBuilder.withSubTreeBuilder(
      compositeFrameValidationTreeBuilder
    );

    ValidationTreeBuilder dataObjectsValidationTree = new ValidationTreeBuilder(
      "Data Objects",
      "PublicationDelivery/dataObjects",
      validationContext ->
        validationContext
          .getNetexXMLParser()
          .selectNodeSet("CompositeFrame", validationContext.getXmlNode())
          .isEmpty()
    );
    dataObjectsValidationTree.withSubTreeBuilder(
      singleFramesValidationTreeBuilder
    );

    ValidationTreeBuilder framesInCompositeFrameValidationTree =
      new ValidationTreeBuilder(
        "All Frames in Composite Frame",
        "PublicationDelivery/dataObjects/CompositeFrame/frames"
      );

    validationTreeBuilder.withSubTreeBuilder(dataObjectsValidationTree);
    validationTreeBuilder.withSubTreeBuilder(
      framesInCompositeFrameValidationTree
    );

    Stream
      .of(
        siteFrameValidationTreeBuilder,
        resourceFrameValidationTreeBuilder,
        serviceFrameValidationTreeBuilder,
        serviceCalendarFrameValidationTreeBuilder,
        timetableFrameValidationTreeBuilder,
        vehicleScheduleFrameValidationTreeBuilder,
        multipleFramesValidationTreeBuilder
      )
      .forEach(tree -> {
        dataObjectsValidationTree.withSubTreeBuilder(tree);
        framesInCompositeFrameValidationTree.withSubTreeBuilder(tree);
      });

    return validationTreeBuilder;
  }

  public ValidationTreeBuilder rootValidationTreeBuilder() {
    return rootValidationTreeBuilder;
  }

  public void setRootValidationTreeBuilder(
    ValidationTreeBuilder rootValidationTreeBuilder
  ) {
    this.rootValidationTreeBuilder = rootValidationTreeBuilder;
  }

  public ValidationTreeBuilder singleFramesValidationTreeBuilder() {
    return singleFramesValidationTreeBuilder;
  }

  public void setSingleFramesValidationTreeBuilder(
    ValidationTreeBuilder singleFramesValidationTreeBuilder
  ) {
    this.singleFramesValidationTreeBuilder = singleFramesValidationTreeBuilder;
  }

  public ValidationTreeBuilder compositeFrameValidationTreeBuilder() {
    return compositeFrameValidationTreeBuilder;
  }

  public void setCompositeFrameValidationTreeBuilder(
    ValidationTreeBuilder compositeFrameValidationTreeBuilder
  ) {
    this.compositeFrameValidationTreeBuilder =
      compositeFrameValidationTreeBuilder;
  }

  public ValidationTreeBuilder siteFrameValidationTreeBuilder() {
    return siteFrameValidationTreeBuilder;
  }

  public void setSiteFrameValidationTreeBuilder(
    ValidationTreeBuilder siteFrameValidationTreeBuilder
  ) {
    this.siteFrameValidationTreeBuilder = siteFrameValidationTreeBuilder;
  }

  public ValidationTreeBuilder resourceFrameValidationTreeBuilder() {
    return resourceFrameValidationTreeBuilder;
  }

  public void setResourceFrameValidationTreeBuilder(
    ValidationTreeBuilder resourceFrameValidationTreeBuilder
  ) {
    this.resourceFrameValidationTreeBuilder =
      resourceFrameValidationTreeBuilder;
  }

  public ValidationTreeBuilder serviceFrameValidationTreeBuilder() {
    return serviceFrameValidationTreeBuilder;
  }

  public void setServiceFrameValidationTreeBuilder(
    ValidationTreeBuilder serviceFrameValidationTreeBuilder
  ) {
    this.serviceFrameValidationTreeBuilder = serviceFrameValidationTreeBuilder;
  }

  public ValidationTreeBuilder serviceCalendarFrameValidationTreeBuilder() {
    return serviceCalendarFrameValidationTreeBuilder;
  }

  public void setServiceCalendarFrameValidationTreeBuilder(
    ValidationTreeBuilder serviceCalendarFrameValidationTreeBuilder
  ) {
    this.serviceCalendarFrameValidationTreeBuilder =
      serviceCalendarFrameValidationTreeBuilder;
  }

  public ValidationTreeBuilder timetableFrameValidationTreeBuilder() {
    return timetableFrameValidationTreeBuilder;
  }

  public void setTimetableFrameValidationTreeBuilder(
    ValidationTreeBuilder timetableFrameValidationTreeBuilder
  ) {
    this.timetableFrameValidationTreeBuilder =
      timetableFrameValidationTreeBuilder;
  }

  public ValidationTreeBuilder vehicleScheduleFrameValidationTreeBuilder() {
    return vehicleScheduleFrameValidationTreeBuilder;
  }

  public void setVehicleScheduleFrameValidationTreeBuilder(
    ValidationTreeBuilder vehicleScheduleFrameValidationTreeBuilder
  ) {
    this.vehicleScheduleFrameValidationTreeBuilder =
      vehicleScheduleFrameValidationTreeBuilder;
  }

  public ValidationTreeBuilder multipleFramesValidationTreeBuilder() {
    return multipleFramesValidationTreeBuilder;
  }

  public void setMultipleFramesValidationTreeBuilder(
    ValidationTreeBuilder multipleFramesValidationTreeBuilder
  ) {
    this.multipleFramesValidationTreeBuilder =
      multipleFramesValidationTreeBuilder;
  }
}

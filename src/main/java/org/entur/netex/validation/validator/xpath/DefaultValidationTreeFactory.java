package org.entur.netex.validation.validator.xpath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.rules.*;

/**
 * Build the tree of XPath validation rules.
 */
public class DefaultValidationTreeFactory implements ValidationTreeFactory {

  public static final String SITE_FRAME = "SiteFrame";
  public static final String RESOURCE_FRAME = "ResourceFrame";

  @Override
  public ValidationTree buildValidationTree() {
    ValidationTree validationTree = new ValidationTree(
      "PublicationDelivery",
      "/"
    );

    validationTree.addSubTree(getCommonFileValidationTree());
    validationTree.addSubTree(getLineFileValidationTree());
    return validationTree;
  }

  protected ValidationTree getCommonFileValidationTree() {
    ValidationTree commonFileValidationTree = new ValidationTree(
      "Common file",
      "/",
      XPathRuleValidationContext::isCommonFile
    );
    commonFileValidationTree.addSubTree(
      getCompositeFrameValidationTreeForCommonFile()
    );
    commonFileValidationTree.addSubTree(
      getSingleFramesValidationTreeForCommonFile()
    );

    return commonFileValidationTree;
  }

  protected ValidationTree getSingleFramesValidationTreeForCommonFile() {
    ValidationTree validationTree = new ValidationTree(
      "Single frames in common file",
      "PublicationDelivery/dataObjects",
      validationContext ->
        validationContext
          .getNetexXMLParser()
          .selectNodeSet("CompositeFrame", validationContext.getXmlNode())
          .isEmpty()
    );

    validationTree.addSubTree(getResourceFrameValidationTree(RESOURCE_FRAME));

    validationTree.addSubTree(
      getServiceCalendarFrameValidationTree("ServiceCalendarFrame")
    );
    validationTree.addSubTree(
      getVehicleScheduleFrameValidationTree("VehicleScheduleFrame")
    );
    validationTree.addSubTree(
      getSiteFrameValidationTreeForCommonFile(SITE_FRAME)
    );

    return validationTree;
  }

  protected ValidationTree getCompositeFrameValidationTreeForCommonFile() {
    ValidationTree compositeFrameValidationTree = new ValidationTree(
      "Composite frame in common file",
      "PublicationDelivery/dataObjects/CompositeFrame"
    );

    compositeFrameValidationTree.addValidationRules(
      getCompositeFrameBaseValidationRules()
    );

    compositeFrameValidationTree.addSubTree(
      getResourceFrameValidationTree("frames/ResourceFrame")
    );
    compositeFrameValidationTree.addSubTree(
      getServiceCalendarFrameValidationTree("frames/ServiceCalendarFrame")
    );
    compositeFrameValidationTree.addSubTree(
      getVehicleScheduleFrameValidationTree("frames/VehicleScheduleFrame")
    );
    compositeFrameValidationTree.addSubTree(
      getSiteFrameValidationTreeForCommonFile("frames/SiteFrame")
    );

    return compositeFrameValidationTree;
  }

  protected ValidationTree getSiteFrameValidationTreeForCommonFile(
    String path
  ) {
    return new ValidationTree("Site frame in common file", path);
  }

  protected ValidationTree getLineFileValidationTree() {
    ValidationTree lineFileValidationTree = new ValidationTree(
      "Line file",
      "/",
      Predicate.not(XPathRuleValidationContext::isCommonFile)
    );
    lineFileValidationTree.addSubTree(
      getCompositeFrameValidationTreeForLineFile()
    );
    lineFileValidationTree.addSubTree(
      getSingleFramesValidationTreeForLineFile()
    );
    return lineFileValidationTree;
  }

  protected ValidationTree getCompositeFrameValidationTreeForLineFile() {
    ValidationTree compositeFrameValidationTree = new ValidationTree(
      "Composite frame in line file",
      "PublicationDelivery/dataObjects/CompositeFrame"
    );

    compositeFrameValidationTree.addValidationRules(
      getCompositeFrameBaseValidationRules()
    );

    compositeFrameValidationTree.addValidationRule(
      new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty("frames/")
    );
    compositeFrameValidationTree.addValidationRule(
      new ValidateMandatoryBookingProperty("BookingMethods", "frames/")
    );
    compositeFrameValidationTree.addValidationRule(
      new ValidateMandatoryBookingProperty("BookingContact", "frames/")
    );

    compositeFrameValidationTree.addSubTree(
      getResourceFrameValidationTree("frames/ResourceFrame")
    );
    compositeFrameValidationTree.addSubTree(
      getServiceCalendarFrameValidationTree("frames/ServiceCalendarFrame")
    );
    compositeFrameValidationTree.addSubTree(
      getVehicleScheduleFrameValidationTree("frames/VehicleScheduleFrame")
    );

    compositeFrameValidationTree.addSubTree(
      getTimetableFrameValidationTree("frames/TimetableFrame")
    );

    return compositeFrameValidationTree;
  }

  protected ValidationTree getSingleFramesValidationTreeForLineFile() {
    ValidationTree validationTree = new ValidationTree(
      "Single frames in line file",
      "PublicationDelivery/dataObjects",
      validationContext ->
        validationContext
          .getNetexXMLParser()
          .selectNodeSet("CompositeFrame", validationContext.getXmlNode())
          .isEmpty()
    );

    validationTree.addSubTree(getResourceFrameValidationTree(RESOURCE_FRAME));
    validationTree.addSubTree(
      getServiceCalendarFrameValidationTree("ServiceCalendarFrame")
    );
    validationTree.addSubTree(
      getTimetableFrameValidationTree("TimetableFrame")
    );
    validationTree.addSubTree(
      getVehicleScheduleFrameValidationTree("VehicleScheduleFrame")
    );

    return validationTree;
  }

  protected ValidationTree getTimetableFrameValidationTree(String path) {
    ValidationTree validationTree = new ValidationTree("Timetable frame", path);

    validationTree.addSubTree(getNoticesValidationTree());
    validationTree.addSubTree(getNoticeAssignmentsValidationTree());

    return validationTree;
  }

  /**
   * CompositeFrame validation rules that apply both to Line files and common files.
   *
   */
  protected List<XPathValidationRule> getCompositeFrameBaseValidationRules() {
    List<XPathValidationRule> validationRules = new ArrayList<>();

    return validationRules;
  }

  protected ValidationTree getResourceFrameValidationTree(String path) {
    ValidationTree resourceFrameValidationTree = new ValidationTree(
      "Resource frame",
      path
    );

    return resourceFrameValidationTree;
  }

  protected ValidationTree getServiceCalendarFrameValidationTree(String path) {
    ValidationTree serviceCalendarFrameValidationTree = new ValidationTree(
      "Service Calendar frame",
      path
    );

    return serviceCalendarFrameValidationTree;
  }

  protected ValidationTree getVehicleScheduleFrameValidationTree(String path) {
    ValidationTree serviceCalendarFrameValidationTree = new ValidationTree(
      "Vehicle Schedule frame",
      path
    );

    return serviceCalendarFrameValidationTree;
  }

  protected ValidationTree getNoticesValidationTree() {
    ValidationTree noticesValidationTree = new ValidationTree(
      "Notices",
      "notices"
    );

    return noticesValidationTree;
  }

  protected ValidationTree getNoticeAssignmentsValidationTree() {
    ValidationTree noticesAssignmentsValidationTree = new ValidationTree(
      "Notices Assignments",
      "noticeAssignments"
    );

    return noticesAssignmentsValidationTree;
  }

  public static void main(String[] args) {
    DefaultValidationTreeFactory defaultValidationTreeFactory =
      new DefaultValidationTreeFactory();
    System.out.println(
      defaultValidationTreeFactory.buildValidationTree().printRulesList()
    );
  }
}

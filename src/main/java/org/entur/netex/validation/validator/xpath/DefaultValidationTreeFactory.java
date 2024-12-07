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

    validationTree.addValidationRule(
      new ValidateNotExist(
        SITE_FRAME,
        "SITE_FRAME_IN_COMMON_FILE",
        "CompositeFrame unexpected SiteFrame",
        "Unexpected element SiteFrame. It will be ignored",
        Severity.WARNING
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "TimetableFrame",
        "TIMETABLE_FRAME_IN_COMMON_FILE",
        "TimetableFrame illegal in Common file",
        "Timetable frame not allowed in common files",
        Severity.ERROR
      )
    );

    validationTree.addValidationRule(
      new ValidateAtLeastOne(
        "ServiceFrame[validityConditions] | ServiceCalendarFrame[validityConditions]",
        "VALIDITY_CONDITIONS_IN_COMMON_FILE_1",
        "ValidityConditions missing in ServiceFrame or ServiceCalendarFrame",
        "Neither ServiceFrame nor ServiceCalendarFrame defines ValidityConditions",
        Severity.ERROR
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "ResourceFrame[not(validityConditions) and count(//ResourceFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_COMMON_FILE_2",
        "ValidityConditions missing in ResourceFrames",
        "Multiple ResourceFrames without validity conditions",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "ServiceFrame[not(validityConditions) and count(//ServiceFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_COMMON_FILE_3",
        "ValidityConditions missing in ServiceFrames",
        "Multiple ServiceFrames without validity conditions",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "ServiceCalendarFrame[not(validityConditions) and count(//ServiceCalendarFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_COMMON_FILE_4",
        "ValidityConditions missing in ServiceCalendarFrames",
        "Multiple ServiceCalendarFrames without validity conditions",
        Severity.ERROR
      )
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
    compositeFrameValidationTree.addValidationRule(
      new ValidateNotExist(
        "frames/TimetableFrame",
        "COMPOSITE_TIMETABLE_FRAME_IN_COMMON_FILE",
        "CompositeFrame illegal Timetable",
        "Timetable frame not allowed in common files",
        Severity.ERROR
      )
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

    validationTree.addValidationRule(
      new ValidateExactlyOne(
        RESOURCE_FRAME,
        "RESOURCE_FRAME_IN_LINE_FILE",
        "ResourceFrame must be exactly one",
        "Exactly one ResourceFrame should be present",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        SITE_FRAME,
        "SITE_FRAME_IN_LINE_FILE",
        "SiteFrame unexpected SiteFrame in Line file",
        "Unexpected element SiteFrame. It will be ignored",
        Severity.WARNING
      )
    );
    validationTree.addValidationRule(
      new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty("")
    );
    validationTree.addValidationRule(
      new ValidateMandatoryBookingProperty("BookingMethods", "frames/")
    );
    validationTree.addValidationRule(
      new ValidateMandatoryBookingProperty("BookingContact", "frames/")
    );

    validationTree.addValidationRule(
      new ValidateAtLeastOne(
        "ServiceFrame[validityConditions] | ServiceCalendarFrame[validityConditions] | TimetableFrame[validityConditions]",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_1",
        "ValidityConditions missing in all frames",
        "Neither ServiceFrame, ServiceCalendarFrame nor TimetableFrame defines ValidityConditions",
        Severity.ERROR
      )
    );

    validationTree.addValidationRule(
      new ValidateNotExist(
        "ServiceFrame[not(validityConditions) and count(//ServiceFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_2",
        "ValidityConditions missing in ServiceFrames",
        "Multiple frames of same type without validity conditions",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "ServiceCalendarFrame[not(validityConditions) and count(//ServiceCalendarFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_3",
        "ValidityConditions missing in ServiceCalendarFrames",
        "Multiple frames of same type without validity conditions",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "TimetableFrame[not(validityConditions) and count(//TimetableFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_4",
        "ValidityConditions missing in TimeTableFrames",
        "Multiple frames of same type without validity conditions",
        Severity.ERROR
      )
    );
    validationTree.addValidationRule(
      new ValidateNotExist(
        "VehicleScheduleFrame[not(validityConditions) and count(//VehicleScheduleFrame) > 1]",
        "VALIDITY_CONDITIONS_IN_LINE_FILE_5",
        "ValidityConditions missing in VehicleScheduleFrame",
        "Multiple frames of same type without validity conditions",
        Severity.ERROR
      )
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
    validationRules.add(
      new ValidateNotExist(
        "frames/SiteFrame",
        "COMPOSITE_SITE_FRAME_IN_COMMON_FILE",
        "CompositeFrame unexpected SiteFrame",
        "Unexpected element SiteFrame. It will be ignored",
        Severity.WARNING
      )
    );

    validationRules.add(
      new ValidateNotExist(
        ".[not(validityConditions)]",
        "COMPOSITE_FRAME_1",
        "CompositeFrame missing ValidityCondition",
        "A CompositeFrame must define a ValidityCondition valid for all data within the CompositeFrame",
        Severity.ERROR
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "frames//validityConditions",
        "COMPOSITE_FRAME_2",
        "CompositeFrame invalid nested ValidityCondition",
        "ValidityConditions defined inside a frame inside a CompositeFrame",
        Severity.WARNING
      )
    );

    validationRules.add(
      new ValidateNotExist(
        "//ValidBetween[not(FromDate) and not(ToDate)]",
        "COMPOSITE_FRAME_3",
        "CompositeFrame missing ValidBetween",
        "ValidBetween missing either or both of FromDate/ToDate",
        Severity.ERROR
      )
    );
    validationRules.add(
      new ValidateNotExist(
        "//ValidBetween[FromDate and ToDate and ToDate < FromDate]",
        "COMPOSITE_FRAME_4",
        "CompositeFrame invalid ValidBetween",
        "FromDate cannot be after ToDate on ValidBetween",
        Severity.ERROR
      )
    );

    validationRules.add(
      new ValidateNotExist(
        "//AvailabilityCondition[FromDate and ToDate and ToDate < FromDate]",
        "COMPOSITE_FRAME_5",
        "CompositeFrame invalid AvailabilityCondition",
        "FromDate cannot be after ToDate on AvailabilityCondition",
        Severity.ERROR
      )
    );

    validationRules.add(
      new ValidateNotExist(
        "//AvailabilityCondition[not(FromDate) and not(ToDate)]",
        "COMPOSITE_FRAME_6",
        "CompositeFrame missing AvailabilityCondition",
        "AvailabilityCondition must have either FromDate or ToDate or both present",
        Severity.ERROR
      )
    );

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

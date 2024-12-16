package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

/**
 * Constrcut a validation tree builder for CompositeFrames.
 * This tree validates the correct nesting of frames inside the composite frame
 * and the correct use of validity conditions.
 * This tree does not validate the content of the nested frames.
 */
public class DefaultCompositeFrameTreeFactory implements ValidationTreeFactory {

  public static final String CODE_COMPOSITE_FRAME_SITE_FRAME =
    "COMPOSITE_FRAME_SITE_FRAME";
  public static final String CODE_COMPOSITE_TIMETABLE_FRAME_IN_COMMON_FILE =
    "COMPOSITE_TIMETABLE_FRAME_IN_COMMON_FILE";
  public static final String CODE_COMPOSITE_FRAME_1 = "COMPOSITE_FRAME_1";
  public static final String CODE_COMPOSITE_FRAME_2 = "COMPOSITE_FRAME_2";
  public static final String CODE_COMPOSITE_FRAME_3 = "COMPOSITE_FRAME_3";
  public static final String CODE_COMPOSITE_FRAME_4 = "COMPOSITE_FRAME_4";
  public static final String CODE_COMPOSITE_FRAME_5 = "COMPOSITE_FRAME_5";
  public static final String CODE_COMPOSITE_FRAME_6 = "COMPOSITE_FRAME_6";

  @Override
  public ValidationTreeBuilder builder() {
    return new ValidationTreeBuilder(
      "Composite Frame",
      "PublicationDelivery/dataObjects/CompositeFrame"
    )
      .withRule(
        new ValidateNotExist(
          "frames/SiteFrame",
          CODE_COMPOSITE_FRAME_SITE_FRAME,
          "CompositeFrame - unexpected SiteFrame",
          "Unexpected element SiteFrame. It will be ignored",
          Severity.WARNING
        )
      )
      .withRuleForCommonFile(
        new ValidateNotExist(
          "frames/TimetableFrame",
          CODE_COMPOSITE_TIMETABLE_FRAME_IN_COMMON_FILE,
          "CompositeFrame - Illegal TimetableFrame in common file",
          "Timetable frame not allowed in common files",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          ".[not(validityConditions)]",
          CODE_COMPOSITE_FRAME_1,
          "CompositeFrame - missing ValidityCondition",
          "A CompositeFrame must define a ValidityCondition valid for all data within the CompositeFrame",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "frames//validityConditions",
          CODE_COMPOSITE_FRAME_2,
          "CompositeFrame - invalid nested ValidityCondition",
          "ValidityConditions defined inside a frame inside a CompositeFrame",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "//ValidBetween[not(FromDate) and not(ToDate)]",
          CODE_COMPOSITE_FRAME_3,
          "CompositeFrame - missing ValidBetween",
          "ValidBetween missing either or both of FromDate/ToDate",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "//ValidBetween[FromDate and ToDate and ToDate < FromDate]",
          CODE_COMPOSITE_FRAME_4,
          "CompositeFrame - invalid ValidBetween",
          "FromDate cannot be after ToDate on ValidBetween",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "//AvailabilityCondition[FromDate and ToDate and ToDate < FromDate]",
          CODE_COMPOSITE_FRAME_5,
          "CompositeFrame - invalid AvailabilityCondition",
          "FromDate cannot be after ToDate on AvailabilityCondition",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "//AvailabilityCondition[not(FromDate) and not(ToDate)]",
          CODE_COMPOSITE_FRAME_6,
          "CompositeFrame - missing AvailabilityCondition",
          "AvailabilityCondition must have either FromDate or ToDate or both present",
          Severity.ERROR
        )
      );
  }
}

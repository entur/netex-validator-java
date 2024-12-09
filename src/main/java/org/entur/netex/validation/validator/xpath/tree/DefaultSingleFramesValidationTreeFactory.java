package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateAtLeastOne;
import org.entur.netex.validation.validator.xpath.rules.ValidateExactlyOne;
import org.entur.netex.validation.validator.xpath.rules.ValidateMandatoryBookingProperty;
import org.entur.netex.validation.validator.xpath.rules.ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

/**
 * Build a validation tree for single frames.
 * This tree validates the correct nesting of single frames
 * and the correct use of validity conditions.
 * This tree does not validate the content of the individual frames.
 */
public class DefaultSingleFramesValidationTreeFactory
  implements ValidationTreeFactory {

  @Override
  public ValidationTree buildValidationTree() {
    return new ValidationTreeBuilder(".", "Single Frames")
      .withRuleForLineFile(
        new ValidateExactlyOne(
          "ResourceFrame",
          "RESOURCE_FRAME_IN_LINE_FILE",
          "ResourceFrame must be exactly one",
          "Exactly one ResourceFrame should be present",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty("")
      )
      .withRuleForLineFile(
        new ValidateMandatoryBookingProperty("BookingMethods", ".")
      )
      .withRuleForLineFile(
        new ValidateMandatoryBookingProperty("BookingContact", ".")
      )
      .withRuleForLineFile(
        new ValidateAtLeastOne(
          "ServiceFrame[validityConditions] | ServiceCalendarFrame[validityConditions] | TimetableFrame[validityConditions]",
          "VALIDITY_CONDITIONS_IN_LINE_FILE_1",
          "ValidityConditions missing in all frames",
          "Neither ServiceFrame, ServiceCalendarFrame nor TimetableFrame defines ValidityConditions",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "ServiceFrame[not(validityConditions) and count(//ServiceFrame) > 1]",
          "VALIDITY_CONDITIONS_IN_LINE_FILE_2",
          "ValidityConditions missing in ServiceFrames",
          "Multiple frames of same type without validity conditions",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "ServiceCalendarFrame[not(validityConditions) and count(//ServiceCalendarFrame) > 1]",
          "VALIDITY_CONDITIONS_IN_LINE_FILE_3",
          "ValidityConditions missing in ServiceCalendarFrames",
          "Multiple frames of same type without validity conditions",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "TimetableFrame[not(validityConditions) and count(//TimetableFrame) > 1]",
          "VALIDITY_CONDITIONS_IN_LINE_FILE_4",
          "ValidityConditions missing in TimeTableFrames",
          "Multiple frames of same type without validity conditions",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "VehicleScheduleFrame[not(validityConditions) and count(//VehicleScheduleFrame) > 1]",
          "VALIDITY_CONDITIONS_IN_LINE_FILE_5",
          "ValidityConditions missing in VehicleScheduleFrame",
          "Multiple frames of same type without validity conditions",
          Severity.ERROR
        )
      )
      .withRuleForCommonFile(
        new ValidateNotExist(
          "TimetableFrame",
          "TIMETABLE_FRAME_IN_COMMON_FILE",
          "TimetableFrame illegal in Common file",
          "Timetable frame not allowed in common files",
          Severity.ERROR
        )
      )
      .withRuleForCommonFile(
        new ValidateAtLeastOne(
          "ServiceFrame[validityConditions] | ServiceCalendarFrame[validityConditions]",
          "VALIDITY_CONDITIONS_IN_COMMON_FILE_1",
          "ValidityConditions missing in ServiceFrame or ServiceCalendarFrame",
          "Neither ServiceFrame nor ServiceCalendarFrame defines ValidityConditions",
          Severity.ERROR
        )
      )
      .withRuleForCommonFile(
        new ValidateNotExist(
          "ResourceFrame[not(validityConditions) and count(//ResourceFrame) > 1]",
          "VALIDITY_CONDITIONS_IN_COMMON_FILE_2",
          "ValidityConditions missing in ResourceFrames",
          "Multiple ResourceFrames without validity conditions",
          Severity.ERROR
        )
      )
      .withRuleForCommonFile(
        new ValidateNotExist(
          "ServiceFrame[not(validityConditions) and count(//ServiceFrame) > 1]",
          "VALIDITY_CONDITIONS_IN_COMMON_FILE_3",
          "ValidityConditions missing in ServiceFrames",
          "Multiple ServiceFrames without validity conditions",
          Severity.ERROR
        )
      )
      .withRuleForCommonFile(
        new ValidateNotExist(
          "ServiceCalendarFrame[not(validityConditions) and count(//ServiceCalendarFrame) > 1]",
          "VALIDITY_CONDITIONS_IN_COMMON_FILE_4",
          "ValidityConditions missing in ServiceCalendarFrames",
          "Multiple ServiceCalendarFrames without validity conditions",
          Severity.ERROR
        )
      )
      .build();
  }
}

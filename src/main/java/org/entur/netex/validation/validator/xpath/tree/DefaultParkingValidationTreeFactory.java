package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

/**
 * Construct a validation tree builder for Parking elements within a SiteFrame.
 * Validates the structure of AvailabilityCondition entries on Parking entities,
 * including DayTypeRef presence and Timeband time range validity.
 */
public class DefaultParkingValidationTreeFactory implements ValidationTreeFactory {

  public static final String CODE_PARKING_AVAILABILITY_CONDITION_WITHOUT_DAY_TYPE =
    "PARKING_AVAILABILITY_CONDITION_WITHOUT_DAY_TYPE";
  public static final String CODE_PARKING_TIMEBAND_WITHOUT_START_OR_END_TIME =
    "PARKING_TIMEBAND_WITHOUT_START_OR_END_TIME";
  public static final String CODE_PARKING_TIMEBAND_INVALID_TIME_RANGE =
    "PARKING_TIMEBAND_INVALID_TIME_RANGE";

  @Override
  public ValidationTreeBuilder builder() {
    return new ValidationTreeBuilder("Parking", "SiteFrame/parkings/Parking")
      .withRule(
        new ValidateNotExist(
          "validityConditions/AvailabilityCondition[not(dayTypes/DayTypeRef)]",
          CODE_PARKING_AVAILABILITY_CONDITION_WITHOUT_DAY_TYPE,
          "Parking AvailabilityCondition missing DayTypeRef",
          "Each AvailabilityCondition on a Parking must reference a DayType via dayTypes/DayTypeRef",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "validityConditions/AvailabilityCondition/timebands/Timeband[not(StartTime) or not(EndTime)]",
          CODE_PARKING_TIMEBAND_WITHOUT_START_OR_END_TIME,
          "Parking Timeband missing StartTime or EndTime",
          "Each Timeband on a Parking AvailabilityCondition must have both StartTime and EndTime",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "validityConditions/AvailabilityCondition/timebands/Timeband[StartTime and EndTime and StartTime != '00:00:00' and EndTime != '00:00:00' and EndTime < StartTime]",
          CODE_PARKING_TIMEBAND_INVALID_TIME_RANGE,
          "Parking Timeband EndTime before StartTime",
          "Timeband EndTime must not be before StartTime (use 00:00:00 to denote midnight/24h)",
          Severity.ERROR
        )
      );
  }
}

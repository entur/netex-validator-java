package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

/**
 * Construct a validation tree builder for ServiceCalendarFrames.
 */
public class DefaultServiceCalendarFrameValidationTreeFactory
  implements ValidationTreeFactory {

  public static final String CODE_SERVICE_CALENDAR_1 = "SERVICE_CALENDAR_1";
  public static final String CODE_SERVICE_CALENDAR_2 = "SERVICE_CALENDAR_2";
  public static final String CODE_SERVICE_CALENDAR_3 = "SERVICE_CALENDAR_3";
  public static final String CODE_SERVICE_CALENDAR_4 = "SERVICE_CALENDAR_4";
  public static final String CODE_SERVICE_CALENDAR_5 = "SERVICE_CALENDAR_5";

  public static final String CODE_OPERATING_PERIOD_1 = "OPERATING_PERIOD_1";

  @Override
  public ValidationTreeBuilder builder() {
    return new ValidationTreeBuilder(
      "Service Calendar Frame",
      "ServiceCalendarFrame"
    )
      .withRule(
        new ValidateNotExist(
          "//DayType[not(//DayTypeAssignment/DayTypeRef/@ref = @id)]",
          CODE_SERVICE_CALENDAR_1,
          "ServiceCalendar unused DayType",
          "The DayType is not assigned to any calendar dates or periods",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "//ServiceCalendar[not(dayTypes) and not(dayTypeAssignments)]",
          CODE_SERVICE_CALENDAR_2,
          "ServiceCalendar empty ServiceCalendar",
          "ServiceCalendar does not contain neither DayTypes nor DayTypeAssignments",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "//ServiceCalendar[not(ToDate)]",
          CODE_SERVICE_CALENDAR_3,
          "ServiceCalendar missing ToDate",
          "Missing ToDate on ServiceCalendar",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "//ServiceCalendar[not(FromDate)]",
          CODE_SERVICE_CALENDAR_4,
          "ServiceCalendar missing FromDate",
          "Missing FromDate on ServiceCalendar",
          Severity.WARNING
        )
      )
      .withRule(
        new ValidateNotExist(
          "//ServiceCalendar[FromDate and ToDate and ToDate < FromDate]",
          CODE_SERVICE_CALENDAR_5,
          "ServiceCalendar invalid time interval",
          "FromDate cannot be after ToDate on ServiceCalendar",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "//OperatingPeriod[FromDate and ToDate and ToDate < FromDate]",
          CODE_OPERATING_PERIOD_1,
          "OperatingPeriod invalid time interval",
          "FromDate cannot be after ToDate on OperatingPeriod",
          Severity.ERROR
        )
      );
  }
}

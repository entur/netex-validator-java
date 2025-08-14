package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateMandatoryBookingProperty;
import org.entur.netex.validation.validator.xpath.rules.ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

/**
 * Construct a validation tree builder for rules that cross-check data across different frames
 * (example: matching a ServiceJourney in a TimetableFrame with a JourneyPattern in a ServiceFrame)
 * or generic rules that can be applied on different frames (example: notices and notice assignments).
 */
public class DefaultMultipleFramesValidationTreeFactory implements ValidationTreeFactory {

  public static final String CODE_NOTICE_1 = "NOTICE_1";
  public static final String CODE_NOTICE_2 = "NOTICE_2";
  public static final String CODE_NOTICE_3 = "NOTICE_3";
  public static final String CODE_NOTICE_4 = "NOTICE_4";
  public static final String CODE_NOTICE_5 = "NOTICE_5";
  public static final String CODE_NOTICE_6 = "NOTICE_6";
  public static final String CODE_NOTICE_7 = "NOTICE_7";

  @Override
  public ValidationTreeBuilder builder() {
    return new ValidationTreeBuilder("Multiple Frames", ".")
      .withRule(
        new ValidateNotExist(
          "(ServiceFrame | TimetableFrame)/notices/Notice[not(Text) or normalize-space(Text/text()) = '']",
          CODE_NOTICE_1,
          "Notice missing Text",
          "Missing element Text for Notice",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "(ServiceFrame | TimetableFrame)/notices/Notice/alternativeTexts/AlternativeText[not(Text) or normalize-space(Text/text()) = '']",
          CODE_NOTICE_2,
          "Notice missing Text with alternative text",
          "Missing or empty element Text for Notice Alternative Text",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "(ServiceFrame | TimetableFrame)/notices/Notice/alternativeTexts/AlternativeText/Text[not(@lang)]",
          CODE_NOTICE_3,
          "Notice missing language with alternative text",
          "Missing element Lang for Notice Alternative Text",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "(ServiceFrame | TimetableFrame)/notices/Notice/alternativeTexts/AlternativeText[Text/@lang = following-sibling::AlternativeText/Text/@lang or Text/@lang = preceding-sibling::AlternativeText/Text/@lang]",
          CODE_NOTICE_4,
          "Notice duplicated alternative texts",
          "The Notice has two Alternative Texts with the same language",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "(ServiceFrame | TimetableFrame)/noticeAssignments/NoticeAssignment[for $a in following-sibling::NoticeAssignment return if(NoticeRef/@ref= $a/NoticeRef/@ref and NoticedObjectRef/@ref= $a/NoticedObjectRef/@ref) then $a else ()]",
          CODE_NOTICE_5,
          "Notice duplicated assignment",
          "The notice is assigned multiple times to the same object",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "(ServiceFrame | TimetableFrame)/noticeAssignments/NoticeAssignment[not(NoticedObjectRef)]",
          CODE_NOTICE_6,
          "Notice assignment missing reference to noticed object",
          "The notice assignment does not reference an object",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "(ServiceFrame | TimetableFrame)/noticeAssignments/NoticeAssignment[not(NoticeRef)]",
          CODE_NOTICE_7,
          "Notice assignment missing reference to notice",
          "The notice assignment does not reference a notice",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateMandatoryBookingWhenOrMinimumBookingPeriodProperty()
      )
      .withRuleForLineFile(new ValidateMandatoryBookingProperty("BookingMethods"))
      .withRuleForLineFile(new ValidateMandatoryBookingProperty("BookingContact"));
  }
}

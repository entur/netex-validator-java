package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

/**
 * Build a validation tree factory for Notice and NoticeAssignment elements.
 * Notice and NoticeAssignment can be found both in ServiceFrames and TimetableFrames.
 */
public class DefaultNoticeValidationTreeFactory
  implements ValidationTreeFactory {

  static final String CODE_NOTICE_1 = "NOTICE_1";
  static final String CODE_NOTICE_2 = "NOTICE_2";
  static final String CODE_NOTICE_3 = "NOTICE_3";
  static final String CODE_NOTICE_4 = "NOTICE_4";
  static final String CODE_NOTICE_5 = "NOTICE_5";
  static final String CODE_NOTICE_6 = "NOTICE_6";
  static final String CODE_NOTICE_7 = "NOTICE_7";

  @Override
  public ValidationTree buildValidationTree() {
    ValidationTreeBuilder builder = new ValidationTreeBuilder(
      "ServiceFrame",
      "Notices and NoticeAssignments in ServiceFrame and TimetableFrame"
    );

    return builder
      .withRule(
        new ValidateNotExist(
          "notices/Notice[not(Text) or normalize-space(Text/text()) = '']",
          CODE_NOTICE_1,
          "Notice missing Text",
          "Missing element Text for Notice",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "notices/Notice/alternativeTexts/AlternativeText[not(Text) or normalize-space(Text/text()) = '']",
          CODE_NOTICE_2,
          "Notice missing Text with alternative text",
          "Missing or empty element Text for Notice Alternative Text",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "notices/Notice/alternativeTexts/AlternativeText/Text[not(@lang)]",
          CODE_NOTICE_3,
          "Notice missing language with alternative text",
          "Missing element Lang for Notice Alternative Text",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "notices/Notice/alternativeTexts/AlternativeText[Text/@lang = following-sibling::AlternativeText/Text/@lang or Text/@lang = preceding-sibling::AlternativeText/Text/@lang]",
          CODE_NOTICE_4,
          "Notice duplicated alternative texts",
          "The Notice has two Alternative Texts with the same language",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "noticeAssignments/NoticeAssignment[for $a in following-sibling::NoticeAssignment return if(NoticeRef/@ref= $a/NoticeRef/@ref and NoticedObjectRef/@ref= $a/NoticedObjectRef/@ref) then $a else ()]",
          CODE_NOTICE_5,
          "Notice duplicated assignment",
          "The notice is assigned multiple times to the same object",
          Severity.WARNING
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "noticeAssignments/NoticeAssignment[not(NoticedObjectRef)]",
          CODE_NOTICE_6,
          "Notice assignment missing reference to noticed object",
          "The notice assignment does not reference an object",
          Severity.ERROR
        )
      )
      .withRuleForLineFile(
        new ValidateNotExist(
          "noticeAssignments/NoticeAssignment[not(NoticeRef)]",
          CODE_NOTICE_7,
          "Notice assignment missing reference to notice",
          "The notice assignment does not reference a notice",
          Severity.ERROR
        )
      )
      .build();
  }
}

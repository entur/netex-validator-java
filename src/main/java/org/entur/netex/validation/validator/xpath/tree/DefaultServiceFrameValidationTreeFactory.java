package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateExactlyOne;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

public class DefaultServiceFrameValidationTreeFactory
  implements ValidationTreeFactory {

  @Override
  public ValidationTree buildValidationTree() {
    ValidationTreeBuilder builder = new ValidationTreeBuilder(
      "ServiceFrame/*",
      " Service Frame "
    );

    return builder
      .withRuleForLineFile(
        new ValidateExactlyOne(
          "lines/*[self::Line or self::FlexibleLine]",
          "LINE_1",
          "Line missing Line or FlexibleLine",
          "There must be either Lines or Flexible Lines",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "lines/*[self::Line or self::FlexibleLine][not(Name) or normalize-space(Name) = '']",
          "LINE_2",
          "Line missing Name",
          "Missing Name on Line",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "lines/*[self::Line or self::FlexibleLine]/*[self::Presentation or self::AlternativePresentation]/*[self::Colour or self::TextColour][text()][string-length(text())!=6]",
          "LINE_8",
          "Invalid color coding length on Presentation",
          "Line colour should be encoded with 6 hexadecimal digits",
          Severity.WARNING
        )
      )
      .withRuleForCommonFile(
        new ValidateNotExist(
          "lines/Line",
          "SERVICE_FRAME_IN_COMMON_FILE_1",
          "ServiceFrame unexpected element Line",
          "Line not allowed in common files",
          Severity.ERROR
        )
      )
      .build();
  }
}

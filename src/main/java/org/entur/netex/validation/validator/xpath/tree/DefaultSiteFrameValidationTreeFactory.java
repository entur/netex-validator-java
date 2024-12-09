package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

public class DefaultSiteFrameValidationTreeFactory
  implements ValidationTreeFactory {

  public static final String CODE_SITE_FRAME_IN_LINE_FILE =
    "SITE_FRAME_IN_LINE_FILE";
  public static final String CODE_SITE_FRAME_IN_COMMON_FILE =
    "SITE_FRAME_IN_COMMON_FILE";

  @Override
  public ValidationTree buildValidationTree() {
    return new ValidationTreeBuilder("SiteFrame", "Site Frame")
      .withRuleForLineFile(
        new ValidateNotExist(
          ".",
          CODE_SITE_FRAME_IN_LINE_FILE,
          "SiteFrame unexpected SiteFrame in Line file",
          "Unexpected element SiteFrame. It will be ignored",
          Severity.WARNING
        )
      )
      .withRuleForCommonFile(
        new ValidateNotExist(
          ".",
          CODE_SITE_FRAME_IN_COMMON_FILE,
          "SiteFrame unexpected SiteFrame in Common file",
          "Unexpected element SiteFrame. It will be ignored",
          Severity.WARNING
        )
      )
      .build();
  }
}

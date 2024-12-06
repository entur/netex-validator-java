package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;

public class DefaultTimetableFrameValidationTreeFactory
  implements ValidationTreeFactory {

  @Override
  public ValidationTree buildValidationTree() {
    return new ValidationTree(
      "Timetable Frame",
      "TimetableFrame",
      XPathRuleValidationContext::isLineFile
    );
  }
}

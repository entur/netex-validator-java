package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.support.XPathTestSupport.parseDocument;
import static org.entur.netex.validation.validator.xpath.support.XPathTestSupport.validationContext;
import static org.entur.netex.validation.validator.xpath.tree.DefaultSiteFrameValidationTreeFactory.CODE_SITE_FRAME_IN_COMMON_FILE;
import static org.entur.netex.validation.validator.xpath.tree.DefaultSiteFrameValidationTreeFactory.CODE_SITE_FRAME_IN_LINE_FILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultSiteFrameValidationTreeFactoryTest {

  private static final String NETEX_FRAGMENT_INVALID =
    """
        <SiteFrame xmlns="http://www.netex.org.uk/netex" version="1" id="AKT:TimetableFrame:484392"/>
        """;

  private ValidationTree validationTree;

  @BeforeEach
  void setUp() {
    validationTree =
      new DefaultSiteFrameValidationTreeFactory().builder().build();
  }

  @Test
  void testSiteFrameInLineFile() {
    XPathRuleValidationContext xpathValidationContext = validationContext(
      NETEX_FRAGMENT_INVALID
    );
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_SITE_FRAME_IN_LINE_FILE
    );
    assertEquals(1, validationIssues.size());
    assertEquals(
      CODE_SITE_FRAME_IN_LINE_FILE,
      validationIssues.get(0).rule().code()
    );
  }

  @Test
  void testSiteFrameInCommonFile() {
    XPathRuleValidationContext xpathValidationContext = validationContext(
      parseDocument(NETEX_FRAGMENT_INVALID),
      "_common.xml"
    );
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_SITE_FRAME_IN_COMMON_FILE
    );
    assertEquals(1, validationIssues.size());
    assertEquals(
      CODE_SITE_FRAME_IN_COMMON_FILE,
      validationIssues.get(0).rule().code()
    );
  }
}

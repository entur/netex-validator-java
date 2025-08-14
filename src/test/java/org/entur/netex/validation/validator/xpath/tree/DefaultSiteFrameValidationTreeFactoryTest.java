package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.tree.DefaultSiteFrameValidationTreeFactory.CODE_SITE_FRAME_IN_COMMON_FILE;
import static org.entur.netex.validation.validator.xpath.tree.DefaultSiteFrameValidationTreeFactory.CODE_SITE_FRAME_IN_LINE_FILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
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
    validationTree = new DefaultSiteFrameValidationTreeFactory().builder().build();
  }

  @Test
  void testSiteFrameInLineFile() {
    XPathRuleValidationContext xpathValidationContext = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_INVALID)
      .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_SITE_FRAME_IN_LINE_FILE
    );
    assertEquals(1, validationIssues.size());
    assertEquals(CODE_SITE_FRAME_IN_LINE_FILE, validationIssues.get(0).rule().code());
  }

  @Test
  void testSiteFrameInCommonFile() {
    XPathRuleValidationContext xpathValidationContext = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_INVALID)
      .withFilename("_common.xml")
      .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_SITE_FRAME_IN_COMMON_FILE
    );
    assertEquals(1, validationIssues.size());
    assertEquals(CODE_SITE_FRAME_IN_COMMON_FILE, validationIssues.get(0).rule().code());
  }
}

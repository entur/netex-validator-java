package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.tree.DefaultMultipleFramesValidationTreeFactory.CODE_NOTICE_7;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultMultipleFramesValidationTreeFactoryTest {

  private ValidationTree validationTree;

  @BeforeEach
  void setUp() {
    validationTree = new DefaultMultipleFramesValidationTreeFactory().builder().build();
  }

  private static final String NETEX_FRAGMENT =
    """
    <frames xmlns="http://www.netex.org.uk/netex">
        <ServiceFrame  id="ENT:ServiceFrame:1" version="2223">
           <notices>
             <Notice version="0" id="NYC:Notice:4e14a5b2-c4fc-49f5-9dd0-107ed6ee702a">
                    <Text>Denne avgangen tar ikke med rullestoler, sykler eller barnevogner. Turen kan i sjeldne tilfeller bli kansellert.</Text>
             </Notice>
           </notices>
           <noticeAssignments>
              <NoticeAssignment order="2" version="1" id="BRA:NoticeAssignment:1">
                  <NoticedObjectRef ref="ENT:FlexibleLine:9204411c-bf86-4b6a-b8fa-5c40b8702213" version="46"/>
              </NoticeAssignment>
          </noticeAssignments>
        </ServiceFrame>
    </frames>
    """;

  @Test
  void test() {
    XPathRuleValidationContext xpathValidationContext = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT)
      .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      CODE_NOTICE_7
    );
    assertEquals(1, validationIssues.size());
    assertEquals(CODE_NOTICE_7, validationIssues.get(0).rule().code());
  }
}

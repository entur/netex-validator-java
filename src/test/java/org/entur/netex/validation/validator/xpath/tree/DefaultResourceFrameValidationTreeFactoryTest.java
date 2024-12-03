package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.support.XPathTestSupport.validationContext;
import static org.entur.netex.validation.validator.xpath.tree.DefaultResourceFrameValidationTreeFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultResourceFrameValidationTreeFactoryTest {

  private static final String NETEX_FRAGMENT_INVALID =
    """
            <ResourceFrame xmlns="http://www.netex.org.uk/netex" version="521" id="SJN:ResourceFrame:Shared">
                 <organisations>
                   <Authority version="1" id="SJN:Authority:SJN">
                     <CompanyNumber>917 58 77 28</CompanyNumber>
                     <Name>SJ Nord</Name>
                     <LegalName>SJ Nord</LegalName>
                     <ContactDetails>
                       <Email>post@sj.no</Email>
                       <Url>https://www.sj.no</Url>
                     </ContactDetails>
                   </Authority>
                   <Operator version="1" id="SJN:Operator:SJN">
                     <keyList>
                       <KeyValue>
                         <Key>RICS code</Key>
                         <Value>3781</Value>
                       </KeyValue>
                     </keyList>
                   </Operator>
                 </organisations>
            </ResourceFrame>
        
        """;

  private static final String NETEX_FRAGMENT_VALID =
    """
                  <ResourceFrame xmlns="http://www.netex.org.uk/netex" version="521" id="SJN:ResourceFrame:Shared">
                       <organisations>
                         <Authority version="1" id="SJN:Authority:SJN">
                           <CompanyNumber>917 58 77 28</CompanyNumber>
                           <Name>SJ Nord</Name>
                           <LegalName>SJ Nord</LegalName>
                           <ContactDetails>
                             <Email>post@sj.no</Email>
                             <Url>https://www.sj.no</Url>
                           </ContactDetails>
                         </Authority>
                         <Operator version="1" id="SJN:Operator:SJN">
                           <keyList>
                             <KeyValue>
                               <Key>RICS code</Key>
                               <Value>3781</Value>
                             </KeyValue>
                           </keyList>
                           <CompanyNumber>917 58 77 28</CompanyNumber>
                           <Name>SJ Nord</Name>
                           <LegalName>SJ Nord</LegalName>
                           <CustomerServiceContactDetails>
                             <Email>kundeservice@sj.no</Email>
                             <Phone>+61 25 22 00</Phone>
                             <Url>https://www.sj.no</Url>
                           </CustomerServiceContactDetails>
                         </Operator>
                       </organisations>
                  </ResourceFrame>
              
              """;

  private ValidationTree validationTree;

  @BeforeEach
  void setUp() {
    validationTree =
      new DefaultResourceFrameValidationTreeFactory().builder().build();
  }

  static Stream<String> ruleCodes() {
    return Stream.of(
      CODE_OPERATOR_1,
      CODE_OPERATOR_2,
      CODE_OPERATOR_3,
      CODE_OPERATOR_6
    );
  }

  @ParameterizedTest
  @MethodSource("ruleCodes")
  void testInvalidResourceFrame(String code) {
    XPathRuleValidationContext xpathValidationContext = validationContext(
      NETEX_FRAGMENT_INVALID
    );
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      code
    );
    assertEquals(1, validationIssues.size());
    assertEquals(code, validationIssues.get(0).rule().code());
  }

  @ParameterizedTest
  @MethodSource("ruleCodes")
  void testValidResourceFrame(String code) {
    XPathRuleValidationContext xpathValidationContext = validationContext(
      NETEX_FRAGMENT_VALID
    );
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      code
    );
    assertTrue(validationIssues.isEmpty());
  }
}

package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.tree.DefaultServiceCalendarFrameValidationTreeFactory.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultServiceCalendarFrameValidationTreeFactoryTest {

  private static final String NETEX_FRAGMENT_INVALID =
    """
<ServiceCalendarFrame xmlns="http://www.netex.org.uk/netex" version="521" id="SJN:ServiceCalendarFrame:Shared">
     <ServiceCalendar>
     <!-- FromDate after ToDate -->
     <FromDate>2024-02-01</FromDate>
     <ToDate>2024-01-01</ToDate>
     </ServiceCalendar>
     <dayTypes>
       <DayType version="0" id="SJN:DayType:2024-11-28-Thu" />
       <!-- Unused DayType-->
       <DayType version="0" id="SJN:DayType:2024-11-29-Fre" />
     </dayTypes>
     <operatingDays>
       <OperatingDay version="0" id="SJN:OperatingDay:2024-11-28-Thu">
         <CalendarDate>2024-11-28</CalendarDate>
       </OperatingDay>
     </operatingDays>
     <dayTypeAssignments>
       <DayTypeAssignment order="0" version="0" id="SJN:DayTypeAssignment:2024-11-28-Thu">
         <Date>2024-11-28</Date>
         <DayTypeRef ref="SJN:DayType:2024-11-28-Thu" version="0" />
       </DayTypeAssignment>
       
     </dayTypeAssignments>
</ServiceCalendarFrame>
        
        """;

  private static final String NETEX_FRAGMENT_VALID =
    """
<ServiceCalendarFrame xmlns="http://www.netex.org.uk/netex" version="521" id="SJN:ServiceCalendarFrame:Shared">
     <ServiceCalendar>
     <FromDate>2024-01-01</FromDate>
     <ToDate>2024-01-02</ToDate>
      <dayTypes/>
     </ServiceCalendar>
     <dayTypes>
       <DayType version="0" id="SJN:DayType:2024-11-28-Thu" />
     </dayTypes>
     <operatingDays>
       <OperatingDay version="0" id="SJN:OperatingDay:2024-11-28-Thu">
         <CalendarDate>2024-11-28</CalendarDate>
       </OperatingDay>
     </operatingDays>
     <dayTypeAssignments>
       <DayTypeAssignment order="0" version="0" id="SJN:DayTypeAssignment:2024-11-28-Thu">
         <Date>2024-11-28</Date>
         <DayTypeRef ref="SJN:DayType:2024-11-28-Thu" version="0" />
       </DayTypeAssignment>
       
     </dayTypeAssignments>
</ServiceCalendarFrame>
              
              """;

  private ValidationTree validationTree;

  @BeforeEach
  void setUp() {
    validationTree =
      new DefaultServiceCalendarFrameValidationTreeFactory().builder().build();
  }

  static Stream<String> ruleCodes() {
    return Stream.of(
      CODE_SERVICE_CALENDAR_1,
      CODE_SERVICE_CALENDAR_2,
      CODE_SERVICE_CALENDAR_5
    );
  }

  @ParameterizedTest
  @MethodSource("ruleCodes")
  void testInvalidServiceCalendarFrame(String code) {
    XPathRuleValidationContext xpathValidationContext =
      TestValidationContextBuilder
        .ofNetexFragment(NETEX_FRAGMENT_INVALID)
        .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      code
    );
    assertEquals(1, validationIssues.size());
    assertEquals(code, validationIssues.get(0).rule().code());
  }

  @ParameterizedTest
  @MethodSource("ruleCodes")
  void testValidServiceCalendarFrame(String code) {
    XPathRuleValidationContext xpathValidationContext =
      TestValidationContextBuilder
        .ofNetexFragment(NETEX_FRAGMENT_VALID)
        .build();
    List<ValidationIssue> validationIssues = validationTree.validate(
      xpathValidationContext,
      code
    );
    assertTrue(validationIssues.isEmpty());
  }
}

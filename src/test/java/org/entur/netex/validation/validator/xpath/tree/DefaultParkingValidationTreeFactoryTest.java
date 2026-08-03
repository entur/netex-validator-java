package org.entur.netex.validation.validator.xpath.tree;

import static org.entur.netex.validation.validator.xpath.tree.DefaultParkingValidationTreeFactory.CODE_PARKING_AVAILABILITY_CONDITION_WITHOUT_DAY_TYPE;
import static org.entur.netex.validation.validator.xpath.tree.DefaultParkingValidationTreeFactory.CODE_PARKING_TIMEBAND_INVALID_TIME_RANGE;
import static org.entur.netex.validation.validator.xpath.tree.DefaultParkingValidationTreeFactory.CODE_PARKING_TIMEBAND_WITHOUT_START_OR_END_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.entur.netex.validation.test.xpath.support.TestValidationContextBuilder;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultParkingValidationTreeFactoryTest {

  private static final String NETEX_FRAGMENT_VALID =
    """
        <Parking xmlns="http://www.netex.org.uk/netex" version="1" id="FSR:Parking:liipi-1">
          <validityConditions>
            <AvailabilityCondition version="1" id="FSR:Parking:liipi-1:AvailabilityCondition:1">
              <IsAvailable>true</IsAvailable>
              <dayTypes>
                <DayTypeRef ref="FSR:DayType:BusinessDay"/>
              </dayTypes>
              <timebands>
                <Timeband version="1" id="FSR:Parking:liipi-1:Timeband:1">
                  <StartTime>08:00:00</StartTime>
                  <EndTime>20:00:00</EndTime>
                </Timeband>
              </timebands>
            </AvailabilityCondition>
            <AvailabilityCondition version="1" id="FSR:Parking:liipi-1:AvailabilityCondition:2">
              <IsAvailable>false</IsAvailable>
              <dayTypes>
                <DayTypeRef ref="FSR:DayType:Sunday"/>
              </dayTypes>
            </AvailabilityCondition>
          </validityConditions>
        </Parking>
        """;

  private static final String NETEX_FRAGMENT_MIDNIGHT_OPEN =
    """
        <Parking xmlns="http://www.netex.org.uk/netex" version="1" id="FSR:Parking:liipi-1">
          <validityConditions>
            <AvailabilityCondition version="1" id="FSR:Parking:liipi-1:AvailabilityCondition:1">
              <IsAvailable>true</IsAvailable>
              <dayTypes>
                <DayTypeRef ref="FSR:DayType:BusinessDay"/>
              </dayTypes>
              <timebands>
                <Timeband version="1" id="FSR:Parking:liipi-1:Timeband:1">
                  <StartTime>00:00:00</StartTime>
                  <EndTime>00:00:00</EndTime>
                </Timeband>
              </timebands>
            </AvailabilityCondition>
          </validityConditions>
        </Parking>
        """;

  private static final String NETEX_FRAGMENT_MISSING_DAY_TYPE_REF =
    """
        <Parking xmlns="http://www.netex.org.uk/netex" version="1" id="FSR:Parking:liipi-1">
          <validityConditions>
            <AvailabilityCondition version="1" id="FSR:Parking:liipi-1:AvailabilityCondition:1">
              <IsAvailable>true</IsAvailable>
              <timebands>
                <Timeband version="1" id="FSR:Parking:liipi-1:Timeband:1">
                  <StartTime>08:00:00</StartTime>
                  <EndTime>20:00:00</EndTime>
                </Timeband>
              </timebands>
            </AvailabilityCondition>
          </validityConditions>
        </Parking>
        """;

  private static final String NETEX_FRAGMENT_MISSING_END_TIME =
    """
        <Parking xmlns="http://www.netex.org.uk/netex" version="1" id="FSR:Parking:liipi-1">
          <validityConditions>
            <AvailabilityCondition version="1" id="FSR:Parking:liipi-1:AvailabilityCondition:1">
              <IsAvailable>true</IsAvailable>
              <dayTypes>
                <DayTypeRef ref="FSR:DayType:BusinessDay"/>
              </dayTypes>
              <timebands>
                <Timeband version="1" id="FSR:Parking:liipi-1:Timeband:1">
                  <StartTime>08:00:00</StartTime>
                </Timeband>
              </timebands>
            </AvailabilityCondition>
          </validityConditions>
        </Parking>
        """;

  private static final String NETEX_FRAGMENT_INVALID_TIME_RANGE =
    """
        <Parking xmlns="http://www.netex.org.uk/netex" version="1" id="FSR:Parking:liipi-1">
          <validityConditions>
            <AvailabilityCondition version="1" id="FSR:Parking:liipi-1:AvailabilityCondition:1">
              <IsAvailable>true</IsAvailable>
              <dayTypes>
                <DayTypeRef ref="FSR:DayType:BusinessDay"/>
              </dayTypes>
              <timebands>
                <Timeband version="1" id="FSR:Parking:liipi-1:Timeband:1">
                  <StartTime>20:00:00</StartTime>
                  <EndTime>08:00:00</EndTime>
                </Timeband>
              </timebands>
            </AvailabilityCondition>
          </validityConditions>
        </Parking>
        """;

  private ValidationTree validationTree;

  @BeforeEach
  void setUp() {
    validationTree = new DefaultParkingValidationTreeFactory().builder().build();
  }

  @Test
  void testValidParkingWithOpeningHours() {
    XPathRuleValidationContext context = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_VALID)
      .build();
    assertTrue(validationTree.validate(context).isEmpty());
  }

  @Test
  void testValidParkingWithMidnightOpenHours() {
    XPathRuleValidationContext context = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_MIDNIGHT_OPEN)
      .build();
    assertTrue(
      validationTree.validate(context, CODE_PARKING_TIMEBAND_INVALID_TIME_RANGE).isEmpty()
    );
  }

  @Test
  void testAvailabilityConditionWithoutDayTypeRef() {
    XPathRuleValidationContext context = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_MISSING_DAY_TYPE_REF)
      .build();
    List<ValidationIssue> issues = validationTree.validate(
      context,
      CODE_PARKING_AVAILABILITY_CONDITION_WITHOUT_DAY_TYPE
    );
    assertEquals(1, issues.size());
    assertEquals(
      CODE_PARKING_AVAILABILITY_CONDITION_WITHOUT_DAY_TYPE,
      issues.get(0).rule().code()
    );
  }

  @Test
  void testTimebandWithoutEndTime() {
    XPathRuleValidationContext context = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_MISSING_END_TIME)
      .build();
    List<ValidationIssue> issues = validationTree.validate(
      context,
      CODE_PARKING_TIMEBAND_WITHOUT_START_OR_END_TIME
    );
    assertEquals(1, issues.size());
    assertEquals(
      CODE_PARKING_TIMEBAND_WITHOUT_START_OR_END_TIME,
      issues.get(0).rule().code()
    );
  }

  @Test
  void testTimebandWithEndTimeBeforeStartTime() {
    XPathRuleValidationContext context = TestValidationContextBuilder
      .ofNetexFragment(NETEX_FRAGMENT_INVALID_TIME_RANGE)
      .build();
    List<ValidationIssue> issues = validationTree.validate(
      context,
      CODE_PARKING_TIMEBAND_INVALID_TIME_RANGE
    );
    assertEquals(1, issues.size());
    assertEquals(CODE_PARKING_TIMEBAND_INVALID_TIME_RANGE, issues.get(0).rule().code());
  }
}

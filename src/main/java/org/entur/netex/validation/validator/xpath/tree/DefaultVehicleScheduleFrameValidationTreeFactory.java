package org.entur.netex.validation.validator.xpath.tree;

import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.xpath.ValidationTree;
import org.entur.netex.validation.validator.xpath.ValidationTreeFactory;
import org.entur.netex.validation.validator.xpath.rules.ValidateAtLeastOne;
import org.entur.netex.validation.validator.xpath.rules.ValidateNotExist;

public class DefaultVehicleScheduleFrameValidationTreeFactory
  implements ValidationTreeFactory {

  static final String CODE_BLOCK_1 = "BLOCK_1";
  static final String CODE_BLOCK_2 = "BLOCK_2";
  static final String CODE_BLOCK_3 = "BLOCK_3";

  @Override
  public ValidationTree buildValidationTree() {
    ValidationTreeBuilder builder = new ValidationTreeBuilder(
      "VehicleScheduleFrame",
      "Vehicle Schedule Frame"
    );

    return builder
      .withRule(
        new ValidateAtLeastOne(
          "blocks/Block | blocks/TrainBlock",
          CODE_BLOCK_1,
          "Block missing VehicleScheduleFrame",
          "At least one Block or TrainBlock required in VehicleScheduleFrame",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "blocks/Block[not(journeys)]",
          CODE_BLOCK_2,
          "Block missing Journey",
          "At least one Journey must be defined for Block",
          Severity.ERROR
        )
      )
      .withRule(
        new ValidateNotExist(
          "blocks/Block[not(dayTypes)]",
          CODE_BLOCK_3,
          "Block missing DayType",
          "At least one DayType must be defined for Block",
          Severity.WARNING
        )
      )
      .build();
  }
}

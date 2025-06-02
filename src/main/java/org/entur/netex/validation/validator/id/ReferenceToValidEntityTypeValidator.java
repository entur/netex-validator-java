package org.entur.netex.validation.validator.id;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.entur.netex.validation.validator.Severity;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.ValidationRule;
import org.entur.netex.validation.validator.XPathValidator;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validate that NeTEX references point to a valid element type.
 */
public class ReferenceToValidEntityTypeValidator implements XPathValidator {

  static final ValidationRule RULE_INVALID_REFERENCE = new ValidationRule(
    "NETEX_ID_6",
    "NeTEx ID reference to invalid element",
    "Reference to %s is not allowed from element %s. Generally an element named XXXXRef may only reference elements if type XXXX",
    Severity.ERROR
  );

  static final ValidationRule RULE_INVALID_ID_STRUCTURE = new ValidationRule(
    "NETEX_ID_7",
    "NeTEx ID invalid value",
    "Invalid id structure on element",
    Severity.ERROR
  );

  private static final Logger LOGGER = LoggerFactory.getLogger(
    ReferenceToValidEntityTypeValidator.class
  );

  private final Map<String, Set<String>> allowedSubstitutions;

  public ReferenceToValidEntityTypeValidator() {
    this.allowedSubstitutions = getAllowedSubstitutions();
  }

  @Override
  public List<ValidationIssue> validate(XPathValidationContext xPathValidationContext) {
    LOGGER.debug(
      "Validating file {} in report {}",
      xPathValidationContext.getFileName(),
      xPathValidationContext.getValidationReportId()
    );
    return validate(xPathValidationContext.getLocalRefs());
  }

  protected List<ValidationIssue> validate(List<IdVersion> localRefs) {
    List<ValidationIssue> validationIssues = new ArrayList<>();

    for (IdVersion id : localRefs) {
      String referencingElement = id.getElementName();
      String[] idParts = id.getId().split(":");
      if (idParts.length == 3) {
        String referencedElement = idParts[1];
        // The general rule is that an element <XXX> should be referenced from an element <XXXRef> or <DefaultXXXRef>
        // unless it is explicitly listed in the allowed substitutions.
        if (
          !(referencedElement + "Ref").equals(referencingElement) &&
          !("Default" + referencedElement + "Ref").equals(referencingElement) &&
          !canSubstitute(referencingElement, referencedElement)
        ) {
          validationIssues.add(
            new ValidationIssue(
              RULE_INVALID_REFERENCE,
              id.dataLocation(),
              referencedElement,
              referencingElement
            )
          );
        }
      } else {
        validationIssues.add(
          new ValidationIssue(RULE_INVALID_ID_STRUCTURE, id.dataLocation())
        );
      }
    }
    return validationIssues;
  }

  private boolean canSubstitute(String referencingElement, String referencedElement) {
    Set<String> possibleSubstitutions = allowedSubstitutions.get(referencingElement);
    return (
      possibleSubstitutions != null && possibleSubstitutions.contains(referencedElement)
    );
  }

  private Map<String, Set<String>> getAllowedSubstitutions() {
    Map<String, Set<String>> substitutions = new HashMap<>();

    Set<String> groupOfLinesRefSubstitutions = new HashSet<>();
    groupOfLinesRefSubstitutions.add("Network");
    groupOfLinesRefSubstitutions.add("GroupOfLines");
    substitutions.put("RepresentedByGroupRef", groupOfLinesRefSubstitutions);

    Set<String> inverseRouteRefSubstitutions = new HashSet<>();
    inverseRouteRefSubstitutions.add("Route");
    substitutions.put("InverseRouteRef", inverseRouteRefSubstitutions);

    Set<String> projectedPointRefSubstitutions = new HashSet<>();
    projectedPointRefSubstitutions.add("ScheduledStopPoint");
    projectedPointRefSubstitutions.add("RoutePoint");
    projectedPointRefSubstitutions.add("TimingPoint");
    substitutions.put("ProjectToPointRef", projectedPointRefSubstitutions);
    substitutions.put("ProjectedPointRef", projectedPointRefSubstitutions);
    substitutions.put("ToPointRef", projectedPointRefSubstitutions);
    substitutions.put("FromPointRef", projectedPointRefSubstitutions);
    substitutions.put("StartPointRef", projectedPointRefSubstitutions);
    substitutions.put("EndPointRef", projectedPointRefSubstitutions);

    Set<String> noticedObjectRefSubstitutions = new HashSet<>();
    noticedObjectRefSubstitutions.add("Line");
    noticedObjectRefSubstitutions.add("FlexibleLine");
    noticedObjectRefSubstitutions.add("ServiceJourney");
    noticedObjectRefSubstitutions.add("JourneyPattern");
    noticedObjectRefSubstitutions.add("ServiceJourneyPattern");
    noticedObjectRefSubstitutions.add("StopPointInJourneyPattern");
    noticedObjectRefSubstitutions.add("TimetabledPassingTime");
    substitutions.put("NoticedObjectRef", noticedObjectRefSubstitutions);

    Set<String> toAndFromJourneyRefSubstitutions = new HashSet<>();
    toAndFromJourneyRefSubstitutions.add("ServiceJourney");
    toAndFromJourneyRefSubstitutions.add("DatedServiceJourney");
    substitutions.put("ToJourneyRef", toAndFromJourneyRefSubstitutions);
    substitutions.put("FromJourneyRef", toAndFromJourneyRefSubstitutions);

    Set<String> vehicleScheduleJourneyRefSubstitutions = new HashSet<>(
      toAndFromJourneyRefSubstitutions
    );
    vehicleScheduleJourneyRefSubstitutions.add("VehicleJourney");
    vehicleScheduleJourneyRefSubstitutions.add("DeadRun");
    substitutions.put("VehicleJourneyRef", vehicleScheduleJourneyRefSubstitutions);

    Set<String> datedVehicleJourneyRefSubstitutions = new HashSet<>();
    datedVehicleJourneyRefSubstitutions.add("DatedServiceJourney");
    substitutions.put(
      "DatedVehicleJourneyRef",
      datedVehicleJourneyRefSubstitutions
    );

    Set<String> serviceJourneyPatternRefSubstitutions = new HashSet<>();
    serviceJourneyPatternRefSubstitutions.add("ServiceJourneyPattern");
    substitutions.put("JourneyPatternRef", serviceJourneyPatternRefSubstitutions);

    Set<String> lineRefSubstitutions = new HashSet<>();
    lineRefSubstitutions.add("FlexibleLine");
    substitutions.put("LineRef", lineRefSubstitutions);

    Set<String> mainPartRefSubstitutions = new HashSet<>();
    mainPartRefSubstitutions.add("JourneyPart");
    substitutions.put("MainPartRef", mainPartRefSubstitutions);

    Set<String> fromStopPointRefSubstitutions = new HashSet<>();
    fromStopPointRefSubstitutions.add("ScheduledStopPoint");
    substitutions.put("FromStopPointRef", fromStopPointRefSubstitutions);

    Set<String> fromOperatingDayRefSubstitutions = new HashSet<>();
    fromOperatingDayRefSubstitutions.add("OperatingDay");
    substitutions.put("FromOperatingDayRef", fromOperatingDayRefSubstitutions);

    Set<String> toOperatingDayRefSubstitutions = new HashSet<>();
    toOperatingDayRefSubstitutions.add("OperatingDay");
    substitutions.put("ToOperatingDayRef", toOperatingDayRefSubstitutions);

    Set<String> toStopPointRefSubstitutions = new HashSet<>();
    toStopPointRefSubstitutions.add("ScheduledStopPoint");
    substitutions.put("ToStopPointRef", toStopPointRefSubstitutions);

    Set<String> organisationRefSubstitutions = new HashSet<>();
    organisationRefSubstitutions.add("Authority");
    substitutions.put("OrganisationRef", organisationRefSubstitutions);

    Set<String> placeRefSubstitutions = new HashSet<>();
    placeRefSubstitutions.add("StopPlace");
    placeRefSubstitutions.add("Quay");
    substitutions.put("PlaceRef", placeRefSubstitutions);
    substitutions.put("ParentSiteRef", placeRefSubstitutions);

    Set<String> equipmentRefSubstitutions = new HashSet<>();
    equipmentRefSubstitutions.add("AccessVehicleEquipment");
    substitutions.put("EquipmentRef", equipmentRefSubstitutions);

    return substitutions;
  }

  @Override
  public Set<ValidationRule> getRules() {
    return Set.of(RULE_INVALID_REFERENCE, RULE_INVALID_ID_STRUCTURE);
  }
}

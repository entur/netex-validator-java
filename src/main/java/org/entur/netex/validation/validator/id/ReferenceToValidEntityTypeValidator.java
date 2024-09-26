package org.entur.netex.validation.validator.id;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.entur.netex.validation.validator.AbstractXPathNetexValidator;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntryFactory;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validate that NeTEX references point to a valid element type.
 */
public class ReferenceToValidEntityTypeValidator
  extends AbstractXPathNetexValidator {

  static final String RULE_CODE_NETEX_ID_6 = "NETEX_ID_6";
  static final String RULE_CODE_NETEX_ID_7 = "NETEX_ID_7";

  private static final String MESSAGE_FORMAT_INVALID_REFERENCE =
    "Reference to %s is not allowed from element %s. Generally an element named XXXXRef may only reference elements if type XXXX";
  private static final String MESSAGE_FORMAT_INVALID_ID_STRUCTURE =
    "Invalid id structure on element";
  private static final Logger LOGGER = LoggerFactory.getLogger(
    ReferenceToValidEntityTypeValidator.class
  );

  private final Map<String, Set<String>> allowedSubstitutions;

  public ReferenceToValidEntityTypeValidator(
    ValidationReportEntryFactory validationReportEntryFactory
  ) {
    super(validationReportEntryFactory);
    this.allowedSubstitutions = getAllowedSubstitutions();
  }

  @Override
  public void validate(
    ValidationReport validationReport,
    XPathValidationContext xPathValidationContext
  ) {
    LOGGER.debug(
      "Validating file {} in report {}",
      xPathValidationContext.getFileName(),
      validationReport.getValidationReportId()
    );
    validationReport.addAllValidationReportEntries(
      validate(xPathValidationContext.getLocalRefs())
    );
  }

  protected List<ValidationReportEntry> validate(List<IdVersion> localRefs) {
    List<ValidationReportEntry> validationReportEntries = new ArrayList<>();

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
          String validationReportEntryMessage = String.format(
            MESSAGE_FORMAT_INVALID_REFERENCE,
            referencedElement,
            referencingElement
          );
          DataLocation dataLocation = getIdVersionLocation(id);
          validationReportEntries.add(
            createValidationReportEntry(
              RULE_CODE_NETEX_ID_6,
              dataLocation,
              validationReportEntryMessage
            )
          );
        }
      } else {
        DataLocation dataLocation = getIdVersionLocation(id);
        validationReportEntries.add(
          createValidationReportEntry(
            RULE_CODE_NETEX_ID_7,
            dataLocation,
            MESSAGE_FORMAT_INVALID_ID_STRUCTURE
          )
        );
      }
    }
    return validationReportEntries;
  }

  private boolean canSubstitute(
    String referencingElement,
    String referencedElement
  ) {
    Set<String> possibleSubstitutions = allowedSubstitutions.get(
      referencingElement
    );
    return (
      possibleSubstitutions != null &&
      possibleSubstitutions.contains(referencedElement)
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
    substitutions.put(
      "VehicleJourneyRef",
      vehicleScheduleJourneyRefSubstitutions
    );

    Set<String> serviceJourneyPatternRefSubstitutions = new HashSet<>();
    serviceJourneyPatternRefSubstitutions.add("ServiceJourneyPattern");
    substitutions.put(
      "JourneyPatternRef",
      serviceJourneyPatternRefSubstitutions
    );

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

    Set<String> equipmentRefSubstitutions = new HashSet<>();
    equipmentRefSubstitutions.add("AccessVehicleEquipment");
    substitutions.put("EquipmentRef", equipmentRefSubstitutions);

    return substitutions;
  }

  @Override
  public Set<String> getRuleDescriptions() {
    return Set.of(
      createRuleDescription(
        RULE_CODE_NETEX_ID_6,
        MESSAGE_FORMAT_INVALID_REFERENCE
      ),
      createRuleDescription(
        RULE_CODE_NETEX_ID_7,
        MESSAGE_FORMAT_INVALID_ID_STRUCTURE
      )
    );
  }
}

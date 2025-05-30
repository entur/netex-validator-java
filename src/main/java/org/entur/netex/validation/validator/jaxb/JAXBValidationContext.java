package org.entur.netex.validation.validator.jaxb;

import java.util.*;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.ValidationContext;
import org.entur.netex.validation.validator.id.IdVersion;
import org.entur.netex.validation.validator.jaxb.support.DatedServiceJourneyUtils;
import org.entur.netex.validation.validator.model.*;
import org.rutebanken.netex.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validation context for JAXB-based validators.
 */
public class JAXBValidationContext implements ValidationContext {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    JAXBValidationContext.class
  );

  private final String validationReportId;
  private final NetexEntitiesIndex netexEntitiesIndex;
  private final CommonDataRepository commonDataRepository;
  private final StopPlaceRepository stopPlaceRepository;
  private final String codespace;
  private final String fileName;
  private final Map<String, IdVersion> localIdsMap;

  public JAXBValidationContext(
    String validationReportId,
    NetexEntitiesIndex netexEntitiesIndex,
    CommonDataRepository commonDataRepository,
    Function<JAXBValidationContext, StopPlaceRepository> stopPlaceRepositoryFunction,
    String codespace,
    String fileName,
    Map<String, IdVersion> localIdsMap
  ) {
    this.validationReportId = validationReportId;
    this.netexEntitiesIndex = netexEntitiesIndex;
    this.commonDataRepository = commonDataRepository;
    if (stopPlaceRepositoryFunction != null) {
      this.stopPlaceRepository = stopPlaceRepositoryFunction.apply(this);
    } else {
      this.stopPlaceRepository = null;
    }

    this.codespace = codespace;
    this.fileName = fileName;
    this.localIdsMap = localIdsMap;
  }

  @Override
  public String getFileName() {
    return fileName;
  }

  @Override
  public String getCodespace() {
    return codespace;
  }

  public NetexEntitiesIndex getNetexEntitiesIndex() {
    return netexEntitiesIndex;
  }

  public String getValidationReportId() {
    return validationReportId;
  }

  public Map<String, IdVersion> getLocalIdsMap() {
    return localIdsMap;
  }

  public DataLocation dataLocation(String entityId) {
    IdVersion idVersion = localIdsMap.get(entityId);

    return idVersion != null
      ? new DataLocation(
        idVersion.getId(),
        fileName,
        idVersion.getLineNumber(),
        idVersion.getColumnNumber()
      )
      : new DataLocation(entityId, fileName, 0, 0);
  }

  /**
   * Find the quay id for the given scheduled stop point.
   * If the quay id is not found in the common data repository,
   * it will be looked up from the netex entities index.
   */
  @Nullable
  public QuayId quayIdForScheduledStopPoint(
    ScheduledStopPointId scheduledStopPointId
  ) {
    if (scheduledStopPointId == null) {
      return null;
    }
    return commonDataRepository.hasSharedScheduledStopPoints(validationReportId)
      ? commonDataRepository.quayIdForScheduledStopPoint(
        scheduledStopPointId,
        validationReportId
      )
      : QuayId.ofValidId(
        netexEntitiesIndex
          .getQuayIdByStopPointRefIndex()
          .get(scheduledStopPointId.id())
      );
  }

  /**
   * Return the coordinates for a given Quay.
   */
  @Nullable
  public QuayCoordinates coordinatesForQuayId(QuayId quayId) {
    return stopPlaceRepository.getCoordinatesForQuayId(quayId);
  }

  /**
   * Return the transport mode for a given Quay.
   */
  @Nullable
  public TransportModeAndSubMode transportModeAndSubModeForQuayId(
    QuayId quayId
  ) {
    return stopPlaceRepository.getTransportModesForQuayId(quayId);
  }

  /**
   * Return the coordinates for a given scheduledStopPoint.
   */
  @Nullable
  public QuayCoordinates coordinatesForScheduledStopPoint(
    ScheduledStopPointId scheduledStopPointId
  ) {
    QuayId quayId = quayIdForScheduledStopPoint(scheduledStopPointId);
    return quayId == null
      ? null
      : stopPlaceRepository.getCoordinatesForQuayId(quayId);
  }

  /**
   * Return all flexible stop places in the current file.
   */
  public Collection<FlexibleStopPlace> flexibleStopPlaces() {
    return Collections.unmodifiableCollection(
      netexEntitiesIndex.getFlexibleStopPlaceIndex().getAll()
    );
  }

  /**
   * Return all lines in the current file.
   */
  public Collection<Line> lines() {
    return Collections.unmodifiableCollection(
      netexEntitiesIndex.getLineIndex().getAll()
    );
  }

  /**
   * Return all flexible lines in the current file.
   */
  public Collection<FlexibleLine> flexibleLines() {
    return Collections.unmodifiableCollection(
      netexEntitiesIndex.getFlexibleLineIndex().getAll()
    );
  }

  /**
   * Return the name of the stop place referenced by a given ScheduledStopPoint.
   */
  @Nullable
  public String stopPointName(ScheduledStopPointId scheduledStopPointId) {
    QuayId quayId = quayIdForScheduledStopPoint(scheduledStopPointId);
    if (quayId == null) {
      LOGGER.debug(
        "Stop place name cannot be found due to missing stop point assignment."
      );
      return Optional
        .ofNullable(scheduledStopPointId)
        .map(ScheduledStopPointId::id)
        .orElse(null);
    }
    return Optional
      .ofNullable(stopPlaceRepository.getStopPlaceNameForQuayId(quayId))
      .orElse(quayId.id());
  }

  /**
   * Return all journey patterns in the current file.
   */
  public Collection<JourneyPattern> journeyPatterns() {
    return Collections.unmodifiableCollection(
      netexEntitiesIndex.getJourneyPatternIndex().getAll()
    );
  }

  /**
   * Return the JourneyPattern for the given ServiceJourney.
   * Missing JourneyPatternRef on ServiceJourney is validated with SERVICE_JOURNEY_10
   */
  @Nullable
  public JourneyPattern journeyPattern(ServiceJourney serviceJourney) {
    return netexEntitiesIndex
      .getJourneyPatternIndex()
      .get(serviceJourney.getJourneyPatternRef().getValue().getRef());
  }

  /**
   * Returns all the ServiceJourneys in the current file.
   */
  public Collection<ServiceJourney> serviceJourneys() {
    return Collections.unmodifiableCollection(
      netexEntitiesIndex.getServiceJourneyIndex().getAll()
    );
  }

  /**
   *
   * @return all the DatedServiceJourneys in the current file.
   *
   */
  public Collection<DatedServiceJourney> datedServiceJourneys() {
    return Collections.unmodifiableCollection(
      netexEntitiesIndex.getDatedServiceJourneyIndex().getAll()
    );
  }

  /**
   * Return DatedServiceJourneys matching a given service alteration.
   */
  public Collection<DatedServiceJourney> datedServiceJourneysByServiceAlteration(
    ServiceAlterationEnumeration serviceAlteration
  ) {
    return datedServiceJourneys()
      .stream()
      .filter(dsj -> dsj.getServiceAlteration() == serviceAlteration)
      .toList();
  }

  /**
   * Return the DatedServiceJourney identified by the given NeTEx id.
   */
  @Nullable
  public DatedServiceJourney datedServiceJourney(String id) {
    return netexEntitiesIndex.getDatedServiceJourneyIndex().get(id);
  }

  /**
   * Return the original DatedServiceJourney of a given DatedServiceJourney.
   */
  @Nullable
  public DatedServiceJourney originalDatedServiceJourney(
    DatedServiceJourney dsj
  ) {
    return netexEntitiesIndex
      .getDatedServiceJourneyIndex()
      .get(DatedServiceJourneyUtils.originalDatedServiceJourneyRef(dsj));
  }

  /**
   *
   * @return all the DeadRuns in the current file.
   */
  public Collection<DeadRun> deadRuns() {
    return Collections.unmodifiableCollection(
      netexEntitiesIndex.getDeadRunIndex().getAll()
    );
  }

  /**
   * Returns the TimetabledPassingTimes for the given ServiceJourney.
   * Missing TimetabledPassingTimes is validated with SERVICE_JOURNEY_3
   */
  public Collection<TimetabledPassingTime> timetabledPassingTimes(
    ServiceJourney serviceJourney
  ) {
    return Optional
      .ofNullable(serviceJourney)
      .map(ServiceJourney::getPassingTimes)
      .map(TimetabledPassingTimes_RelStructure::getTimetabledPassingTime)
      .map(Collections::unmodifiableCollection)
      .orElse(List.of());
  }

  /**
   * Return all service links in the current file.
   */

  public Collection<ServiceLink> serviceLinks() {
    return Collections.unmodifiableCollection(
      netexEntitiesIndex.getServiceLinkIndex().getAll()
    );
  }

  /**
   *
   * Return the ScheduledStopPoint ids at both ends of a ServiceLink.
   */
  public FromToScheduledStopPointId fromToScheduledStopPointIdForServiceLink(
    ServiceLinkId serviceLinkId
  ) {
    return commonDataRepository.fromToScheduledStopPointIdForServiceLink(
      serviceLinkId,
      validationReportId
    );
  }

  /**
   * Returns all ServiceJourneyInterchanges in the current file.
   */
  public Collection<ServiceJourneyInterchange> serviceJourneyInterchanges() {
    return Collections.unmodifiableCollection(
      netexEntitiesIndex.getServiceJourneyInterchangeIndex().getAll()
    );
  }

  public boolean hasCompositeFrames() {
    return !netexEntitiesIndex.getCompositeFrames().isEmpty();
  }

  public Collection<CompositeFrame> compositeFrames() {
    return Collections.unmodifiableCollection(
      netexEntitiesIndex.getCompositeFrames()
    );
  }

  public boolean hasServiceCalendarFrames() {
    return !netexEntitiesIndex.getServiceCalendarFrames().isEmpty();
  }

  public Collection<ServiceCalendarFrame> serviceCalendarFrames() {
    return Collections.unmodifiableCollection(
      netexEntitiesIndex.getServiceCalendarFrames()
    );
  }

  /**
   * Find the transport mode for the given service journey.
   * If the transport mode is not set on the service journey,
   * it will be looked up from the line or flexible line.
   */
  @Nullable
  public TransportModeAndSubMode transportModeAndSubMode(
    ServiceJourney serviceJourney
  ) {
    AllVehicleModesOfTransportEnumeration transportMode =
      serviceJourney.getTransportMode();

    TransportSubmodeStructure subModeStructure =
      serviceJourney.getTransportSubmode();

    if (transportMode == null) {
      JourneyPattern journeyPattern = journeyPattern(serviceJourney);
      return transportModeAndSubMode(journeyPattern);
    }
    return TransportModeAndSubMode.of(transportMode, subModeStructure);
  }

  /**
   * Find the transport mode for the given journey pattern.
   * it will be looked up from the line or flexible line with FIXED Type
   */
  @Nullable
  public TransportModeAndSubMode transportModeAndSubMode(
    JourneyPattern journeyPattern
  ) {
    Route route = netexEntitiesIndex
      .getRouteIndex()
      .get(journeyPattern.getRouteRef().getRef());
    Line line = netexEntitiesIndex
      .getLineIndex()
      .get(route.getLineRef().getValue().getRef());

    if (line != null) {
      return TransportModeAndSubMode.of(
        line.getTransportMode(),
        line.getTransportSubmode()
      );
    }

    FlexibleLine flexibleLine = netexEntitiesIndex
      .getFlexibleLineIndex()
      .get(route.getLineRef().getValue().getRef());

    return TransportModeAndSubMode.of(
      flexibleLine.getTransportMode(),
      flexibleLine.getTransportSubmode()
    );
  }

  /**
   * Returns the FlexibleStopPlaceRef connected to ScheduledStopPointRef in a FlexibleStopAssignment.
   */
  @Nullable
  public String flexibleStopPlaceRefFromScheduledStopPointRef(
    String scheduledStopPointRef
  ) {
    String flexibleStopPlaceRefFromCommon =
      commonDataRepository.getFlexibleStopPlaceRefByStopPointRef(
        validationReportId,
        scheduledStopPointRef
      );

    String flexibleStopPlaceRefFromNetexEntities = netexEntitiesIndex
      .getFlexibleStopPlaceIdByStopPointRefIndex()
      .get(scheduledStopPointRef);

    if (flexibleStopPlaceRefFromNetexEntities == null) {
      return flexibleStopPlaceRefFromCommon;
    }

    return flexibleStopPlaceRefFromNetexEntities;
  }
}

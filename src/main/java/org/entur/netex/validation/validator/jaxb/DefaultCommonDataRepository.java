package org.entur.netex.validation.validator.jaxb;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex.validation.validator.model.FromToScheduledStopPointId;
import org.entur.netex.validation.validator.model.QuayId;
import org.entur.netex.validation.validator.model.ScheduledStopPointId;
import org.entur.netex.validation.validator.model.ServiceLinkId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of CommonDataRepository.
 * This repository is used to store and retrieve data collected from common files.
 */
public class DefaultCommonDataRepository implements CommonDataRepositoryLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    DefaultCommonDataRepository.class
  );

  private final Map<String, Map<String, String>> scheduledStopPointAndQuayIdCache;
  private final Map<String, Map<String, String>> serviceLinksAndFromToScheduledStopPointIdCache;
  private final Map<String, Map<String, String>> scheduledStopPointToFlexibleStopPlaceCache;

  /**
   * The default constructor initializes synchronized data structures for storing the common data.
   */
  public DefaultCommonDataRepository() {
    this(
      Collections.synchronizedMap(new HashMap<>()),
      Collections.synchronizedMap(new HashMap<>()),
      Collections.synchronizedMap(new HashMap<>())
    );
  }

  /**
   * Create a common data repository that uses arbitrary data structures for storing common data.
   * This can be used for implementing distributed storage in a memory store.
   */
  public DefaultCommonDataRepository(
    Map<String, Map<String, String>> scheduledStopPointAndQuayIdCache,
    Map<String, Map<String, String>> serviceLinksAndFromToScheduledStopPointIdCache,
    Map<String, Map<String, String>> scheduledStopPointToFlexibleStopPlaceCache
  ) {
    this.scheduledStopPointAndQuayIdCache = scheduledStopPointAndQuayIdCache;
    this.serviceLinksAndFromToScheduledStopPointIdCache =
      serviceLinksAndFromToScheduledStopPointIdCache;
    this.scheduledStopPointToFlexibleStopPlaceCache =
      scheduledStopPointToFlexibleStopPlaceCache;
  }

  @Override
  public String getFlexibleStopPlaceRefByStopPointRef(
    String validationReportId,
    String stopPointRef
  ) {
    Map<String, String> stopPlaceRefToFlexibleStopPlaceMap =
      scheduledStopPointToFlexibleStopPlaceCache.get(validationReportId);
    if (stopPlaceRefToFlexibleStopPlaceMap == null) {
      throw new NetexValidationException(
        "Flexible stop place cache not found for validation report with id: " +
        validationReportId
      );
    }
    return stopPlaceRefToFlexibleStopPlaceMap.get(stopPointRef);
  }

  @Override
  public boolean hasSharedScheduledStopPoints(String validationReportId) {
    Map<String, String> idsForReport = scheduledStopPointAndQuayIdCache.get(
      validationReportId
    );
    return idsForReport != null && !idsForReport.isEmpty();
  }

  @Override
  public QuayId quayIdForScheduledStopPoint(
    ScheduledStopPointId scheduledStopPointId,
    String validationReportId
  ) {
    Map<String, String> idsForReport = scheduledStopPointAndQuayIdCache.get(
      validationReportId
    );
    if (idsForReport == null) {
      throw new NetexValidationException(
        "Quay ids cache not found for validation report with id: " +
        validationReportId
      );
    }
    return QuayId.ofValidId(idsForReport.get(scheduledStopPointId.id()));
  }

  @Override
  public FromToScheduledStopPointId fromToScheduledStopPointIdForServiceLink(
    ServiceLinkId serviceLinkId,
    String validationReportId
  ) {
    Map<String, String> idsForReport =
      serviceLinksAndFromToScheduledStopPointIdCache.get(validationReportId);
    if (idsForReport == null) {
      throw new NetexValidationException(
        "Service links cache not found for validation report with id: " +
        validationReportId
      );
    }
    return FromToScheduledStopPointId.fromString(
      idsForReport.get(serviceLinkId.id())
    );
  }

  @Override
  public void collect(
    String validationReportId,
    NetexEntitiesIndex netexEntitiesIndex
  ) {
    // Merging with the existing map, for handing the case where there are
    // multiple common files in the dataset.
    scheduledStopPointAndQuayIdCache.merge(
      validationReportId,
      getQuayIdsPerScheduledStopPoints(netexEntitiesIndex),
      (existingMap, newMap) -> {
        existingMap.putAll(newMap);
        return existingMap;
      }
    );

    scheduledStopPointToFlexibleStopPlaceCache.merge(
      validationReportId,
      netexEntitiesIndex.getFlexibleStopPlaceIdByStopPointRefIndex(),
      (existingMap, newMap) -> {
        existingMap.putAll(newMap);
        return existingMap;
      }
    );

    Map<String, String> scheduledStopPointIdsPerServiceLinkId =
      getFromToScheduledStopPointIdPerServiceLinkId(netexEntitiesIndex)
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

    serviceLinksAndFromToScheduledStopPointIdCache.merge(
      validationReportId,
      scheduledStopPointIdsPerServiceLinkId,
      (existingMap, newMap) -> {
        existingMap.putAll(newMap);
        return existingMap;
      }
    );

    LOGGER.info(
      "{} Quay ids for ScheduledStopPoints cached for validation report with id: {}",
      scheduledStopPointAndQuayIdCache.get(validationReportId).size(),
      validationReportId
    );
  }

  @Override
  public void cleanUp(String validationReportId) {
    scheduledStopPointAndQuayIdCache.remove(validationReportId);
    serviceLinksAndFromToScheduledStopPointIdCache.remove(validationReportId);
  }

  private Map<String, String> getQuayIdsPerScheduledStopPoints(
    NetexEntitiesIndex netexEntitiesIndex
  ) {
    return netexEntitiesIndex
      .getPassengerStopAssignmentsByStopPointRefIndex()
      .values()
      .stream()
      .collect(
        Collectors.toMap(
          passengerStopAssignment ->
            passengerStopAssignment
              .getScheduledStopPointRef()
              .getValue()
              .getRef(),
          passengerStopAssignment ->
            passengerStopAssignment.getQuayRef().getValue().getRef(),
          (v1, v2) -> v2
        )
      );
  }

  private Map<String, String> getFromToScheduledStopPointIdPerServiceLinkId(
    NetexEntitiesIndex netexEntitiesIndex
  ) {
    return netexEntitiesIndex
      .getServiceLinkIndex()
      .getAll()
      .stream()
      .collect(
        Collectors.toMap(
          serviceLink -> ServiceLinkId.of(serviceLink).toString(),
          serviceLink -> FromToScheduledStopPointId.of(serviceLink).toString(),
          (v1, v2) -> v2
        )
      );
  }
}

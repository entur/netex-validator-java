package org.entur.netex.validation.validator.jaxb;

import java.util.List;
import org.entur.netex.validation.validator.model.*;

/**
 * Repository for NeTEx data collected during validation.
 * This is required for validation rules that use data across files.
 * This repository contains common data collected from shared files, and data collected from individual line files.
 */
public interface NetexDataRepository {
  /**
   *
   */
  boolean hasQuayIds(String validationReportId);

  /**
   * Resolve the QuayId referred by a ScheduledStopPoint.
   */
  QuayId quayIdForScheduledStopPoint(
    ScheduledStopPointId scheduledStopPointId,
    String validationReportId
  );

  /**
   * Resolve the "from" and "to" ScheduledStopPoints referred by a ServiceLink.
   */
  FromToScheduledStopPointId fromToScheduledStopPointIdForServiceLink(
    ServiceLinkId serviceLinkId,
    String validationReportId
  );

  /**
   * List the NeTEx Line names in the dataset.
   */
  List<SimpleLine> lineNames(String validationReportId);

  /**
   * List the ServiceJourneyStops for a ServiceJourney.
   */
  public List<ServiceJourneyStop> serviceJourneyStops(
    String validationReportId,
    ServiceJourneyId serviceJourneyId
  );

  /**
   * List the ServiceJourneyInterchangeInfos in the dataset.
   */
  List<ServiceJourneyInterchangeInfo> serviceJourneyInterchangeInfos(
    String validationReportId
  );

  /**
   *
   */
  void fillNetexDataCache(byte[] fileContent, String validationReportId);

  /**
   * Clean up the NeTEx data repository.
   */
  void cleanUp(String validationReportId);
}

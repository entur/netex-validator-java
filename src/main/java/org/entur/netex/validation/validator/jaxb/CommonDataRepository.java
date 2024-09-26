package org.entur.netex.validation.validator.jaxb;

import java.util.List;

/**
 * Repository for common data from the Netex Common file.
 * This repository is used to store and retrieve common data.
 */
public interface CommonDataRepository {
  boolean hasQuayIds(String validationReportId);

  QuayId findQuayIdForScheduledStopPoint(
    ScheduledStopPointId scheduledStopPointId,
    String validationReportId
  );

  ScheduledStopPointIds findScheduledStopPointIdsForServiceLink(
    ServiceLinkId serviceLinkId,
    String validationReportId
  );

  List<LineInfo> getLineNames(String validationReportId);

  void loadCommonDataCache(byte[] fileContent, String validationReportId);

  /**
   * Clean up the common data cache.
   * This method is used to clean up the common data cache.
   * Caution: Not an unused method, referred in camel route AggregateValidationReportsRouteBuilder
   */
  void cleanUp(String validationReportId);
}

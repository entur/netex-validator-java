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
  QuayId findQuayIdForScheduledStopPoint(
    ScheduledStopPointId scheduledStopPointId,
    String validationReportId
  );

  /**
   * Resolve the "from" and "to" ScheduledStopPoints referred by a ServiceLink.
   */
  FromToScheduledStopPointId findFromToScheduledStopPointIdForServiceLink(
    ServiceLinkId serviceLinkId,
    String validationReportId
  );

  /**
   * List the NeTEx Line names in the dataset.
   */
  List<SimpleLine> getLineNames(String validationReportId);

  /**
   *
   */
  void loadCommonDataCache(byte[] fileContent, String validationReportId);

  /**
   * Clean up the NeTEx data repository.
   */
  void cleanUp(String validationReportId);
}

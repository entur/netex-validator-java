package org.entur.netex.validation.validator.jaxb;

import org.entur.netex.validation.validator.model.FromToScheduledStopPointId;
import org.entur.netex.validation.validator.model.QuayId;
import org.entur.netex.validation.validator.model.ScheduledStopPointId;
import org.entur.netex.validation.validator.model.ServiceLinkId;

/**
 * This repository contains common data collected from common files.
 */
public interface CommonDataRepository {
  /**
   * Return true if the common files contain shared scheduled stop points.
   */
  boolean hasSharedScheduledStopPoints(String validationReportId);

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
}

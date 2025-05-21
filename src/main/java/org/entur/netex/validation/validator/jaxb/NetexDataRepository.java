package org.entur.netex.validation.validator.jaxb;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.entur.netex.validation.validator.model.*;

/**
 * Repository for NeTEx data collected during validation.
 * This is required for validation rules that use data across files.
 */
public interface NetexDataRepository {
  /**
   * List the NeTEx Line names in the dataset.
   */
  List<SimpleLine> lineNames(String validationReportId);

  /**
   * ServiceJourneyStops per ServiceJourneyId for a validation report.
   */
  Map<ServiceJourneyId, List<ServiceJourneyStop>> serviceJourneyStops(
    String validationReportId
  );

  /**
   * List the ServiceJourneyInterchangeInfos in the dataset.
   */
  List<ServiceJourneyInterchangeInfo> serviceJourneyInterchangeInfos(
    String validationReportId
  );

  /**
   * List the DayTypes for each ServiceJourneyId in the dataset.
   */
  Map<ServiceJourneyId, List<DayTypeId>> serviceJourneyDayTypes(
    String validationReportId
  );

  /**
   * List the active dates for each DayType/OperatingDay in the dataset.
   */
  Map<ActiveDatesId, ActiveDates> activeDates(String validationReportId);

  /**
   * OperatingDays for ServiceJourney in DatedServiceJourney.
   */
  Map<ServiceJourneyId, List<OperatingDayId>> serviceJourneyOperatingDays(
    String validationReportId
  );

  Map<ServiceJourneyId, List<LocalDateTime>> serviceJourneyIdToActiveDates(
    String validationReportId
  );
}

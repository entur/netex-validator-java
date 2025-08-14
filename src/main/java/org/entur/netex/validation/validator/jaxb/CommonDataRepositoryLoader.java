package org.entur.netex.validation.validator.jaxb;

import org.entur.netex.index.api.NetexEntitiesIndex;

/**
 * This interface extends the read-only {@link CommonDataRepository} interface with methods for populating and cleaning the
 * common data repository.
 */
public interface CommonDataRepositoryLoader extends CommonDataRepository {
  /**
   * Collect common data for the given validation report.
   */
  void collect(String validationReportId, NetexEntitiesIndex netexEntitiesIndex);

  /**
   * Clean up the common data repository for the given validation report.
   */
  void cleanUp(String validationReportId);
}

package org.entur.netex.validation.validator.jaxb;

import java.util.Map;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.validation.validator.ValidationContext;
import org.entur.netex.validation.validator.id.IdVersion;

/**
 * Validation context for JAXB-based validators.
 */
public class JAXBValidationContext implements ValidationContext {

  private final String validationReportId;
  private final NetexEntitiesIndex netexEntitiesIndex;
  private final NetexDataRepository netexDataRepository;
  private final StopPlaceRepository stopPlaceRepository;
  private final String codespace;
  private final String fileName;
  private final Map<String, IdVersion> localIdsMap;

  public JAXBValidationContext(
    String validationReportId,
    NetexEntitiesIndex netexEntitiesIndex,
    NetexDataRepository netexDataRepository,
    StopPlaceRepository stopPlaceRepository,
    String codespace,
    String fileName,
    Map<String, IdVersion> localIdsMap
  ) {
    this.validationReportId = validationReportId;
    this.netexEntitiesIndex = netexEntitiesIndex;
    this.netexDataRepository = netexDataRepository;
    this.stopPlaceRepository = stopPlaceRepository;
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

  public StopPlaceRepository getStopPlaceRepository() {
    return stopPlaceRepository;
  }

  public NetexDataRepository getNetexDataRepository() {
    return netexDataRepository;
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
}

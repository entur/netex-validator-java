package org.entur.netex.validation.validator.jaxb;

import java.util.Map;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.validation.validator.id.IdVersion;

/**
 * Extends the XPathValidationContext with NetexEntitiesIndex, which is the in memory index of the Netex dataset.
 */
public class JAXBValidationContext {

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
    StopPlaceRepository stopPlaceRepository,
    String codespace,
    String fileName,
    Map<String, IdVersion> localIdsMap
  ) {
    this.validationReportId = validationReportId;
    this.netexEntitiesIndex = netexEntitiesIndex;
    this.commonDataRepository = commonDataRepository;
    this.stopPlaceRepository = stopPlaceRepository;
    this.codespace = codespace;
    this.fileName = fileName;
    this.localIdsMap = localIdsMap;
  }

  public String getFileName() {
    return fileName;
  }

  public String getCodespace() {
    return codespace;
  }

  public StopPlaceRepository getStopPlaceRepository() {
    return stopPlaceRepository;
  }

  public CommonDataRepository getCommonDataRepository() {
    return commonDataRepository;
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

  public boolean isCommonFile() {
    return fileName != null && fileName.startsWith("_");
  }
}

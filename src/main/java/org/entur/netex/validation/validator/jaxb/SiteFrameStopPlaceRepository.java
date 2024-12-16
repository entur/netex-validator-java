package org.entur.netex.validation.validator.jaxb;

import jakarta.annotation.Nullable;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.validation.validator.model.QuayCoordinates;
import org.entur.netex.validation.validator.model.QuayId;
import org.entur.netex.validation.validator.model.StopPlaceId;
import org.entur.netex.validation.validator.model.TransportModeAndSubMode;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;

/**
 * Stop place repository that retrieves stop place data from the SiteFrame in the current file.
 */
public class SiteFrameStopPlaceRepository implements StopPlaceRepository {

  private final NetexEntitiesIndex netexEntitiesIndex;

  public SiteFrameStopPlaceRepository(JAXBValidationContext validationContext) {
    this.netexEntitiesIndex = validationContext.getNetexEntitiesIndex();
  }

  @Override
  public boolean hasStopPlaceId(StopPlaceId stopPlaceId) {
    return (
      netexEntitiesIndex
        .getStopPlaceIndex()
        .getLatestVersion(stopPlaceId.id()) !=
      null
    );
  }

  @Override
  public boolean hasQuayId(QuayId quayId) {
    return (
      netexEntitiesIndex.getQuayIndex().getLatestVersion(quayId.id()) != null
    );
  }

  @Nullable
  @Override
  public TransportModeAndSubMode getTransportModesForQuayId(QuayId quayId) {
    String stopPlaceId = netexEntitiesIndex
      .getStopPlaceIdByQuayIdIndex()
      .get(quayId.id());
    if (stopPlaceId == null) {
      return null;
    }
    StopPlace stopPlace = netexEntitiesIndex
      .getStopPlaceIndex()
      .getLatestVersion(stopPlaceId);
    if (stopPlace == null) {
      return null;
    }
    return TransportModeAndSubMode.of(stopPlace);
  }

  @Nullable
  @Override
  public QuayCoordinates getCoordinatesForQuayId(QuayId quayId) {
    Quay quay = netexEntitiesIndex.getQuayIndex().getLatestVersion(quayId.id());
    if (quay == null) {
      return null;
    }
    return QuayCoordinates.of(quay);
  }

  @Nullable
  @Override
  public String getStopPlaceNameForQuayId(QuayId quayId) {
    String stopPlaceId = netexEntitiesIndex
      .getStopPlaceIdByQuayIdIndex()
      .get(quayId.id());
    if (stopPlaceId == null) {
      return null;
    }
    return netexEntitiesIndex
      .getStopPlaceIndex()
      .getLatestVersion(stopPlaceId)
      .getName()
      .getValue();
  }
}

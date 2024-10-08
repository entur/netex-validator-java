package org.entur.netex.validation.validator.model;

import org.entur.netex.validation.exception.NetexValidationException;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;

/**
 * Latitude and longitude of a NeTEx Quay.
 */
public record QuayCoordinates(double longitude, double latitude) {
  public static QuayCoordinates of(StopPlace stopPlace) {
    if (stopPlace != null && stopPlace.getCentroid() != null) {
      LocationStructure location = stopPlace.getCentroid().getLocation();
      return new QuayCoordinates(
        location.getLongitude().doubleValue(),
        location.getLatitude().doubleValue()
      );
    }
    return null;
  }

  public static QuayCoordinates of(Quay quay) {
    if (quay != null && quay.getCentroid() != null) {
      LocationStructure location = quay.getCentroid().getLocation();
      return new QuayCoordinates(
        location.getLongitude().doubleValue(),
        location.getLatitude().doubleValue()
      );
    }
    return null;
  }

  /** Return Antu domain coordinate as JTS GeoTools Library coordinate. */
  public Coordinate asJtsCoordinate() {
    return new Coordinate(longitude, latitude);
  }

  @Override
  public String toString() {
    return longitude + "§" + latitude;
  }

  public static QuayCoordinates fromString(String quayCoordinates) {
    if (quayCoordinates != null) {
      String[] split = quayCoordinates.split("§");
      if (split.length == 2) {
        return new QuayCoordinates(
          Double.parseDouble(split[0]),
          Double.parseDouble(split[1])
        );
      } else {
        throw new NetexValidationException(
          "Invalid quayCoordinates string: " + quayCoordinates
        );
      }
    }
    return null;
  }
}

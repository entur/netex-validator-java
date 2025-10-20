package org.entur.netex.validation.validator.utils;

import jakarta.xml.bind.JAXBElement;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;

public class StopPlaceUtils {

  private StopPlaceUtils() {}

  public static boolean isParentStopPlace(StopPlace stopPlace) {
    if (stopPlace.getKeyList() != null) {
      for (var keyValue : stopPlace.getKeyList().getKeyValue()) {
        if (
          keyValue.getKey().equals("IS_PARENT_STOP_PLACE") &&
          keyValue.getValue().equals("true")
        ) {
          return true;
        }
      }
    }
    return false;
  }

  public static Set<String> getQuayIdsForStopPlace(StopPlace stopPlace) {
    if (stopPlace.getQuays() != null && stopPlace.getQuays().getQuayRefOrQuay() != null) {
      return stopPlace
        .getQuays()
        .getQuayRefOrQuay()
        .stream()
        .map(JAXBElement::getValue)
        .filter(Objects::nonNull)
        .map(value -> ((Quay) value).getId())
        .collect(Collectors.toSet());
    }
    return new HashSet<>();
  }
}

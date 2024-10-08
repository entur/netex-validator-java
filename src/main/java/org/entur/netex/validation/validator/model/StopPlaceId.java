package org.entur.netex.validation.validator.model;

import java.util.Objects;
import org.entur.netex.validation.exception.NetexValidationException;

/**
 * The NeTEx id of a StopPlace.
 */
public record StopPlaceId(String id) {
  public StopPlaceId {
    Objects.requireNonNull(id, "Stop place id should not be null");
    if (!isValid(id)) {
      throw new NetexValidationException("In valid stop place id: " + id);
    }
  }

  public static boolean isValid(String stopPlaceId) {
    return stopPlaceId.contains(":StopPlace:");
  }

  @Override
  public String toString() {
    return id();
  }
}

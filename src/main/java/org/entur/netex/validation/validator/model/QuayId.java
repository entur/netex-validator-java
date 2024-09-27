package org.entur.netex.validation.validator.model;

import java.util.Objects;
import java.util.Optional;
import org.entur.netex.validation.exception.NetexValidationException;
import org.rutebanken.netex.model.Quay;

/**
 * The NeTEx id of Quay.
 */
public record QuayId(String id) {
  public QuayId {
    Objects.requireNonNull(id, "Quay id should not be null");
    if (!isValid(id)) {
      throw new NetexValidationException("Invalid quay id: " + id);
    }
  }

  public static QuayId ofValidId(Quay quay) {
    return Optional
      .of(quay)
      .map(Quay::getId)
      .map(QuayId::ofValidId)
      .orElse(null);
  }

  public static QuayId ofValidId(String id) {
    return id != null && isValid(id) ? new QuayId(id) : null;
  }

  public static QuayId ofNullable(String id) {
    return id == null ? null : new QuayId(id);
  }

  public static boolean isValid(String quayId) {
    return quayId.contains(":Quay:");
  }

  @Override
  public String toString() {
    return id();
  }
}

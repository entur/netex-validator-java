package org.entur.netex.validation.validator.model;

import java.util.Objects;
import java.util.Optional;
import org.entur.netex.validation.exception.NetexValidationException;
import org.rutebanken.netex.model.FlexibleLine;
import org.rutebanken.netex.model.Line;
import org.rutebanken.netex.model.MultilingualString;

/**
 * Light-way representation of a NeTEx Line.
 * This contains the minimum information required to validate a Line.
 */
public record SimpleLine(String lineId, String lineName, String fileName) {
  public SimpleLine {
    Objects.requireNonNull(lineId, "Line id should not be null");
    Objects.requireNonNull(lineName, "Line name should not be null");
    Objects.requireNonNull(fileName, "File name should not be null");
  }

  public static SimpleLine of(Line line, String fileName) {
    return new SimpleLine(
      line.getId(),
      Optional
        .ofNullable(line.getName())
        .map(MultilingualString::getValue)
        .orElse(null),
      fileName
    );
  }

  public static SimpleLine of(FlexibleLine flexibleLine, String fileName) {
    return new SimpleLine(
      flexibleLine.getId(),
      Optional
        .ofNullable(flexibleLine.getName())
        .map(MultilingualString::getValue)
        .orElse(null),
      fileName
    );
  }

  @Override
  public String toString() {
    return lineId + "§" + lineName + "§" + fileName;
  }

  public static SimpleLine fromString(String lineInfo) {
    if (lineInfo != null) {
      String[] split = lineInfo.split("§");
      if (split.length == 3) {
        return new SimpleLine(split[0], split[1], split[2]);
      } else {
        throw new NetexValidationException(
          "Invalid lineInfo string: " + lineInfo
        );
      }
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SimpleLine simpleLine)) return false;
    return Objects.equals(lineName, simpleLine.lineName);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(lineName);
  }
}

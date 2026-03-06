package org.entur.netex.validation.validator.model;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;
import org.rutebanken.netex.model.MultilingualString;

/**
 * Helper for extracting string values from NeTEx MultilingualString.
 * In NeTEx 2.0 (netex-java-model 3.x), MultilingualString uses a mixed content model
 * where text is stored in getContent() as a List of Serializable objects,
 * replacing the previous getValue() method.
 */
public final class MultilingualStringValue {

  private MultilingualStringValue() {}

  @Nullable
  public static String of(@Nullable MultilingualString multilingualString) {
    if (multilingualString == null) {
      return null;
    }
    List<Serializable> content = multilingualString.getContent();
    if (content == null || content.isEmpty()) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (Serializable item : content) {
      if (item instanceof String s) {
        sb.append(s);
      }
    }
    return sb.isEmpty() ? null : sb.toString();
  }
}

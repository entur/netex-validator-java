package org.entur.netex.validation.validator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.entur.netex2.model.MultilingualString;
import org.junit.jupiter.api.Test;

class MultilingualStringValueTest {

  @Test
  void nullMultilingualString() {
    assertNull(MultilingualStringValue.of(null));
  }

  @Test
  void emptyContent() {
    assertNull(MultilingualStringValue.of(new MultilingualString()));
  }

  @Test
  void singleTextContent() {
    assertEquals(
      "Oslo S",
      MultilingualStringValue.of(new MultilingualString().withContent("Oslo S"))
    );
  }

  @Test
  void multipleTextContentIsConcatenated() {
    assertEquals(
      "Oslo S",
      MultilingualStringValue.of(new MultilingualString().withContent("Oslo", " S"))
    );
  }
}

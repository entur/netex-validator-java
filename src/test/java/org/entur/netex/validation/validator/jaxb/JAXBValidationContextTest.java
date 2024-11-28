package org.entur.netex.validation.validator.jaxb;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.entur.netex.validation.validator.DataLocation;
import org.entur.netex.validation.validator.id.IdVersion;
import org.junit.jupiter.api.Test;

class JAXBValidationContextTest {

  public static final String FILE_NAME = "netex.xml";
  public static final String OBJECT_ID = "ENT:LINE:1";
  private static final int LINE_NUMBER = 1;
  private static final int COLUMN_NUMBER = 2;

  @Test
  void testDataLocationMissingId() {
    JAXBValidationContext context = new JAXBValidationContext(
      null,
      null,
      null,
      null,
      null,
      FILE_NAME,
      Map.of()
    );

    DataLocation dataLocation = context.dataLocation(OBJECT_ID);
    assertNotNull(dataLocation);
    assertEquals(FILE_NAME, dataLocation.getFileName());
    assertEquals(OBJECT_ID, dataLocation.getObjectId());
  }

  @Test
  void testDataLocationExistingId() {
    IdVersion idVersion = new IdVersion(
      OBJECT_ID,
      "1",
      "Line",
      null,
      FILE_NAME,
      LINE_NUMBER,
      COLUMN_NUMBER
    );
    Map<String, IdVersion> localIdsMap = Map.of(OBJECT_ID, idVersion);
    JAXBValidationContext context = new JAXBValidationContext(
      null,
      null,
      null,
      null,
      null,
      FILE_NAME,
      localIdsMap
    );

    DataLocation dataLocation = context.dataLocation(OBJECT_ID);
    assertNotNull(dataLocation);
    assertEquals(FILE_NAME, dataLocation.getFileName());
    assertEquals(OBJECT_ID, dataLocation.getObjectId());
    assertEquals(LINE_NUMBER, dataLocation.getLineNumber());
    assertEquals(COLUMN_NUMBER, dataLocation.getColumNumber());
  }
}

package org.entur.netex.validation.validator.utils;

import jakarta.xml.bind.JAXBElement;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.*;

class StopPlaceUtilsTest {

  @Test
  void testIsParentStopPlace() {
    StopPlace parentStopPlace = new StopPlace();
    parentStopPlace.setKeyList(
      new KeyListStructure()
        .withKeyValue(
          new KeyValueStructure().withKey("IS_PARENT_STOP_PLACE").withValue("true")
        )
    );
    Assertions.assertTrue(StopPlaceUtils.isParentStopPlace(parentStopPlace));

    StopPlace nonParentStopPlace = new StopPlace();
    Assertions.assertFalse(StopPlaceUtils.isParentStopPlace(nonParentStopPlace));

    StopPlace nonParentStopPlace2 = new StopPlace();
    parentStopPlace.setKeyList(
      new KeyListStructure()
        .withKeyValue(
          new KeyValueStructure().withKey("IS_PARENT_STOP_PLACE").withValue("false")
        )
    );
    Assertions.assertFalse(StopPlaceUtils.isParentStopPlace(nonParentStopPlace2));
  }

  @Test
  void testGetQuayIdsForStopPlace() {
    StopPlace stopPlace = new StopPlace();
    Quay quay1 = new Quay().withId("NSR:Quay:1");
    Quay quay2 = new Quay().withId("NSR:Quay:2");
    stopPlace.setQuays(
      new Quays_RelStructure()
        .withQuayRefOrQuay(
          List.of(
            new JAXBElement<>(
              new QName("http://www.netex.org.uk/netex", "Quay"),
              Quay.class,
              quay1
            ),
            new JAXBElement<>(
              new QName("http://www.netex.org.uk/netex", "Quay"),
              Quay.class,
              quay2
            )
          )
        )
    );
    Assertions.assertEquals(
      Set.of(quay1.getId(), quay2.getId()),
      StopPlaceUtils.getQuayIdsForStopPlace(stopPlace)
    );
  }

  @Test
  void testGetQuayIdsForStopPlaceWhenItHasNoQuays() {
    StopPlace stopPlace = new StopPlace();
    Assertions.assertTrue(StopPlaceUtils.getQuayIdsForStopPlace(stopPlace).isEmpty());
  }
}

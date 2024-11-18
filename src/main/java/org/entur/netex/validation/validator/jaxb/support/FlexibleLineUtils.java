package org.entur.netex.validation.validator.jaxb.support;

import org.rutebanken.netex.model.FlexibleLine;
import org.rutebanken.netex.model.FlexibleLineTypeEnumeration;

public class FlexibleLineUtils {

  private FlexibleLineUtils() {}

  /**
   * Return true if the flexible line has a fixed sequence of stops.
   * A Fixed flexible line follows a fixed sequence of stops, but trips must be booked in advance (not scheduled).
   */
  public static boolean isFixedFlexibleLine(FlexibleLine flexibleLine) {
    return (
      flexibleLine != null &&
      flexibleLine.getFlexibleLineType() == FlexibleLineTypeEnumeration.FIXED
    );
  }
}

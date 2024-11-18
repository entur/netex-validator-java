package org.entur.netex.validation.validator.model;

import org.entur.netex.validation.exception.NetexValidationException;
import org.rutebanken.netex.model.ServiceLink;

/**
 * The pair of ScheduledStopPoint ids representing the "from" and "to" ends of a ServiceLink.
 */
public record FromToScheduledStopPointId(
  ScheduledStopPointId from,
  ScheduledStopPointId to
) {
  public static FromToScheduledStopPointId of(ServiceLink serviceLink) {
    return new FromToScheduledStopPointId(
      ScheduledStopPointId.of(serviceLink.getFromPointRef()),
      ScheduledStopPointId.of(serviceLink.getToPointRef())
    );
  }

  @Override
  public String toString() {
    return from.toString() + "§" + to.toString();
  }

  public static FromToScheduledStopPointId fromString(
    String scheduledStopPointIds
  ) {
    if (scheduledStopPointIds != null) {
      String[] split = scheduledStopPointIds.split("§");
      if (split.length == 2) {
        return new FromToScheduledStopPointId(
          new ScheduledStopPointId(split[0]),
          new ScheduledStopPointId(split[1])
        );
      } else {
        throw new NetexValidationException(
          "Invalid scheduledStopPointIds string: " + scheduledStopPointIds
        );
      }
    }
    return null;
  }
}

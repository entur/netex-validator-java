package org.entur.netex.validation.validator.model;

import java.util.Optional;
import org.entur.netex.validation.exception.NetexValidationException;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

/**
 * The NeTEx id od a ScheduledStopPoint.
 */
public record ScheduledStopPointId(String id) {
  public ScheduledStopPointId {
    if (!isValid(id)) {
      throw new NetexValidationException(
        "Invalid scheduled stop point id: " + id
      );
    }
  }

  public static ScheduledStopPointId of(
    StopPointInJourneyPattern stopPointInJourneyPattern
  ) {
    return of(stopPointInJourneyPattern.getScheduledStopPointRef().getValue());
  }

  public static ScheduledStopPointId of(
    ScheduledStopPointRefStructure scheduledStopPointRef
  ) {
    return new ScheduledStopPointId(scheduledStopPointRef.getRef());
  }

  public static ScheduledStopPointId ofNullable(
    ScheduledStopPointRefStructure scheduledStopPointRef
  ) {
    return Optional
      .ofNullable(scheduledStopPointRef)
      .map(ScheduledStopPointRefStructure::getRef)
      .map(ScheduledStopPointId::new)
      .orElse(null);
  }

  public static boolean isValid(String id) {
    return id != null && id.contains(":ScheduledStopPoint:");
  }

  @Override
  public String toString() {
    return id();
  }
}

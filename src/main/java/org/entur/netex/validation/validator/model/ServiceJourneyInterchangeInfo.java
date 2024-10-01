package org.entur.netex.validation.validator.model;

import java.util.Optional;
import org.entur.netex.validation.exception.NetexValidationException;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.VersionOfObjectRefStructure;

public record ServiceJourneyInterchangeInfo(
  String filename,
  String interchangeId,
  ScheduledStopPointId fromStopPoint,
  ScheduledStopPointId toStopPoint,
  ServiceJourneyId fromJourneyRef,
  ServiceJourneyId toJourneyRef
) {
  public static ServiceJourneyInterchangeInfo of(
    String filename,
    ServiceJourneyInterchange serviceJourneyInterchange
  ) {
    return new ServiceJourneyInterchangeInfo(
      filename,
      serviceJourneyInterchange.getId(),
      ScheduledStopPointId.ofNullable(
        serviceJourneyInterchange.getFromPointRef()
      ),
      ScheduledStopPointId.ofNullable(
        serviceJourneyInterchange.getToPointRef()
      ),
      Optional
        .ofNullable(serviceJourneyInterchange.getFromJourneyRef())
        .map(VersionOfObjectRefStructure::getRef)
        .map(ServiceJourneyId::new)
        .orElse(null),
      Optional
        .ofNullable(serviceJourneyInterchange.getToJourneyRef())
        .map(VersionOfObjectRefStructure::getRef)
        .map(ServiceJourneyId::new)
        .orElse(null)
    );
  }

  public boolean isValid() {
    return (
      filename != null &&
      !filename.isEmpty() &&
      interchangeId != null &&
      Optional
        .ofNullable(fromStopPoint)
        .map(ScheduledStopPointId::id)
        .map(ScheduledStopPointId::isValid)
        .orElse(false) &&
      Optional
        .ofNullable(toStopPoint)
        .map(ScheduledStopPointId::id)
        .map(ScheduledStopPointId::isValid)
        .orElse(false) &&
      fromJourneyRef != null &&
      toJourneyRef != null
    );
  }

  /*
   * Used to encode data to store in redis.
   * Caution: Changes in this method can effect data stored in redis.
   */
  @Override
  public String toString() {
    return (
      filename +
      "§" +
      interchangeId +
      "§" +
      fromStopPoint +
      "§" +
      toStopPoint +
      "§" +
      fromJourneyRef +
      "§" +
      toJourneyRef
    );
  }

  /*
   * Used to encode data to store in redis.
   * Caution: Changes in this method can effect data stored in redis.
   */
  public static ServiceJourneyInterchangeInfo fromString(
    String serviceJourneyInterchangeInfo
  ) {
    if (serviceJourneyInterchangeInfo != null) {
      String[] split = serviceJourneyInterchangeInfo.split("§");
      if (split.length == 6) {
        return new ServiceJourneyInterchangeInfo(
          split[0],
          split[1],
          new ScheduledStopPointId(split[2]),
          new ScheduledStopPointId(split[3]),
          new ServiceJourneyId(split[4]),
          new ServiceJourneyId(split[5])
        );
      } else {
        throw new NetexValidationException(
          "Invalid serviceJourneyInterchangeInfo string: " +
          serviceJourneyInterchangeInfo
        );
      }
    }
    return null;
  }
}

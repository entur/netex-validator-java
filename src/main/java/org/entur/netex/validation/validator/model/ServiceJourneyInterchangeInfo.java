package org.entur.netex.validation.validator.model;

import java.time.Duration;
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
  ServiceJourneyId toJourneyRef,
  Boolean guaranteed,
  Optional<Duration> maximumWaitTime
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
        .orElse(null),
      serviceJourneyInterchange.isGuaranteed(),
      Optional.ofNullable(serviceJourneyInterchange.getMaximumWaitTime())
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
      toJourneyRef != null &&
      guaranteed != null
    );
  }

  /*
   * Used to encode data to store in redis.
   * Caution: Changes in this method can effect data stored in redis.
   */
  @Override
  public String toString() {
    String interchangeString =
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
      toJourneyRef +
      "§" +
      guaranteed;
    if (maximumWaitTime.isPresent()) {
      return interchangeString.concat("§" + maximumWaitTime.get());
    }
    return interchangeString;
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
      if (split.length == 7 || split.length == 8) {
        return new ServiceJourneyInterchangeInfo(
          split[0],
          split[1],
          new ScheduledStopPointId(split[2]),
          new ScheduledStopPointId(split[3]),
          new ServiceJourneyId(split[4]),
          new ServiceJourneyId(split[5]),
          Boolean.parseBoolean(split[6]),
          split.length == 8
            ? Optional.of(MaximumWaitTime.of(split[7]).duration())
            : Optional.empty()
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

package org.entur.netex.validation.validator.model;

import java.util.Optional;
import org.entur.netex.validation.exception.NetexValidationException;
import org.entur.netex2.model.LinkInJourneyPattern;
import org.entur.netex2.model.ServiceLink;
import org.entur.netex2.model.VersionOfObjectRefStructure;

/**
 * The NeTEx id of a ServiceLink.
 */
public record ServiceLinkId(String id) {
  public ServiceLinkId {
    if (!isValid(id)) {
      throw new NetexValidationException("Invalid scheduled stop point id: " + id);
    }
  }

  public static ServiceLinkId of(ServiceLink serviceLink) {
    return new ServiceLinkId(serviceLink.getId());
  }

  public static ServiceLinkId of(LinkInJourneyPattern linkInJourneyPattern) {
    return Optional
      .ofNullable(linkInJourneyPattern)
      .map(LinkInJourneyPattern::getServiceLinkRef)
      .map(VersionOfObjectRefStructure::getRef)
      .map(ServiceLinkId::new)
      .orElse(null);
  }

  public static boolean isValid(String id) {
    return id != null && id.contains(":ServiceLink:");
  }

  @Override
  public String toString() {
    return id();
  }
}

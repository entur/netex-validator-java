package org.entur.netex.validation.validator.jaxb;

/**
 * Light-way representation of a NeTEx StopPlace.
 * This contains the minimum information required to validate a StopPlace.
 */
public record SimpleStopPlace(
  String name,
  TransportModeAndSubMode transportModeAndSubMode
) {}

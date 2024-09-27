package org.entur.netex.validation.validator.model;

/**
 * Light-way representation of a NeTEx quay.
 * This contains the minimum information required to validate a Quay.
 */
public record SimpleQuay(
  QuayCoordinates quayCoordinates,
  StopPlaceId stopPlaceId
) {}

/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.entur.netex.validation.validator.jaxb;

import javax.annotation.Nullable;
import org.entur.netex.validation.validator.model.QuayCoordinates;
import org.entur.netex.validation.validator.model.QuayId;
import org.entur.netex.validation.validator.model.StopPlaceId;
import org.entur.netex.validation.validator.model.TransportModeAndSubMode;

/**
 * A repository that contains stop place reference data.
 * Concrete implementations can retrieve data from a SiteFrame included in the dataset,
 * or from an external stop register
 */
public interface StopPlaceRepository {
  /**
   * Checks that a StopPlace id exists.
   */
  boolean hasStopPlaceId(StopPlaceId stopPlaceId);

  /**
   * Checks that a Quay id exists.
   */
  boolean hasQuayId(QuayId quayId);

  /**
   * Returns the transport mode and sub-mode for a Quay id.
   */
  @Nullable
  TransportModeAndSubMode getTransportModesForQuayId(QuayId quayId);

  /**
   * Returns the coordinates for a Quay id.
   */
  @Nullable
  QuayCoordinates getCoordinatesForQuayId(QuayId quayId);

  /**
   * Returns the stop place names for a Quay id.
   */
  @Nullable
  String getStopPlaceNameForQuayId(QuayId quayId);

  /**
   * Refresh the repository.
   */
  void refreshCache();

  /**
   * Return true if the repository is not primed.
   */
  boolean isEmpty();
}

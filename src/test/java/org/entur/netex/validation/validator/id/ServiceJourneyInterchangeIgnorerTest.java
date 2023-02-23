package org.entur.netex.validation.validator.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

class ServiceJourneyInterchangeIgnorerTest {


    private static final String TEST_SERVICE_JOURNEY_REFERENCE_ID = "XXX:ServiceJourney:1";
    private static final String TEST_SCHEDULED_STOP_POINT_REFERENCE_ID = "XXX:ScheduledStopPoint:1";
    private ServiceJourneyInterchangeIgnorer serviceJourneyInterchangeIgnorer;

    @BeforeEach
    void setUpTest() {
        serviceJourneyInterchangeIgnorer = new ServiceJourneyInterchangeIgnorer();
    }

    @Test
    void testFilterAllReferencesNotOriginatingFromAnInterchange() {
        IdVersion idVersion = new IdVersion(TEST_SERVICE_JOURNEY_REFERENCE_ID, null, "ServiceJourneyRef", null, null, 0, 0);
        Set<IdVersion> externalIdsToValidate = Set.of(idVersion);
        Set<IdVersion> filteredIdVersions = serviceJourneyInterchangeIgnorer.validateReferenceIds(externalIdsToValidate);
        Assertions.assertNotNull(filteredIdVersions);
        Assertions.assertTrue(filteredIdVersions.isEmpty());
    }

    @Test
    void testAcceptServiceJourneyRefOriginatingFromAnInterchange() {
        IdVersion idVersion = new IdVersion(TEST_SERVICE_JOURNEY_REFERENCE_ID, null, "FromJourneyRef", null, null, 0, 0);
        Set<IdVersion> externalIdsToValidate = Set.of(idVersion);
        Set<IdVersion> filteredIdVersions = serviceJourneyInterchangeIgnorer.validateReferenceIds(externalIdsToValidate);
        Assertions.assertNotNull(filteredIdVersions);
        Assertions.assertEquals(externalIdsToValidate, filteredIdVersions);
    }

    @Test
    void testAcceptScheduledStopPointRefOriginatingFromAnInterchange() {
        IdVersion idVersion = new IdVersion(TEST_SCHEDULED_STOP_POINT_REFERENCE_ID, null, "FromPointRef", null, null, 0, 0);
        Set<IdVersion> externalIdsToValidate = Set.of(idVersion);
        Set<IdVersion> filteredIdVersions = serviceJourneyInterchangeIgnorer.validateReferenceIds(externalIdsToValidate);
        Assertions.assertNotNull(filteredIdVersions);
        Assertions.assertEquals(externalIdsToValidate, filteredIdVersions);
    }


}

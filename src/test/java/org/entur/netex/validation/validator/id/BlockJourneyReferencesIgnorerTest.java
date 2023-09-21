package org.entur.netex.validation.validator.id;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlockJourneyReferencesIgnorerTest {

  private static final String TEST_NON_JOURNEY_REFERENCE_ID = "XXX:YY:1";
  private static final String TEST_DEAD_RUN_REFERENCE_ID = "XXX:DeadRun:1";
  private static final String TEST_SERVICE_JOURNEY_REFERENCE_ID =
    "XXX:ServiceJourney:1";
  private BlockJourneyReferencesIgnorer blockJourneyReferencesIgnorer;

  @BeforeEach
  void setUpTest() {
    blockJourneyReferencesIgnorer = new BlockJourneyReferencesIgnorer();
  }

  @Test
  void testFilterAllReferencesNotOriginatingFromBlock() {
    IdVersion idVersion = new IdVersion(
      TEST_DEAD_RUN_REFERENCE_ID,
      null,
      "DeadRunRef",
      List.of("Not a Block"),
      null,
      0,
      0
    );
    Set<IdVersion> externalIdsToValidate = Set.of(idVersion);
    Set<IdVersion> filteredIdVersions =
      blockJourneyReferencesIgnorer.validateReferenceIds(externalIdsToValidate);
    Assertions.assertNotNull(filteredIdVersions);
    Assertions.assertTrue(filteredIdVersions.isEmpty());
  }

  @Test
  void testFilterAllReferencesOriginatingFromBlockAndNotReferringToServiceJourneyOrDeadRun() {
    IdVersion idVersion = new IdVersion(
      TEST_NON_JOURNEY_REFERENCE_ID,
      null,
      "YYRef",
      List.of("Block"),
      null,
      0,
      0
    );
    Set<IdVersion> externalIdsToValidate = Set.of(idVersion);
    Set<IdVersion> filteredIdVersions =
      blockJourneyReferencesIgnorer.validateReferenceIds(externalIdsToValidate);
    Assertions.assertNotNull(filteredIdVersions);
    Assertions.assertTrue(filteredIdVersions.isEmpty());
  }

  @Test
  void testAcceptReferenceToServiceJourneyFromBlock() {
    IdVersion idVersion = new IdVersion(
      TEST_SERVICE_JOURNEY_REFERENCE_ID,
      null,
      "ServiceJourneyRef",
      List.of("Block"),
      null,
      0,
      0
    );
    Set<IdVersion> externalIdsToValidate = Set.of(idVersion);
    Set<IdVersion> filteredIdVersions =
      blockJourneyReferencesIgnorer.validateReferenceIds(externalIdsToValidate);
    Assertions.assertNotNull(filteredIdVersions);
    Assertions.assertEquals(externalIdsToValidate, filteredIdVersions);
  }

  @Test
  void testAcceptReferenceToDeadRunFromBlock() {
    IdVersion idVersion = new IdVersion(
      TEST_DEAD_RUN_REFERENCE_ID,
      null,
      "DeadRunRef",
      List.of("Block"),
      null,
      0,
      0
    );
    Set<IdVersion> externalIdsToValidate = Set.of(idVersion);
    Set<IdVersion> filteredIdVersions =
      blockJourneyReferencesIgnorer.validateReferenceIds(externalIdsToValidate);
    Assertions.assertNotNull(filteredIdVersions);
    Assertions.assertEquals(externalIdsToValidate, filteredIdVersions);
  }
}

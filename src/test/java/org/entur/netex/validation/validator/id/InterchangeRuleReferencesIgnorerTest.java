package org.entur.netex.validation.validator.id;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InterchangeRuleReferencesIgnorerTest {

  private InterchangeRuleReferencesIgnorer interchangeRuleReferencesIgnorer;

  @BeforeEach
  void setUpTest() {
    interchangeRuleReferencesIgnorer = new InterchangeRuleReferencesIgnorer();
  }

  @Test
  void testFilterAllReferencesOriginatingFromAnInterchangeRule() {
    IdVersion idVersion = new IdVersion(
      "RUT:Line:45",
      null,
      "LineRef",
      List.of("InterchangeRule", "LineInDirectionRef"),
      null,
      0,
      0
    );
    Set<IdVersion> externalIdsToValidate = Set.of(idVersion);
    Set<IdVersion> filteredIdVersions =
      interchangeRuleReferencesIgnorer.validateReferenceIds(externalIdsToValidate);
    Assertions.assertNotNull(filteredIdVersions);
    Assertions.assertEquals(externalIdsToValidate, filteredIdVersions);
  }

  @Test
  void testAcceptServiceJourneyRefNotOriginatingFromAnInterchangeRule() {
    IdVersion idVersion = new IdVersion(
      "RUT:Line:45",
      null,
      "LineRef",
      List.of("LineInDirectionRef"),
      null,
      0,
      0
    );
    Set<IdVersion> externalIdsToValidate = Set.of(idVersion);
    Set<IdVersion> filteredIdVersions =
      interchangeRuleReferencesIgnorer.validateReferenceIds(externalIdsToValidate);
    Assertions.assertNotNull(filteredIdVersions);
    Assertions.assertTrue(filteredIdVersions.isEmpty());
  }
}

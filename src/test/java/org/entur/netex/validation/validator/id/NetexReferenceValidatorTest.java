package org.entur.netex.validation.validator.id;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.entur.netex.validation.validator.ValidationIssue;
import org.entur.netex.validation.validator.xpath.XPathValidationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NetexReferenceValidatorTest {

  private static final String TEST_CODESPACE = "TST";
  private static final String TEST_VALIDATION_REPORT_ID = "TEST_VALIDATION_REPORT_ID";
  private static final String TEST_LOCAL_ID = "XXX:YY:1";
  private static final String TEST_MISSING_EXTERNAL_ID = "XXX:YY:2";
  private NetexIdRepository netexIdRepository;

  @BeforeEach
  void setUpTest() {
    netexIdRepository =
      new NetexIdRepository() {
        @Override
        public Set<String> getDuplicateNetexIds(
          String reportId,
          String filename,
          Set<String> localIds
        ) {
          if (localIds.contains(TEST_LOCAL_ID)) {
            return Set.of(TEST_LOCAL_ID);
          } else {
            return Set.of();
          }
        }

        @Override
        public Set<String> getSharedNetexIds(String reportId) {
          return Set.of();
        }

        @Override
        public void addSharedNetexIds(String reportId, Set<IdVersion> commonIds) {}

        @Override
        public void cleanUp(String reportId) {}
      };
  }

  @Test
  void testUnresolvedExternalReferencesToLocalIds() {
    IdVersion localIdVersion = new IdVersion(TEST_LOCAL_ID, null, "YY", null, null, 0, 0);
    Set<IdVersion> localIds = Set.of(localIdVersion);
    IdVersion localRef = new IdVersion(
      TEST_MISSING_EXTERNAL_ID,
      null,
      "YY",
      null,
      null,
      0,
      0
    );
    List<IdVersion> localRefs = List.of(localRef);
    XPathValidationContext xPathValidationContext = new XPathValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      localRefs,
      TEST_VALIDATION_REPORT_ID
    );
    NetexReferenceValidator netexReferenceValidator = new NetexReferenceValidator(
      netexIdRepository,
      List.of()
    );
    List<ValidationIssue> validationIssues = netexReferenceValidator.validate(
      xPathValidationContext
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertEquals(
      NetexReferenceValidator.RULE,
      validationIssues.stream().findFirst().orElseThrow().rule()
    );
  }

  @Test
  void testNoUnresolvedExternalReferencesToLocalIds() {
    IdVersion localIdVersion = new IdVersion(TEST_LOCAL_ID, null, "YY", null, null, 0, 0);
    Set<IdVersion> localIds = Set.of(localIdVersion);
    List<IdVersion> localRefs = new ArrayList<>(localIds);
    XPathValidationContext xPathValidationContext = new XPathValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      localRefs,
      TEST_VALIDATION_REPORT_ID
    );
    NetexReferenceValidator netexReferenceValidator = new NetexReferenceValidator(
      netexIdRepository,
      List.of()
    );
    List<ValidationIssue> validationIssues = netexReferenceValidator.validate(
      xPathValidationContext
    );
    Assertions.assertTrue(validationIssues.isEmpty());
  }

  @Test
  void testUnresolvedExternalReferencesToExternalRefs() {
    IdVersion localRefVersion = new IdVersion(
      TEST_LOCAL_ID,
      null,
      "YY",
      null,
      null,
      0,
      0
    );
    Set<IdVersion> localIds = Set.of();
    List<IdVersion> localRefs = List.of(localRefVersion);
    XPathValidationContext xPathValidationContext = new XPathValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      localRefs,
      TEST_VALIDATION_REPORT_ID
    );

    ExternalReferenceValidator externalReferenceValidator = externalIdsToValidate ->
      Set.of();
    NetexReferenceValidator netexReferenceValidator = new NetexReferenceValidator(
      netexIdRepository,
      List.of(externalReferenceValidator)
    );
    List<ValidationIssue> validationIssues = netexReferenceValidator.validate(
      xPathValidationContext
    );
    Assertions.assertFalse(validationIssues.isEmpty());
    Assertions.assertEquals(
      NetexReferenceValidator.RULE,
      validationIssues.stream().findFirst().orElseThrow().rule()
    );
  }

  @Test
  void testNoUnresolvedExternalReferencesToExternalRefs() {
    IdVersion localRefVersion = new IdVersion(
      TEST_LOCAL_ID,
      null,
      "YY",
      null,
      null,
      0,
      0
    );
    Set<IdVersion> localIds = Set.of();
    List<IdVersion> localRefs = List.of(localRefVersion);
    XPathValidationContext xPathValidationContext = new XPathValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      localRefs,
      TEST_VALIDATION_REPORT_ID
    );

    ExternalReferenceValidator externalReferenceValidator = externalIdsToValidate ->
      Set.of(localRefVersion);
    NetexReferenceValidator netexReferenceValidator = new NetexReferenceValidator(
      netexIdRepository,
      List.of(externalReferenceValidator)
    );
    List<ValidationIssue> validationIssues = netexReferenceValidator.validate(
      xPathValidationContext
    );
    Assertions.assertTrue(validationIssues.isEmpty());
  }
}

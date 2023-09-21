package org.entur.netex.validation.validator.id;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntryFactory;
import org.entur.netex.validation.validator.ValidationReportEntrySeverity;
import org.entur.netex.validation.validator.xpath.ValidationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NetexReferenceValidatorTest {

  private static final String TEST_CODESPACE = "TST";
  private static final String TEST_VALIDATION_REPORT_ID =
    "TEST_VALIDATION_REPORT_ID";
  private static final String TEST_LOCAL_ID = "XXX:YY:1";
  private static final String TEST_MISSING_EXTERNAL_ID = "XXX:YY:2";
  private ValidationReport validationReport;
  private NetexIdRepository netexIdRepository;
  private ValidationReportEntryFactory validationReportEntryFactory;

  @BeforeEach
  void setUpTest() {
    validationReportEntryFactory =
      (code, validationReportEntryMessage, dataLocation) ->
        new ValidationReportEntry(
          validationReportEntryMessage,
          code,
          ValidationReportEntrySeverity.INFO
        );
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
        public void addSharedNetexIds(
          String reportId,
          Set<IdVersion> commonIds
        ) {}

        @Override
        public void cleanUp(String reportId) {}
      };

    validationReport =
      new ValidationReport(TEST_CODESPACE, TEST_VALIDATION_REPORT_ID);
  }

  @Test
  void testUnresolvedExternalReferencesToLocalIds() {
    IdVersion localIdVersion = new IdVersion(
      TEST_LOCAL_ID,
      null,
      "YY",
      null,
      null,
      0,
      0
    );
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
    ValidationContext validationContext = new ValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      localRefs
    );
    NetexReferenceValidator netexReferenceValidator =
      new NetexReferenceValidator(
        netexIdRepository,
        List.of(),
        validationReportEntryFactory
      );
    netexReferenceValidator.validate(validationReport, validationContext);
    Assertions.assertFalse(
      validationReport.getValidationReportEntries().isEmpty()
    );
    Assertions.assertEquals(
      NetexReferenceValidator.RULE_CODE_NETEX_ID_5,
      validationReport
        .getValidationReportEntries()
        .stream()
        .findFirst()
        .orElseThrow()
        .getName()
    );
  }

  @Test
  void testNoUnresolvedExternalReferencesToLocalIds() {
    IdVersion localIdVersion = new IdVersion(
      TEST_LOCAL_ID,
      null,
      "YY",
      null,
      null,
      0,
      0
    );
    Set<IdVersion> localIds = Set.of(localIdVersion);
    List<IdVersion> localRefs = new ArrayList<>(localIds);
    ValidationContext validationContext = new ValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      localRefs
    );
    NetexReferenceValidator netexReferenceValidator =
      new NetexReferenceValidator(
        netexIdRepository,
        List.of(),
        validationReportEntryFactory
      );
    netexReferenceValidator.validate(validationReport, validationContext);
    Assertions.assertTrue(
      validationReport.getValidationReportEntries().isEmpty()
    );
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
    ValidationContext validationContext = new ValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      localRefs
    );

    ExternalReferenceValidator externalReferenceValidator =
      externalIdsToValidate -> Set.of();
    NetexReferenceValidator netexReferenceValidator =
      new NetexReferenceValidator(
        netexIdRepository,
        List.of(externalReferenceValidator),
        validationReportEntryFactory
      );
    netexReferenceValidator.validate(validationReport, validationContext);
    Assertions.assertFalse(
      validationReport.getValidationReportEntries().isEmpty()
    );
    Assertions.assertEquals(
      NetexReferenceValidator.RULE_CODE_NETEX_ID_5,
      validationReport
        .getValidationReportEntries()
        .stream()
        .findFirst()
        .orElseThrow()
        .getName()
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
    ValidationContext validationContext = new ValidationContext(
      null,
      null,
      TEST_CODESPACE,
      null,
      localIds,
      localRefs
    );

    ExternalReferenceValidator externalReferenceValidator =
      externalIdsToValidate -> Set.of(localRefVersion);
    NetexReferenceValidator netexReferenceValidator =
      new NetexReferenceValidator(
        netexIdRepository,
        List.of(externalReferenceValidator),
        validationReportEntryFactory
      );
    netexReferenceValidator.validate(validationReport, validationContext);
    Assertions.assertTrue(
      validationReport.getValidationReportEntries().isEmpty()
    );
  }
}

package org.entur.netex.validation.validator.id;

import org.entur.netex.validation.validator.ValidationReport;
import org.entur.netex.validation.validator.ValidationReportEntry;
import org.entur.netex.validation.validator.ValidationReportEntryFactory;
import org.entur.netex.validation.validator.ValidationReportEntrySeverity;
import org.entur.netex.validation.validator.xpath.ValidationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

class ReferenceToValidEntityTypeValidatorTest {

    private static final String TEST_CODESPACE = "TST";
    private static final String TEST_VALIDATION_REPORT_ID = "TEST_VALIDATION_REPORT_ID";
    private static final String TEST_REFERENCED_ID = "XXX:YY:1";
    private static final String TEST_INVALID_REFERENCED_ID = "This is an invalid NeTEx id";
    private ReferenceToValidEntityTypeValidator referenceToValidEntityTypeValidator;
    private ValidationReport validationReport;

    @BeforeEach
    void setUpTest() {
        ValidationReportEntryFactory validationReportEntryFactory = (code, validationReportEntryMessage, dataLocation) -> new ValidationReportEntry(validationReportEntryMessage, code, ValidationReportEntrySeverity.INFO);
        referenceToValidEntityTypeValidator = new ReferenceToValidEntityTypeValidator(validationReportEntryFactory);
        validationReport = new ValidationReport(TEST_CODESPACE, TEST_VALIDATION_REPORT_ID);
    }

    @Test
    void testInvalidReferenceType() {
        IdVersion idVersion = new IdVersion(TEST_REFERENCED_ID, null, "YY", null, null, 0, 0);
        List<IdVersion> localRefs = List.of(idVersion);
        ValidationContext validationContext = new ValidationContext(null, null, TEST_CODESPACE, null, Set.of(), localRefs);
        referenceToValidEntityTypeValidator.validate(validationReport, validationContext);
        Assertions.assertFalse(validationReport.getValidationReportEntries().isEmpty());
        Assertions.assertEquals(ReferenceToValidEntityTypeValidator.RULE_CODE_NETEX_ID_6, validationReport.getValidationReportEntries().stream().findFirst().orElseThrow().getName());
    }

    @Test
    void testInvalidReferenceStructure() {
        IdVersion idVersion = new IdVersion(TEST_INVALID_REFERENCED_ID, null, "YY", null, null, 0, 0);
        List<IdVersion> localRefs = List.of(idVersion);
        ValidationContext validationContext = new ValidationContext(null, null, TEST_CODESPACE, null, Set.of(), localRefs);
        referenceToValidEntityTypeValidator.validate(validationReport, validationContext);
        Assertions.assertFalse(validationReport.getValidationReportEntries().isEmpty());
        Assertions.assertEquals(ReferenceToValidEntityTypeValidator.RULE_CODE_NETEX_ID_7, validationReport.getValidationReportEntries().stream().findFirst().orElseThrow().getName());
    }

    @Test
    void testValidReferenceType() {
        IdVersion idVersion = new IdVersion(TEST_REFERENCED_ID, null, "YYRef", null, null, 0, 0);
        List<IdVersion> localRefs = List.of(idVersion);
        ValidationContext validationContext = new ValidationContext(null, null, TEST_CODESPACE, null, Set.of(), localRefs);
        referenceToValidEntityTypeValidator.validate(validationReport, validationContext);
        Assertions.assertTrue(validationReport.getValidationReportEntries().isEmpty());
    }


}

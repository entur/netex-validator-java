# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Java library for validating NeTEx (Network Timetable Exchange) datasets against the Nordic NeTEx Profile. Used by [Antu](https://github.com/entur/antu) for Entur-specific validation. Published to Maven Central as `org.entur:netex-validator-java`.

## Build & Test Commands

```bash
# Build (skip prettier during development)
mvn install -Dprettier.skip=true

# Run all tests (skip prettier)
mvn test -Dprettier.skip=true

# Run a single test class
mvn test -Dprettier.skip=true -Dtest=ClassName

# Run a single test method
mvn test -Dprettier.skip=true -Dtest=ClassName#methodName

# Format code with prettier (run before committing)
mvn prettier:write

# Check formatting only (CI mode)
mvn validate -P prettierCheck
```

Java 17 is required. The project uses prettier-java for code formatting (runs in the `validate` phase by default). Use `-Dprettier.skip=true` to skip formatting during development.

## Architecture

### Validation Pipeline

`NetexValidatorsRunner` orchestrates validation in three sequential, blocking phases:

1. **Schema Validation** (`NetexSchemaValidator`) - XML Schema validation. If errors found, stops here.
2. **XPath Validation** (`XPathValidator` implementations) - XPath queries on Saxon XDM tree. If errors found, stops here.
3. **JAXB Validation** (`JAXBValidator` implementations) - Validation using JAXB object model (`NetexEntitiesIndex` from netex-parser-java). Runs only if prior phases pass.

After all files are processed, **Dataset Validators** (`DatasetValidator`) run cross-file validations.

### Key Interfaces

- `NetexValidator<V extends ValidationContext>` - Base validator interface. Returns `List<ValidationIssue>`.
- `XPathValidator` - Marker interface extending `NetexValidator<XPathValidationContext>`.
- `JAXBValidator` - Marker interface extending `NetexValidator<JAXBValidationContext>`.
- `DatasetValidator` - Cross-file validation after all individual files are processed.
- `NetexDataCollector` - Collects data from individual files for use by dataset validators.

### Validation Rules

Each rule is a `ValidationRule(code, name, message, severity)`. Validators return `ValidationIssue` instances referencing a rule and a `DataLocation`. The `ValidationReportEntryFactory` converts issues to report entries, supporting configuration-based override of severity/name/message via YAML.

### XPath Rule System

XPath rules are organized in a `ValidationTree` hierarchy (see `validator/xpath/tree/` for defaults). Generic rule implementations in `validator/xpath/rules/`:
- `ValidateNotExist` - Asserts an XPath returns no results
- `ValidateAtLeastOne` - Asserts at least one match
- `ValidateExactlyOne` - Asserts exactly one match

`XPathRuleValidator` takes a `ValidationTreeFactory` and evaluates the tree against each file.

### Data Flow for JAXB Validators

- `CommonDataRepository` / `CommonDataRepositoryLoader` - Stores data from common files (prefixed with `_`) for use when validating line files.
- `StopPlaceRepository` - Provides stop place/quay data for validators needing geographic context.
- `NetexDataRepository` - Collects data per-file via `NetexDataCollector` for cross-file dataset validation.

### Package Layout

- `cli/` - Command-line interface (`NetexValidatorCLI`, `validate-netex.sh`)
- `configuration/` - YAML-based rule configuration override
- `validator/` - Core interfaces and runner
- `validator/xpath/` - XPath-based validation (rules, tree structure)
- `validator/jaxb/` - JAXB-based validation (object model navigation)
- `validator/id/` - NeTEx ID extraction and uniqueness checking
- `validator/schema/` - XML Schema validation
- `validator/model/` - Domain value types (QuayId, ServiceJourneyId, etc.)
- `xml/` - XML parsing utilities (Saxon-based `NetexXMLParser`)

### Test Utilities

Shared test helpers are in `src/test/java/org/entur/netex/validation/test/` and packaged as a test JAR for downstream projects:
- `test/xpath/support/TestValidationContextBuilder` - Builds XPath validation contexts from XML strings
- `test/jaxb/support/JAXBUtils` - JAXB test utilities

### NeTEx File Conventions

- Datasets are ZIP files containing XML files
- Common/shared files are prefixed with `_`
- Each non-common file represents a single NeTEx Line
- Common files are validated first; their data is collected for use during line file validation

## Versioning

Follows [semantic versioning](https://semver.org/). Version bumps are managed via gitflow-maven-plugin.

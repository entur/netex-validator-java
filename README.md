# NeTEx validator Java

[![CircleCI](https://circleci.com/gh/entur/netex-validator-java/tree/main.svg?style=svg)](https://circleci.com/gh/entur/netex-validator-java/tree/main)

Validate NeTEx datasets against the [Nordic NeTEx Profile](https://enturas.atlassian.net/wiki/spaces/PUBLIC/pages/728891481/Nordic+NeTEx+Profile).

# Input data
The validator requires:
- The NeTEx codespace of the timetable data provider.
- A NeTEx file containing the timetable data.
- A unique id (string) identifying the validation run.

# Output
The validator produces a **ValidationReport** object that contains a list of **ValidationReportEntry** instances.
A **ValidationReportEntry** represents a unique validation finding identified by:
- the name of the validation rule,
- the context-specific validation message,
- the severity of the finding (INFO, WARNING, ERROR),
- the name of the file being analyzed.

# XML Schema validation
The entry point **NetexValidatorsRunner** runs by default an XML Schema validation as the first step in the validation process.

This validation step is blocking: in case of an XML Schema validation error, further validations are skipped. 

# Configurable validators
The entry point **NetexValidatorsRunner** can be configured with a list of **NetexValidator** instances that are executed sequentially during a validation run, after a successful XML Schema validation.

The library offers default implementations for:
- XPath validation
- NeTEx id uniqueness validation
- NeTEx reference consistency check

The library can be extended with custom NetexValidator implementations (see [Antu](https://github.com/entur/antu) for examples of Entur-specific validators)

# Parallel processing and thread-safety
The library is thread-safe and can execute validations in parallel within the same JVM, though some NetexValidator implementations may require synchronization.
This is the case in particular for the NeTEx id uniqueness validation, since it checks uniqueness across several files.
See [Antu](https://github.com/entur/antu) for examples of distributed validation across several Kubernetes pods.

# Development guide

## Adding new XPath validation rules
Simple XPath validation rules can be implemented with the following generic ValidationRules:
- ValidateNotExist
- ValidateAtLeastOne
- ValidateExactlyOne

Example:
```java
ValidationRule rule = new ValidateNotExist("lines/*[self::Line or self::FlexibleLine][not(TransportMode)]", "Missing TransportMode on Line", "LINE_4")
```

More complex rules can be implemented by extending these generic ValidationRules or implementing the ValidationRule interface.

## Registering an XPath validation rule
XPath rules must be registered in a ValidationTree to be applied on a NeTEx document.
The library comes with a default validation tree (see DefaultValidationTreeFactory) that can be extended with custom rules.

## Implementing custom NeTEx validators
Other types of NeTEx validators (non XPath-based) can be added by implementing the NetexValidator interface.
See NetexIdUniquenessValidator for an example.

## Registering validators
Validators must be registered in a NetexValidatorsRunner.
The method NetexValidatorsRunner.validate() is the entry point for running a validation.  
It executes the registered NeTEx validators and returns a ValidationReport containing the validation findings.

Example:
```java
// create a NeTEx XML Parser that ignores SiteFrame elements
NetexXMLParser netexXMLParser = new NetexXMLParser(Set.of("SiteFrame"));
// create a NeTEx schema validator, limit the number of findings to 100
NetexSchemaValidator netexSchemaValidator = new NetexSchemaValidator(100);
// create a custom NeTex validator
NetexValidator netexValidator = new CustomNetexValidator()
// create a NeTEx validator runner that registers the NeTEx schema validator and the custom NeTEx validator
NetexValidatorsRunner netexValidatorsRunner = new NetexValidatorsRunner(netexXMLParser, netexSchemaValidator, List.of(netexValidator));
// run the validation for a given codespace, report id, NeTEx filename and file binary content
ValidationReport validationReport = netexValidatorsRunner.validate(codespace, reportId, filename, content);
```
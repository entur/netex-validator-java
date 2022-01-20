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
- NeTEx id uniqueness
- NeTEx reference consistency

The library can be extended with custom NetexValidator implementations (see [Antu](https://github.com/entur/antu) for examples of Entur-specific validators)

# Parallel processing and thread-safety
The library is thread-safe and can execute validations in parallel within the same JVM, though some NetexValidator implementations may require synchronization.
This is the case in particular for the NeTEx id uniqueness validation, since it checks uniqueness across several files.
See [Antu](https://github.com/entur/antu) for examples of distributed validation across several Kubernetes pods.
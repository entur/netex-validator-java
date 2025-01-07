# NeTEx validator Java

Validation library for NeTEx data.  
The library analyzes NeTEx datasets and generates a validation report. 
In addition to XML schema validation, the library applies a configurable set of validation rules (See list below).  
The library is intended primarily to support the validation of datasets compliant with the [Nordic NeTEx Profile](https://enturas.atlassian.net/wiki/spaces/PUBLIC/pages/728891481/Nordic+NeTEx+Profile).  
It expects the dataset to follow the packaging and naming conventions stated in the Nordic NeTEx profile (dataset packaged as a zip file, one NeTEx Line per XML file, shared file prefixed with '_', ...)

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

# XPath validation
The entry point **NetexValidatorsRunner** can be configured with a list of XPath validators.  
XPath validators are run after XML schema validation.  
XPath validators assert validation rules by executing XPath queries on the NeTEx document.   
This validation step is blocking: in case of a validation error, further validations are skipped.

# JAXB validation
The entry point **NetexValidatorsRunner** can be configured with a list of JAXB validators.  
JAXB validators are run after XML schema validation and XPath validation.  
JAXB validators assert validation rules by navigating a (JAXB) object model of the NeTEx document.  
The object model makes it easier to validate more complex rules than XPath validators. On the other hand, these
validators expect that the NeTEx document is well-formed and that the NeTEx entities required by the NeTEx profile are present.  
It is therefore recommended that any assumption made by JAXB validators are asserted during the XPath validation step.

# Configurable validators
The entry point **NetexValidatorsRunner** can be configured with a list of **NetexValidator** instances that are executed sequentially during a validation run, after a successful XML Schema validation.

The library offers default implementations for:
- XPath validation
- NeTEx id uniqueness validation
- NeTEx reference consistency check

The library can be extended with custom NetexValidator implementations (see [Antu](https://github.com/entur/antu) for examples of Entur-specific validators)

# Configurable rule severity and description
Each rule is defined with a default name, severity (INFO, WARNING, ERROR, CRITICAL) and a parameterized message in English.
The name, severity and message can be customized/internationalized in a configuration file (YAML).  
The message field can contain placeholders that follow the String.format() conventions.  
The default configuration file is named ```configuration.default.yaml```

Internationalization example: 
```
- code: TRANSPORT_MODE_ON_LINE
  name: Mode de transport invalide
  message: Le mode de transport %s est invalide
  severity: WARNING
 ```

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
Other types of NeTEx validators can be added by implementing the NetexValidator interface.
See NetexIdUniquenessValidator for an example.

## Registering validators
Validators must be registered in a NetexValidatorsRunner.
The method NetexValidatorsRunner.validate() is the entry point for running a validation.  
It executes the registered NeTEx validators and returns a ValidationReport containing the validation findings.

Example (see also **Demo.java** for an executable example)
```java
// create a NeTEx XML Parser that ignores SiteFrame elements
NetexXMLParser netexXMLParser = new NetexXMLParser(Set.of("SiteFrame"));
// create a NeTEx schema validator, limit the number of findings to 100
NetexSchemaValidator netexSchemaValidator = new NetexSchemaValidator(100);
// create a custom NeTex validator
NetexValidator netexValidator = new CustomNetexValidator();
// create a NeTEx validator runner that registers the NeTEx schema validator and the custom NeTEx validator
NetexValidatorsRunner netexValidatorsRunner = NetexValidatorsRunner
        .of()
        .withNetexXMLParser(netexXMLParser)
        .withNetexSchemaValidator(netexSchemaValidator)
        .withXPathValidators(List.of(xPathValidator))
        .build();
// run the validation for a given codespace, report id, NeTEx filename and file binary content
ValidationReport validationReport = netexValidatorsRunner.validate(codespace, reportId, filename, content);
```

# Default rule set
The NeTEx validator library comes with the following rule set by default:

| Rule Code                                |                                     Rule Description                                     |
|------------------------------------------|:----------------------------------------------------------------------------------------:|
| AUTHORITY_1                              |                             Authority missing CompanyNumber                              |
| AUTHORITY_2                              |                                  Authority missing Name                                  |
| AUTHORITY_3                              |                               Authority missing LegalName                                |
| AUTHORITY_4                              |                             Authority missing ContactDetails                             |
| AUTHORITY_5                              |                         Authority missing Url for ContactDetails                         |
| BLOCK_1                                  |                            Block missing VehicleScheduleFrame                            |
| BLOCK_2                                  |                                  Block missing Journey                                   |
| BLOCK_3                                  |                                  Block missing DayType                                   |
| BOOKING_1                                |                              Booking illegal BookingAccess                               |
| BOOKING_2                                |                              Booking illegal BookingMethod                               |
| BOOKING_3                                |                                 Booking illegal BookWhen                                 |
| BOOKING_4                                |                                     Booking property                                     |
| BOOKING_5                                |                         Missing BookWhen or MinimumBookingPeriod                         |
| BUY_WHEN_1                               |                                  BuyWhen illegal value                                   |
| COMPOSITE_FRAME_1                        |                        CompositeFrame - missing ValidityCondition                        |
| COMPOSITE_FRAME_2                        |                    CompositeFrame - invalid nested ValidityCondition                     |
| COMPOSITE_FRAME_3                        |                          CompositeFrame - missing ValidBetween                           |
| COMPOSITE_FRAME_4                        |                          CompositeFrame - invalid ValidBetween                           |
| COMPOSITE_FRAME_5                        |                      CompositeFrame - invalid AvailabilityCondition                      |
| COMPOSITE_FRAME_6                        |                      CompositeFrame - missing AvailabilityCondition                      |
| COMPOSITE_FRAME_SITE_FRAME               |                          CompositeFrame - unexpected SiteFrame                           |
| COMPOSITE_TIMETABLE_FRAME_IN_COMMON_FILE |                  CompositeFrame - Illegal TimetableFrame in common file                  |
| DATED_SERVICE_JOURNEY_1                  |                       DatedServiceJourney missing OperatingDayRef                        |
| DATED_SERVICE_JOURNEY_2                  |                      DatedServiceJourney missing ServiceJourneyRef                       |
| DATED_SERVICE_JOURNEY_3                  |                      DatedServiceJourney multiple ServiceJourneyRef                      |
| DATED_SERVICE_JOURNEY_4                  |                          DatedServiceJourney multiple versions                           |
| DATED_SERVICE_JOURNEY_5                  |         DatedServiceJourney multiple references to the same DatedServiceJourney          |
| DEAD_RUN_1                               |                          DeadRun missing PassingTime references                          |
| DEAD_RUN_2                               |                        DeadRun missing JourneyPattern references                         |
| DEAD_RUN_3                               |                            DeadRun missing DayType references                            |
| DESTINATION_DISPLAY_1                    |                           DestinationDisplay missing FrontText                           |
| DESTINATION_DISPLAY_2                    |                 DestinationDisplay missing DestinationDisplayRef on Via                  |
| FLEXIBLE_LINE_1                          |                          FlexibleLine missing FlexibleLineType                           |
| FLEXIBLE_LINE_10                         |            FlexibleLine illegal use of both BookWhen and MinimumBookingPeriod            |
| FLEXIBLE_LINE_11                         |  FlexibleLine BookWhen without LatestBookingTime or LatestBookingTime without BookWhen   |
| FLEXIBLE_LINE_8                          |                          FlexibleLine illegal FlexibleLineType                           |
| FLEXIBLE_LINE_9                          |                         FlexibleLine illegal FlexibleServiceType                         |
| FLEXIBLE_SERVICE_1                       |                 FlexibleService missing Id on FlexibleServiceProperties                  |
| FLEXIBLE_SERVICE_2                       |               FlexibleService missing version on FlexibleServiceProperties               |
| FLEXIBLE_SERVICE_3                       |          FlexibleService illegal use of both BookWhen and MinimumBookingPeriod           |
| FLEXIBLE_SERVICE_4                       | FlexibleService BookWhen without LatestBookingTime or LatestBookingTime without BookWhen |
| INTERCHANGE_1                            |                              Interchange invalid properties                              |
| INTERCHANGE_2                            |                          Interchange unexpected MaximumWaitTime                          |
| INTERCHANGE_3                            |                          Interchange excessive MaximumWaitTime                           |
| JOURNEY_PATTERN_1                        |                   JourneyPattern illegal element ServiceJourneyPattern                   |
| JOURNEY_PATTERN_2                        |                          JourneyPattern missing JourneyPattern                           |
| JOURNEY_PATTERN_3                        |                             JourneyPattern missing RouteRef                              |
| JOURNEY_PATTERN_4                        |             JourneyPattern missing DestinationDisplayRef on first stop point             |
| JOURNEY_PATTERN_5                        |             JourneyPattern illegal DestinationDisplayRef on last stop point              |
| JOURNEY_PATTERN_6                        |                 JourneyPattern stop point without boarding or alighting                  |
| JOURNEY_PATTERN_7                        |                 JourneyPattern illegal repetition of DestinationDisplay                  |
| JOURNEY_PATTERN_8                        |          JourneyPattern  illegal use of both BookWhen and MinimumBookingPeriod           |
| JOURNEY_PATTERN_9                        | JourneyPattern  BookWhen without LatestBookingTime or LatestBookingTime without BookWhen |
| LINE_1                                   |                            Line missing Line or FlexibleLine                             |
| LINE_2                                   |                                    Line missing Name                                     |
| LINE_3                                   |                                 Line missing PublicCode                                  |
| LINE_4                                   |                                Line missing TransportMode                                |
| LINE_5                                   |                              Line missing TransportSubmode                               |
| LINE_6                                   |                             Line with incorrect use of Route                             |
| LINE_7                                   |                           Line missing Network or GroupOfLines                           |
| LINE_8                                   |                       Invalid color coding length on Presentation                        |
| LINE_9                                   |                        Invalid color coding value on Presentation                        |
| NETEX_ID_1                               |                             NeTEx ID duplicated across files                             |
| NETEX_ID_10                              |                          Duplicate NeTEx ID across common files                          |
| NETEX_ID_5                               |                              NeTEx ID unresolved reference                               |
| NETEX_ID_6                               |                          NeTEx ID reference to invalid element                           |
| NETEX_ID_7                               |                                  NeTEx ID invalid value                                  |
| NETEX_ID_8                               |                           NeTEx ID missing version on elements                           |
| NETEX_ID_9                               |                          NeTEx ID missing version on reference                           |
| NETWORK_1                                |                               Network missing AuthorityRef                               |
| NETWORK_2                                |                             Network missing Name on Network                              |
| NETWORK_3                                |                           Network missing Name on GroupOfLines                           |
| NOTICE_1                                 |                                   Notice missing Text                                    |
| NOTICE_2                                 |                        Notice missing Text with alternative text                         |
| NOTICE_3                                 |                      Notice missing language with alternative text                       |
| NOTICE_4                                 |                           Notice duplicated alternative texts                            |
| NOTICE_5                                 |                               Notice duplicated assignment                               |
| NOTICE_6                                 |                  Notice assignment missing reference to noticed object                   |
| NOTICE_7                                 |                      Notice assignment missing reference to notice                       |
| OPERATOR_1                               |                              Operator missing CompanyNumber                              |
| OPERATOR_2                               |                                  Operator missing Name                                   |
| OPERATOR_3                               |                                Operator missing LegalName                                |
| OPERATOR_4                               |                             Operator missing ContactDetails                              |
| OPERATOR_5                               |                         Operator missing Url for ContactDetails                          |
| OPERATOR_6                               |                      Operator missing CustomerServiceContactDetails                      |
| OPERATOR_7                               |                  Operator missing Url for CustomerServiceContactDetails                  |
| PASSENGER_STOP_ASSIGNMENT_1              |                  PassengerStopAssignment missing ScheduledStopPointRef                   |
| PASSENGER_STOP_ASSIGNMENT_2              |                         PassengerStopAssignment missing QuayRef                          |
| PASSENGER_STOP_ASSIGNMENT_3              |                    PassengerStopAssignment duplicated Quay assignment                    |
| RESOURCE_FRAME_IN_LINE_FILE              |                            ResourceFrame must be exactly one                             |
| ROUTE_1                                  |                                      Route missing                                       |
| ROUTE_2                                  |                                    Route missing Name                                    |
| ROUTE_3                                  |                                  Route missing LineRef                                   |
| ROUTE_4                                  |                              Route missing pointsInSequence                              |
| ROUTE_5                                  |                                Route illegal DirectionRef                                |
| ROUTE_6                                  |                                  Route duplicated order                                  |
| SERVICE_CALENDAR_1                       |                              ServiceCalendar unused DayType                              |
| SERVICE_CALENDAR_2                       |                          ServiceCalendar empty ServiceCalendar                           |
| SERVICE_CALENDAR_3                       |                              ServiceCalendar missing ToDate                              |
| SERVICE_CALENDAR_4                       |                             ServiceCalendar missing FromDate                             |
| SERVICE_CALENDAR_5                       |                          ServiceCalendar invalid time interval                           |
| SERVICE_FRAME_1                          |                       ServiceFrame unexpected element GroupOfLines                       |
| SERVICE_FRAME_2                          |                       ServiceFrame unexpected element timingPoints                       |
| SERVICE_FRAME_3                          |                      ServiceFrame missing Projection on RoutePoint                       |
| SERVICE_FRAME_IN_COMMON_FILE_1           |                           ServiceFrame unexpected element Line                           |
| SERVICE_FRAME_IN_COMMON_FILE_2           |                          ServiceFrame unexpected element Route                           |
| SERVICE_FRAME_IN_COMMON_FILE_3           |                      ServiceFrame unexpected element JourneyPattern                      |
| SERVICE_JOURNEY_1                        |                                ServiceJourney must exist                                 |
| SERVICE_JOURNEY_10                       |                    ServiceJourney missing reference to JourneyPattern                    |
| SERVICE_JOURNEY_11                       |                   ServiceJourney invalid overriding of transport modes                   |
| SERVICE_JOURNEY_12                       |                            ServiceJourney missing OperatorRef                            |
| SERVICE_JOURNEY_13                       |                    ServiceJourney missing reference to calendar data                     |
| SERVICE_JOURNEY_14                       |                   ServiceJourney duplicated reference to calendar data                   |
| SERVICE_JOURNEY_15                       |                        ServiceJourney missing some passing times                         |
| SERVICE_JOURNEY_16                       |                             ServiceJourney multiple versions                             |
| SERVICE_JOURNEY_17                       |                      Non-unique NeTEx id for TimetabledPassingTime                       |
| SERVICE_JOURNEY_2                        |                           ServiceJourney illegal element Call                            |
| SERVICE_JOURNEY_3                        |                       ServiceJourney missing element PassingTimes                        |
| SERVICE_JOURNEY_4                        |                       ServiceJourney missing arrival and departure                       |
| SERVICE_JOURNEY_5                        |                          ServiceJourney missing departure times                          |
| SERVICE_JOURNEY_6                        |                    ServiceJourney missing arrival time for last stop                     |
| SERVICE_JOURNEY_7                        |                      ServiceJourney identical arrival and departure                      |
| SERVICE_JOURNEY_8                        |                    ServiceJourney missing id on TimetabledPassingTime                    |
| SERVICE_JOURNEY_9                        |                 ServiceJourney missing version on TimetabledPassingTime                  |
| SERVICE_LINK_1                           |                             ServiceLink missing FromPointRef                             |
| SERVICE_LINK_2                           |                              ServiceLink missing ToPointRef                              |
| SERVICE_LINK_3                           |                         ServiceLink missing element Projections                          |
| SERVICE_LINK_4                           |                           ServiceLink missing coordinate list                            |
| SERVICE_LINK_5                           |                              ServiceLink less than 2 points                              |
| SITE_FRAME_IN_COMMON_FILE                |                      SiteFrame unexpected SiteFrame in Common file                       |
| SITE_FRAME_IN_LINE_FILE                  |                       SiteFrame unexpected SiteFrame in Line file                        |
| TIMETABLE_FRAME_IN_COMMON_FILE           |                          TimetableFrame illegal in Common file                           |
| TRANSPORT_MODE_ON_LINE                   |                                Line Illegal TransportMode                                |
| TRANSPORT_MODE_ON_SERVICE_JOURNEY        |                          Service Journey Illegal TransportMode                           |
| TRANSPORT_SUB_MODE_ON_LINE               |                              Line Illegal TransportSubMode                               |
| TRANSPORT_SUB_MODE_ON_SERVICE_JOURNEY    |                         Service Journey Illegal TransportSubMode                         |
| VALIDITY_CONDITIONS_IN_COMMON_FILE_1     |            ValidityConditions missing in ServiceFrame or ServiceCalendarFrame            |
| VALIDITY_CONDITIONS_IN_COMMON_FILE_2     |                       ValidityConditions missing in ResourceFrames                       |
| VALIDITY_CONDITIONS_IN_COMMON_FILE_3     |                       ValidityConditions missing in ServiceFrames                        |
| VALIDITY_CONDITIONS_IN_COMMON_FILE_4     |                   ValidityConditions missing in ServiceCalendarFrames                    |
| VALIDITY_CONDITIONS_IN_LINE_FILE_1       |                         ValidityConditions missing in all frames                         |
| VALIDITY_CONDITIONS_IN_LINE_FILE_2       |                       ValidityConditions missing in ServiceFrames                        |
| VALIDITY_CONDITIONS_IN_LINE_FILE_3       |                   ValidityConditions missing in ServiceCalendarFrames                    |
| VALIDITY_CONDITIONS_IN_LINE_FILE_4       |                      ValidityConditions missing in TimeTableFrames                       |
| VALIDITY_CONDITIONS_IN_LINE_FILE_5       |                    ValidityConditions missing in VehicleScheduleFrame                    |
| VERSION_NON_NUMERIC                      |                                Non-numeric NeTEx version                                 |

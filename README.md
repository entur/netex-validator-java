# NeTEx validator Java

[![CircleCI](https://circleci.com/gh/entur/netex-validator-java/tree/main.svg?style=svg)](https://circleci.com/gh/entur/netex-validator-java/tree/main)

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

# Configurable validators
The entry point **NetexValidatorsRunner** can be configured with a list of **NetexValidator** instances that are executed sequentially during a validation run, after a successful XML Schema validation.

The library offers default implementations for:
- XPath validation
- NeTEx id uniqueness validation
- NeTEx reference consistency check

The library can be extended with custom NetexValidator implementations (see [Antu](https://github.com/entur/antu) for examples of Entur-specific validators)

# Configurable rule severity and description
The rule severity (INFO, WARNING, ERROR) and the rule message displayed in the validation report are specified in a configuration file (YAML). The default configuration can be found in ```configuration.default.yaml```

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
NetexValidator netexValidator = new CustomNetexValidator()
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

| Sr. | Rule Code                                |                                                              Rule Description                                                               |
|-----|------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------:|
| 1   | AUTHORITY_1                              |                                                 Missing CompanyNumber element on Authority                                                  |
| 2   | AUTHORITY_2                              |                                                      Missing Name element on Authority                                                      |
| 3   | AUTHORITY_3                              |                                                   Missing LegalName element on Authority                                                    |
| 4   | AUTHORITY_4                              |                                                     Missing ContactDetails on Authority                                                     |
| 5   | AUTHORITY_5                              |                   The Url must be defined for ContactDetails on Authority and it must start with 'http://' or 'https://'                    |
| 6   | BLOCK_1                                  |                                      At least one Block or TrainBlock required in VehicleScheduleFrame                                      |
| 7   | BLOCK_2                                  |                                               At least one Journey must be defined for Block                                                |
| 8   | BLOCK_3                                  |                                               At least one DayType must be defined for Block                                                |
| 9   | BOOKING_1                                |                                                       Illegal value for BookingAccess                                                       |
| 10  | BOOKING_2                                |                                                       Illegal value for BookingMethod                                                       |
| 11  | BOOKING_3                                |                                                         Illegal value for BookWhen                                                          |
| 12  | BOOKING_4                                |   Mandatory booking property BookingContact not specified on FlexibleServiceProperties, FlexibleLine or on all StopPointInJourneyPatterns   |
| 13  | BOOKING_4                                |   Mandatory booking property BookingMethods not specified on FlexibleServiceProperties, FlexibleLine or on all StopPointInJourneyPatterns   |
| 14  | BOOKING_5                                | Either BookWhen or MinimumBookingPeriod should be specified on FlexibleServiceProperties, FlexibleLine or on all StopPointInJourneyPatterns |
| 15  | BUY_WHEN_1                               |                                                          Illegal value for BuyWhen                                                          |
| 16  | COMPOSITE_FRAME_1                        |                        A CompositeFrame must define a ValidityCondition valid for all data within the CompositeFrame                        |
| 17  | COMPOSITE_FRAME_2                        |                                      ValidityConditions defined inside a frame inside a CompositeFrame                                      |
| 18  | COMPOSITE_FRAME_3                        |                                           ValidBetween missing either or both of FromDate/ToDate                                            |
| 19  | COMPOSITE_FRAME_4                        |                                               FromDate cannot be after ToDate on ValidBetween                                               |
| 20  | COMPOSITE_FRAME_4                        |                                  AvailabilityCondition must have either FromDate or ToDate or both present                                  |
| 21  | COMPOSITE_FRAME_5                        |                                          FromDate cannot be after ToDate on AvailabilityCondition                                           |
| 22  | COMPOSITE_SITE_FRAME_IN_COMMON_FILE      |                                              Unexpected element SiteFrame. It will be ignored                                               |
| 23  | COMPOSITE_TIMETABLE_FRAME_IN_COMMON_FILE |                                                 Timetable frame not allowed in common files                                                 |
| 24  | DATED_SERVICE_JOURNEY_1                  |                                               Missing OperatingDayRef on DatedServiceJourney                                                |
| 25  | DATED_SERVICE_JOURNEY_2                  |                                              Missing ServiceJourneyRef on DatedServiceJourney                                               |
| 26  | DATED_SERVICE_JOURNEY_3                  |                                              Multiple ServiceJourneyRef on DatedServiceJourney                                              |
| 27  | DATED_SERVICE_JOURNEY_4                  |                                          DatedServiceJourney is repeated with a different version                                           |
| 28  | DATED_SERVICE_JOURNEY_5                  |                               Multiple references from a DatedServiceJourney to the same DatedServiceJourney                                |
| 29  | DEAD_RUN_1                               |                                                The Dead run does not reference passing times                                                |
| 30  | DEAD_RUN_2                               |                                              The Dead run does not reference a journey pattern                                              |
| 31  | DEAD_RUN_3                               |                                                  The Dead run does not reference day types                                                  |
| 32  | DESTINATION_DISPLAY_1                    |                                                   Missing FrontText on DestinationDisplay                                                   |
| 33  | DESTINATION_DISPLAY_2                    |                                                    Missing DestinationDisplayRef on Via                                                     |
| 34  | FLEXIBLE_LINE_1                          |                                                  Missing FlexibleLineType on FlexibleLine                                                   |
| 35  | FLEXIBLE_LINE_10                         |                              Only one of BookWhen or MinimumBookingPeriod should be specified on FlexibleLine                               |
| 36  | FLEXIBLE_LINE_11                         |                                    BookWhen must be used together with LatestBookingTime on FlexibleLine                                    |
| 37  | FLEXIBLE_LINE_8                          |                                                  Illegal FlexibleLineType on FlexibleLine                                                   |
| 38  | FLEXIBLE_LINE_9                          |                                                Illegal FlexibleServiceType on ServiceJourney                                                |
| 39  | FLEXIBLE_SERVICE_1                       |                                                   Missing id on FlexibleServiceProperties                                                   |
| 40  | FLEXIBLE_SERVICE_2                       |                                                Missing version on FlexibleServiceProperties                                                 |
| 41  | FLEXIBLE_SERVICE_3                       |                        Only one of BookWhen or MinimumBookingPeriod should be specified on FlexibleServiceProperties                        |
| 42  | FLEXIBLE_SERVICE_4                       |                             BookWhen must be used together with LatestBookingTime on FlexibleServiceProperties                              |
| 43  | INTERCHANGE_1                            |                             The 'Planned' and 'Advertised' properties of an Interchange should not be specified                             |
| 44  | INTERCHANGE_2                            |                                  Guaranteed Interchange should not have a maximum wait time value of zero                                   |
| 45  | INTERCHANGE_3                            | The maximum waiting time after planned departure for the interchange consumer journey (MaximumWaitTime) should not be longer than one hour  |
| 46  | JOURNEY_PATTERN_1                        |                                                      ServiceJourneyPattern not allowed                                                      |
| 47  | JOURNEY_PATTERN_2                        |                                               No JourneyPattern defined in the Service Frame                                                |
| 48  | JOURNEY_PATTERN_3                        |                                                     Missing RouteRef on JourneyPattern                                                      |
| 49  | JOURNEY_PATTERN_4                        |                                      Missing DestinationDisplayRef on first StopPointInJourneyPattern                                       |
| 50  | JOURNEY_PATTERN_5                        |                                     DestinationDisplayRef not allowed on last StopPointInJourneyPattern                                     |
| 51  | JOURNEY_PATTERN_6                        |                                       StopPointInJourneyPattern neither allows boarding nor alighting                                       |
| 52  | JOURNEY_PATTERN_7                        |              StopPointInJourneyPattern declares reference to the same DestinationDisplay as previous StopPointInJourneyPattern              |
| 53  | JOURNEY_PATTERN_8                        |                        Only one of BookWhen or MinimumBookingPeriod should be specified on StopPointInJourneyPattern                        |
| 54  | JOURNEY_PATTERN_9                        |                             BookWhen must be used together with LatestBookingTime on StopPointInJourneyPattern                              |
| 55  | LINE_1                                   |                                                There must be either Lines or Flexible Lines                                                 |
| 56  | LINE_2                                   |                                                            Missing Name on Line                                                             |
| 57  | LINE_3                                   |                                                         Missing PublicCode on Line                                                          |
| 58  | LINE_4                                   |                                                        Missing TransportMode on Line                                                        |
| 59  | LINE_5                                   |                                                      Missing TransportSubmode on Line                                                       |
| 60  | LINE_6                                   |                                         Routes should not be defined within a Line or FlexibleLine                                          |
| 61  | LINE_7                                   |                           A Line must refer to a GroupOfLines or a Network through element RepresentedByGroupRef                            |
| 62  | LINE_8                                   |                                           Line colour should be encoded with 6 hexadecimal digits                                           |
| 63  | LINE_9                                   |                                         Line colour should be encoded with valid hexadecimal digits                                         |
| 64  | NETWORK_1                                |                                                       Missing AuthorityRef on Network                                                       |
| 65  | NETWORK_2                                |                                                       Missing Name element on Network                                                       |
| 66  | NETWORK_3                                |                                                    Missing Name element on GroupOfLines                                                     |
| 67  | NOTICE_1                                 |                                                       Missing element Text for Notice                                                       |
| 68  | NOTICE_2                                 |                                          Missing or empty element Text for Notice Alternative Text                                          |
| 69  | NOTICE_3                                 |                                              Missing element Lang for Notice Alternative Text                                               |
| 70  | NOTICE_4                                 |                                         The Notice has two Alternative Texts with the same language                                         |
| 71  | NOTICE_5                                 |                                          The notice is assigned multiple times to the same object                                           |
| 72  | NOTICE_6                                 |                                             The notice assignment does not reference an object                                              |
| 73  | NOTICE_7                                 |                                              The notice assignment does not reference a notice                                              |
| 74  | OPERATOR_1                               |                                                  Missing CompanyNumber element on Operator                                                  |
| 75  | OPERATOR_2                               |                                                          Missing Name on Operator                                                           |
| 76  | OPERATOR_3                               |                                                    Missing LegalName element on Operator                                                    |
| 77  | OPERATOR_4                               |                                                 Missing ContactDetails element on Operator                                                  |
| 78  | OPERATOR_5                               |                             At least one of Url, Phone or Email must be defined for ContactDetails on Operator                              |
| 79  | OPERATOR_6                               |                                          Missing CustomerServiceContactDetails element on Operator                                          |
| 80  | OPERATOR_7                               |                                      Missing Url element for CustomerServiceContactDetails on Operator                                      |
| 81  | PASSENGER_STOP_ASSIGNMENT_1              |                                          Missing ScheduledStopPointRef on PassengerStopAssignment                                           |
| 82  | PASSENGER_STOP_ASSIGNMENT_2              |                                                 Missing QuayRef on PassengerStopAssignment                                                  |
| 83  | PASSENGER_STOP_ASSIGNMENT_3              |                                    The same quay is assigned more than once in PassengerStopAssignments                                     |
| 84  | RESOURCE_FRAME_IN_LINE_FILE              |                                                 Exactly one ResourceFrame should be present                                                 |
| 85  | ROUTE_1                                  |                                                     There should be at least one Route                                                      |
| 86  | ROUTE_2                                  |                                                            Missing Name on Route                                                            |
| 87  | ROUTE_3                                  |                                                          Missing lineRef on Route                                                           |
| 88  | ROUTE_4                                  |                                                      Missing pointsInSequence on Route                                                      |
| 89  | ROUTE_5                                  |                                            DirectionRef not allowed on Route (use DirectionType)                                            |
| 90  | ROUTE_6                                  |                                                 Several points on route have the same order                                                 |
| 91  | SERVICE_CALENDAR_1                       |                                        The DayType is not assigned to any calendar dates or periods                                         |
| 92  | SERVICE_CALENDAR_2                       |                                  ServiceCalendar does not contain neither DayTypes nor DayTypeAssignments                                   |
| 93  | SERVICE_CALENDAR_3                       |                                                      Missing ToDate on ServiceCalendar                                                      |
| 94  | SERVICE_CALENDAR_4                       |                                                     Missing FromDate on ServiceCalendar                                                     |
| 95  | SERVICE_CALENDAR_5                       |                                             FromDate cannot be after ToDate on ServiceCalendar                                              |
| 96  | SERVICE_FRAME_1                          |                                             Unexpected element groupsOfLines outside of Network                                             |
| 97  | SERVICE_FRAME_2                          |                                              Unexpected element timingPoints. Content ignored                                               |
| 98  | SERVICE_FRAME_3                          |                                                      Missing Projection on RoutePoint                                                       |
| 99  | SERVICE_FRAME_IN_COMMON_FILE_1           |                                                      Line not allowed in common files                                                       |
| 100 | SERVICE_FRAME_IN_COMMON_FILE_2           |                                                      Route not allowed in common files                                                      |
| 101 | SERVICE_FRAME_IN_COMMON_FILE_3           |                                                 JourneyPattern not allowed in common files                                                  |
| 102 | SERVICE_JOURNEY_1                        |                                                 There should be at least one ServiceJourney                                                 |
| 103 | SERVICE_JOURNEY_10                       |                                            The ServiceJourney does not refer to a JourneyPattern                                            |
| 104 | SERVICE_JOURNEY_11                       |                   If overriding Line TransportMode or TransportSubmode on a ServiceJourney, both elements must be present                   |
| 105 | SERVICE_JOURNEY_12                       |                                         Missing OperatorRef on ServiceJourney (not defined on Line)                                         |
| 106 | SERVICE_JOURNEY_13                       |                                   The ServiceJourney does not refer to DayTypes nor DatedServiceJourneys                                    |
| 107 | SERVICE_JOURNEY_14                       |                                    The ServiceJourney references both DayTypes and DatedServiceJourneys                                     |
| 108 | SERVICE_JOURNEY_15                       |                               ServiceJourney does not specify passing time for all StopPointInJourneyPattern                                |
| 109 | SERVICE_JOURNEY_16                       |                                             ServiceJourney is repeated with a different version                                             |
| 110 | SERVICE_JOURNEY_2                        |                                                          Element Call not allowed                                                           |
| 111 | SERVICE_JOURNEY_3                        |                                       The ServiceJourney does not specify any TimetabledPassingTimes                                        |
| 112 | SERVICE_JOURNEY_4                        |                TimetabledPassingTime contains neither DepartureTime/EarliestDepartureTime nor ArrivalTime/LatestArrivalTime                 |
| 113 | SERVICE_JOURNEY_5                        |                                     All TimetabledPassingTime except last call must have DepartureTime                                      |
| 114 | SERVICE_JOURNEY_6                        |                                              Last TimetabledPassingTime must have ArrivalTime                                               |
| 115 | SERVICE_JOURNEY_7                        |                                                  ArrivalTime is identical to DepartureTime                                                  |
| 116 | SERVICE_JOURNEY_8                        |                                                     Missing id on TimetabledPassingTime                                                     |
| 117 | SERVICE_JOURNEY_9                        |                                                  Missing version on TimetabledPassingTime                                                   |
| 118 | SERVICE_LINK_1                           |                                                     Missing FromPointRef on ServiceLink                                                     |
| 119 | SERVICE_LINK_2                           |                                                      Missing ToPointRef on ServiceLink                                                      |
| 120 | SERVICE_LINK_3                           |                                                 Missing projections element on ServiceLink                                                  |
| 121 | SITE_FRAME_IN_COMMON_FILE                |                                              Unexpected element SiteFrame. It will be ignored                                               |
| 122 | SITE_FRAME_IN_LINE_FILE                  |                                              Unexpected element SiteFrame. It will be ignored                                               |
| 123 | TIMETABLE_FRAME_IN_COMMON_FILE           |                                                 Timetable frame not allowed in common files                                                 |
| 124 | TRANSPORT_MODE                           |                                                            Illegal TransportMode                                                            |
| 125 | TRANSPORT_SUB_MODE                       |                                                          Illegal TransportSubMode                                                           |
| 126 | VALIDITY_CONDITIONS_IN_COMMON_FILE_1     |                                  Neither ServiceFrame nor ServiceCalendarFrame defines ValidityConditions                                   |
| 127 | VALIDITY_CONDITIONS_IN_COMMON_FILE_2     |                                             Multiple ResourceFrames without validity conditions                                             |
| 128 | VALIDITY_CONDITIONS_IN_COMMON_FILE_3     |                                             Multiple ServiceFrames without validity conditions                                              |
| 129 | VALIDITY_CONDITIONS_IN_COMMON_FILE_4     |                                         Multiple ServiceCalendarFrames without validity conditions                                          |
| 130 | VALIDITY_CONDITIONS_IN_LINE_FILE_1       |                          Neither ServiceFrame, ServiceCalendarFrame nor TimetableFrame defines ValidityConditions                           |
| 131 | VALIDITY_CONDITIONS_IN_LINE_FILE_2       |                                          Multiple frames of same type without validity conditions                                           |
| 132 | VALIDITY_CONDITIONS_IN_LINE_FILE_3       |                                          Multiple frames of same type without validity conditions                                           |
| 133 | VALIDITY_CONDITIONS_IN_LINE_FILE_4       |                                          Multiple frames of same type without validity conditions                                           |
| 134 | VALIDITY_CONDITIONS_IN_LINE_FILE_5       |                                          Multiple frames of same type without validity conditions                                           |
| 135 | VERSION_NON_NUMERIC                      |                                                          Non-numeric NeTEx version                                                          |
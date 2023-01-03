![CodeQL](https://github.com/jdpgrailsdev/oasis-timeline/workflows/CodeQL/badge.svg?branch=master) ![CI/CD](https://github.com/jdpgrailsdev/oasis-timeline/workflows/CI%2FCD%20Pipeline/badge.svg)
# The Oasis Timeline Project
An interactive timeline of events for the 🇬🇧 rock band [Oasis](https://www.oasisinet.com/#!/home).

The timeline is hosted at [https://www.oasis-timeline.com](https://www.oasis-timeline.com).

A feed of the timeline events is available on Twitter at [@OasisTimeline](https://twitter.com/oasistimeline).

## Projects

The project is broken up into the following sub-modules:

### Autobot

Java-based [Spring Boot application](https://spring.io/projects/spring-boot) that uses the [timeline data](web/src/data) to produce tweets via the [Twitter API](https://developer.twitter.com/en/docs/api-reference-index) on a daily basis.

### Web

[Typescript](https://www.typescriptlang.org/) based web application that hosts a visual timeline of events.

## Build and Deploy

### Build

```sh
> ./gradlew clean build
```

### Deploy

```sh
> ./gradlew clean build publish
```
## Operations

For help with operations of the services produced by this project, please see the associated [runbook](RUNBOOK.md).

## Contributing

The goal of the project is to be as accurate as possible.  If you have information that may improve the timeline or have an event that you believe should be included in
the timeline, please do not hesitate to contact us!  The best place to do so is to add a comment to the [Oasis Timeline thread](http://live4ever.proboards.com/thread/90673/oasis-timeline-project) on the [Live4ever Forum](http://live4ever.proboards.com/). 

Likewise, if you find an issue with the site itself (bug, typo or other problem), please create an issue [here in GitHub](https://github.com/jdpgrailsdev/oasis-timeline/issues).

## License

Copyright 2018-2023.  Release under the [Apache 2.0 license](LICENSE).

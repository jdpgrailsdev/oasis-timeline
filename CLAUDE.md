# Project Overview

This is a Kotlin project using Gradle for the build system and JUnit 6 for testing.  The project is structured with two main components:  `autobot`, which is a Spring Boot application that runs a scheduled task once a day to publish events to social media (Twitter, Bluesky, etc) and `web` with is a Typescript/ReactJS web application.

[Link to project README](README.md)

## Code Style Guidelines

* **Java version:** Java 21+ compatibility is required.
* **Indentation:** Use 2 spaces for indentation. Do not use tabs.
* **Naming:** Follow standard Java/Kotlin naming conventions (camelCase for methods/variables, PascalCase for classes).
* **Documentation:** All public methods and classes must have JavaDoc.
* **Frameworks:** Use Spring Boot patterns, preferring constructor injection over field injection.
* **Linting/Formatting:** The project uses Google Java Format for Java source and ktlint for Kotlin source.  All code must be formatted according to these tools before committing.

## Common Development Commands

All Gradle commands should be run using the wrapper (`./gradlew`).

* **Build all projects:** `./gradlew build`
* **Run all tests:** `./gradlew test` (or `./gradlew clean build` for a clean build).
* **Run specific `autobot` application test class:** `./gradlew :oasis-timeline-autobot:test --tests "com.example.HelloControllerTest"`.
* **Run `autobot` application:** `./gradlew :oasis-timeline-autobot:bootRun`.
* **Run the `web` application:** `cd web; npm run-script start`

# Oasis Timeline Autobot

Java-based [Spring Boot application](https://spring.io/projects/spring-boot) that uses the [timeline data](web/src/data) to produce tweets via the [Twitter API](https://developer.twitter.com/en/docs/api-reference-index) on a daily basis.

## Build and Deploy

### Build

```sh
> ./gradlew :oasis-timeline-autobot:clean :oasis-timeline-autobot:check :oasis-timeline-autobot:build
```

### Deploy

```sh
> ./gradlew :oasis-timeline-autobot:clean :oasis-timeline-autobot:check :oasis-timeline-autobot:build :oasis-timeline-autobot:deployHeroku
```

### Local Execution

```sh
> ./gradlew :oasis-timeline-autobot:clean :oasis-timeline-autobot:bootRun
```

## Operations

### Watch Logs

```sh
> heroku logs --tail -a oasis-timeline-autobot
```


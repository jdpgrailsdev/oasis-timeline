# Oasis Timeline Autobot

Java-based [Spring Boot application](https://spring.io/projects/spring-boot) that uses the [timeline data](../web/src/data) to produce posts to the following social networks on a daily basis:

* Tweets via the [Twitter API](https://developer.twitter.com/en/docs/api-reference-index)
* Posts via the [Bluesky API](https://docs.bsky.app/docs/get-started)

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

#### Docker

```sh
> ./gradlew :oasis-timeline-autobot:buildDockerImage
> docker run -e INSERT_API_KEY="REPLACE_ME" -e METRICS_API_URI="http://localhost" -e NEW_RELIC_APP_NAME="oasis-timeline-autobot" -e NEW_RELIC_LICENSE_KEY="REPLACE_ME" -e SPRING_ACTUATOR_USERNAME="user" -e SPRING_ACTUATOR_PASSWORD="password" -e SPRING_PROFILES_ACTIVE="development" -e TWITTER_OAUTH_CONSUMER_KEY="REPLACE_ME" -e TWITTER_OAUTH_CONSUMER_SECRET="REPLACE_ME" -e TWITTER_OAUTH_ACCESS_TOKEN="REPLACE_ME" -e TWITTER_OAUTH_ACCESS_TOKEN_SECRET="REPLACE_ME" -p 8081:8080 oasis-timeline-autobot
```

## Operations

### Watch Logs

```sh
> heroku logs --tail -a oasis-timeline-autobot
```

# Oasis Timeline Autobot

Publishes the daily timeline events to Twitter.

## Build and Deploy

```sh
> ./gradlew :oasis-timeline-autobot:clean :oasis-timeline-autobot:check :oasis-timeline-autobot:build :oasis-timeline-autobot:deployHeroku
```

### Watch Logs

```sh
> heroku logs --tail -a oasis-timeline-autobot
```
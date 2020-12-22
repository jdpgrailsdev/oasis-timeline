# Operations Runbook

## Oasis Timeline Autobot

#### Restarting

In the event that the running application needs to be restarted, use the following [Heroku CLI command](https://devcenter.heroku.com/articles/heroku-cli-commands):

```sh
> heroku dyno:restart -a oasis-timeline-autobot
```

#### Logging

To view the running application logs, use the following [Heroku CLI command](https://devcenter.heroku.com/articles/heroku-cli-commands):

```sh
> heroku logs --tail -a oasis-timeline-autobot
```

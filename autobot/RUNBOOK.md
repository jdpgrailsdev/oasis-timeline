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

#### Twitter

##### Oauth2 Authentication

If the application loses its access to the Twitter API, follow these steps to restore it:

1. In a browser window, go to https://oasis-timeline-autobot.herokuapp.com/oauth2/authorize.  This will re-direct to Twitter to authorize the app and should redirect back to https://oasis-timeline-autobot.herokuapp.com/oauth2/callback.  If everything is successful, `OK` will be displayed as the response in the browser window. 
2. In the same browser, go to https://oasis-timeline-autobot.herokuapp.com/oauth2/access_tokens/refresh to refresh the tokens.  A response of `SUCCESS` will be displayed if everything worked.
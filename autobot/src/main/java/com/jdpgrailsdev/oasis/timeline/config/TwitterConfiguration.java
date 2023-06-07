package com.jdpgrailsdev.oasis.timeline.config;

import com.github.scribejava.core.pkce.PKCE;
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.ITemplateEngine;

/** Spring configuration for Twitter related beans. */
@Configuration
public class TwitterConfiguration {

  /**
   * Defines the {@link TweetContext} bean.
   *
   * @return The {@link TweetContext} bean.
   */
  @Bean
  @ConfigurationProperties(prefix = "tweet.context")
  public TweetContext tweetContext() {
    return new TweetContext();
  }

  @Bean
  public TwitterCredentialsOAuth2 twitterCredentials(
      @Value("${TWITTER_OAUTH2_CLIENT_ID}") final String clientId,
      @Value("${TWITTER_OAUTH2_CLIENT_SECRET}") final String clientSecret,
      @Value("${TWITTER_OAUTH2_ACCESS_TOKEN:}") final String accessToken,
      @Value("${TWITTER_OAUTH2_REFRESH_TOKEN:}") final String refreshToken) {
    return new TwitterCredentialsOAuth2(clientId, clientSecret, accessToken, refreshToken, true);
  }

  @Bean
  public TwitterApiUtils twitterApiUtils(final TwitterCredentialsOAuth2 twitterCredentials) {
    return new TwitterApiUtils(twitterCredentials);
  }

  @SuppressWarnings("AbbreviationAsWordInName")
  @Bean
  public TwitterOAuth20Service twitterOAuth2Service(
      @Value("${server.base-url}") final String baseUrl,
      @Value("${oauth2.twitter.scopes}") final String scopes,
      final TwitterCredentialsOAuth2 twitterCredentials) {
    return new TwitterOAuth20Service(
        twitterCredentials.getTwitterOauth2ClientId(),
        twitterCredentials.getTwitterOAuth2ClientSecret(),
        String.format("%s/oauth2/callback", baseUrl),
        scopes);
  }

  @Bean
  public PKCE pkce(@Value("${oauth2.pkce.challenge}") final String challenge) {
    final PKCE pkce = new PKCE();
    pkce.setCodeChallenge(challenge);
    pkce.setCodeChallengeMethod(PKCECodeChallengeMethod.PLAIN);
    pkce.setCodeVerifier(challenge);
    return pkce;
  }

  /**
   * Defines the {@link TweetFormatUtils} bean.
   *
   * @param templateEngine The template engine used to render the tweet text.
   * @param tweetContext The {@link TweetContext}.
   * @return The {@link TweetFormatUtils} bean.
   */
  @Bean
  public TweetFormatUtils tweetFormatUtils(
      @Qualifier("textTemplateEngine") final ITemplateEngine templateEngine,
      final TweetContext tweetContext) {
    return new TweetFormatUtils(templateEngine, tweetContext);
  }
}

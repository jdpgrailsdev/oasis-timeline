package com.jdpgrailsdev.oasis.timeline;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

/**
 * Custom Spring {@link ApplicationContextInitializer} that create and stop a {@link WireMockServer}
 * for integration test use.
 */
public class WireMockInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(final ConfigurableApplicationContext applicationContext) {
    // Create and start the WireMock server
    final WireMockServer wireMockServer = new WireMockServer(options().port(9091));
    wireMockServer.start();

    // Register the WireMock server as a Spring bean
    applicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);

    // Register a context lisener to stop the WireMock server when the context is closed
    applicationContext.addApplicationListener(
        applicationEvent -> {
          if (applicationEvent instanceof ContextClosedEvent) {
            wireMockServer.stop();
          }
        });
  }
}

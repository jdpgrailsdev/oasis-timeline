package com.jdpgrailsdev.oasis.timeline;

import com.jdpgrailsdev.oasis.timeline.config.ApplicationConfiguration;
import com.newrelic.api.agent.NewRelic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.lang.Thread.UncaughtExceptionHandler;

@SpringBootApplication
@Import(ApplicationConfiguration.class)
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) {
        // Notice any uncaught exceptions at runtime.
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                log.error(e.getMessage(), e);
                NewRelic.noticeError(e);
            }
        });

        SpringApplication.run(Application.class, args);
    }
}

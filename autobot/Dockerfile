FROM eclipse-temurin:17

RUN mkdir -p /opt/app
COPY build/libs/*.jar /opt/app

EXPOSE 8081
CMD ["java", "-Duser.timezone=UTC", "-Dnewrelic.config.distributed_tracing.enabled=true", "-Dnewrelic.config.span_events=true", "-Dnewrelic.environment=${SPRING_PROFILES_ACTIVE}", "-XX:-OmitStackTraceInFastThrow", "-javaagent:/opt/app/newrelic-agent.jar", "-jar", "/opt/app/oasis-timeline-autobot.jar"]
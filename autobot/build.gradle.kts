import com.google.common.io.Files as GoogleFiles
import com.google.googlejavaformat.java.Formatter
import com.google.googlejavaformat.java.JavaFormatterOptions

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.util.stream.Collectors

import groovy.json.JsonOutput

import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.yaml.snakeyaml.Yaml

val javaVersion = JavaVersion.VERSION_17

fun String.runCommand(currentWorkingDir: File = file("./")): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        workingDir = currentWorkingDir
        commandLine = this@runCommand.split("\\s".toRegex())
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}

buildscript {
    dependencies {
        listOf(
            "com.squareup.okhttp3:okhttp:${project.property("okhttp3.version")}",
            "com.google.guava:guava:${project.property("guava.version")}",
            "com.google.googlejavaformat:google-java-format:${project.property("google-java-format.version")}",
            "org.yaml:snakeyaml:${project.property("snakeyaml.version")}"
        ).forEach { classpath(it) }
    }
}

plugins {
    id("java")
    id("com.heroku.sdk.heroku-gradle") version "2.0.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.springframework.boot") version "3.0.2"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("checkstyle")
    id("pmd")
    id("com.github.spotbugs") version "5.0.13"
    id("jacoco")
    id("com.github.ben-manes.versions") version "0.44.0"
    id("ca.cutterslade.analyze")
    id("com.bmuschko.docker-remote-api") version "9.0.1"
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

val agent: Configuration by configurations.creating

sourceSets {
    create("intTest") {
        java.srcDir(File("src/intTest/java"))
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val intTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

configurations["intTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

// Static Analysis Plugin Configuration

checkstyle {
    configFile = project.file("config/checkstyle/checkstyle.xml")
    toolVersion = project.property("checkstyle.version").toString()
}

jacoco {
    toolVersion = project.property("jacoco.version").toString()
}

pmd {
    isConsoleOutput = true
    isIgnoreFailures = false
    ruleSets = listOf()
    ruleSetFiles = files("config/pmd/pmd.xml")
    toolVersion = project.property("pmd.version").toString()
}

spotbugs {
    ignoreFailures.set(false)
    setEffort("max")
    setReportLevel("low")
    showProgress.set(false)
    toolVersion.set(project.property("spotbugs.version").toString())
}

// Project Dependencies
dependencies {
    listOf(
        "com.newrelic.agent.java:newrelic-agent:${project.property("newrelic.version")}"
    ).forEach { agent(it) }

    listOf(
        "com.github.spotbugs:spotbugs:${project.property("spotbugs.version")}"
    ).forEach { spotbugs(it) }

    listOf(
        "com.github.spotbugs:spotbugs-annotations:${project.property("spotbugs.version")}",
        "com.fasterxml.jackson.core:jackson-annotations",
        "com.fasterxml.jackson.core:jackson-core",
        "com.fasterxml.jackson.core:jackson-databind",
        "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${project.property("jackson-datatype-jsr310.version")}",
        "com.google.guava:guava:${project.property("guava.version")}",
        "com.newrelic.agent.java:newrelic-api:${project.property("newrelic.version")}",
        "com.newrelic.telemetry:micrometer-registry-new-relic:${project.property("micrometer-registry-new-relic.version")}",
        "io.micrometer:micrometer-core",
        "io.projectreactor:reactor-core",
        "org.slf4j:slf4j-api",
        "org.springframework:spring-beans",
        "org.springframework:spring-context",
        "org.springframework:spring-core",
        "org.springframework:spring-web",
        "org.springframework:spring-webmvc",
        "org.springframework.security:spring-security-web",
        "org.springframework.boot:spring-boot",
        "org.springframework.boot:spring-boot-actuator",
        "org.springframework.boot:spring-boot-actuator-autoconfigure",
        "org.springframework.boot:spring-boot-autoconfigure",
        "org.springframework.boot:spring-boot-starter-actuator",
        "org.springframework.boot:spring-boot-starter-security",
        "org.springframework.boot:spring-boot-starter-thymeleaf",
        "org.springframework.boot:spring-boot-starter-web",
        "org.springframework.security:spring-security-config",
        "org.thymeleaf:thymeleaf",
        "org.thymeleaf:thymeleaf-spring6",
        "org.twitter4j:twitter4j-core:${project.property("twitter4j-core.version")}"
    ).forEach { implementation(it) }

    listOf(
            "org.springframework.boot:spring-boot-properties-migrator"
    ).forEach { runtimeOnly(it) }

    listOf(
        "org.springframework.boot:spring-boot-starter-actuator",
        "org.springframework.boot:spring-boot-starter-security",
        "org.springframework.boot:spring-boot-starter-thymeleaf",
        "org.springframework.boot:spring-boot-starter-web"
    ).forEach { permitUnusedDeclared(it) }

    listOf(
        "org.junit:junit-bom"
    ).forEach {
        testImplementation(platform(it))
    }

    listOf(
        "commons-io:commons-io:${project.property("commons-io.version")}",
        "com.github.tomakehurst:wiremock-jre8-standalone",
        "org.apache.httpcomponents:httpclient",
        "org.apache.httpcomponents:httpcore",
        "org.springframework:spring-test",
        "org.springframework.boot:spring-boot-test",
        "org.springframework.boot:spring-boot-starter-test",
        "org.junit.jupiter:junit-jupiter",
        "org.junit.jupiter:junit-jupiter-api",
        "org.junit.jupiter:junit-jupiter-params",
        "org.junit.platform:junit-platform-commons",
        "org.springframework.cloud:spring-cloud-starter-contract-stub-runner:${project.property("spring-cloud-starter-contract-stub-runner.version")}",
        "org.mockito:mockito-core:${project.property("mockito-core.version")}"
    ).forEach {
        testImplementation(it)
    }

    listOf(
        "org.junit.jupiter:junit-jupiter-engine",
        "org.junit.platform:junit-platform-runner",
    ).forEach {
        testRuntimeOnly(it)
    }
}

// Deployment Configuration

heroku {
    appName = project.name
    jdkVersion = javaVersion.majorVersion
    isIncludeBuildDir = false
    includes = listOf(
        "autobot/build/libs/${project.name}-${project.version}.jar",
        "autobot/build/libs/newrelic-agent.jar"
    )
    processTypes = mapOf(
        "web" to listOf("java", "-Dserver.port=\$PORT",
            "-Duser.timezone=UTC",
            "-Dnewrelic.config.distributed_tracing.enabled=true",
            "-Dnewrelic.config.span_events=true",
            "-Dnewrelic.environment=production",
            "-XX:-OmitStackTraceInFastThrow",
            "-javaagent:autobot/build/libs/newrelic-agent.jar",
            "-jar",
            "autobot/build/libs/${project.name}-${project.version}.jar"
        ).joinToString(" ")
    )
}

// Spring Boot Configuration

tasks.getByName<Jar>("jar") {
    // Disable the building of the "plain" archive to avoid duplicate archives in build/lib
    // This is necessary due to the inclusion of by the jar task by the New Relic Gradle plugins
    // and the bootJar task by Spring Boot plugin.  See
    // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#packaging-executable.and-plain-archives
    // for more details
    enabled = false
}

tasks.bootRun {
    environment(
        mapOf(
            "INSERT_API_KEY" to "",
            "METRICS_API_URI" to "http://localhost",
            "NEW_RELIC_APP_NAME" to "oasis-timeline-autobot",
            "SPRING_ACTUATOR_USERNAME" to "user",
            "SPRING_ACTUATOR_PASSWORD" to "password",
            "SPRING_PROFILES_ACTIVE" to "development",
            "TWITTER_OAUTH_CONSUMER_KEY" to "",
            "TWITTER_OAUTH_CONSUMER_SECRET" to "",
            "TWITTER_OAUTH_ACCESS_TOKEN" to "",
            "TWITTER_OAUTH_ACCESS_TOKEN_SECRET" to ""
        )
    )
    jvmArgs = listOf("-Duser.timezone=UTC")
    mainClass.set(project.property("mainClass").toString())
}

springBoot {
    buildInfo()
    mainClass.set(project.property("mainClass").toString())
}


// Custom Tasks

tasks.register<Copy>("copyDataFile") {
    from("${project.rootProject.projectDir}/web/src/data")
    into("src/main/resources/json")
    include("*.json")
    filter { line ->
        line.replace("<i>", "'")
            .replace("</i>", "'")
            .replace("<[^>]*>".toRegex(), "")
    }
}

tasks.register<Copy>("copyAgent") {
    from(configurations["agent"])
    into("build/libs")
    rename("newrelic-agent-.*\\.jar", "newrelic-agent.jar")
}

tasks.register<com.bmuschko.gradle.docker.tasks.image.Dockerfile>("createDockerfile") {
    from("eclipse-temurin:17-alpine")
    copyFile("libs/newrelic-agent.jar", "/app/newrelic-agent.jar")
    copyFile("libs/${project.name}-${project.version}.jar", "/app/${project.name}.jar")
    exposePort(8081)
    entryPoint("java")
    defaultCommand("-Duser.timezone=UTC", "-Dnewrelic.config.distributed_tracing.enabled=true", "-Dnewrelic.config.span_events=true",
            "-Dnewrelic.environment=\${SPRING_PROFILES_ACTIVE}", "-XX:-OmitStackTraceInFastThrow", "-javaagent:/app/newrelic-agent.jar", "-jar", "/app/oasis-timeline-autobot.jar")
}

tasks.register<com.bmuschko.gradle.docker.tasks.image.DockerBuildImage>("buildDockerImage") {
    dockerFile.set(file("${project.buildDir}/docker/Dockerfile"))
    images.add("${project.name}:latest")
    inputDir.set(file(project.buildDir))
    platform.set("linux/amd64")
}

tasks.register<Test>("intTest") {
    description = "Run integration tests (located in src/intTest/...)."
    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    reports.html.outputLocation.set(layout.buildDirectory.dir("reports/tests/integration"))
    reports.junitXml.outputLocation.set(layout.buildDirectory.dir("reports/tests/integration"))
    shouldRunAfter("test")
    doFirst {
        environment(
            mapOf(
                "INSERT_API_KEY" to "",
                "METRICS_API_URI" to "http://localhost",
                "NEW_RELIC_APP_NAME" to "oasis-timeline-autobot",
                "SPRING_ACTUATOR_USERNAME" to "user",
                "SPRING_ACTUATOR_PASSWORD" to "password",
                "SPRING_PROFILES_ACTIVE" to "test",
                "TWITTER_BASE_REST_URL" to "http://localhost:9093/",
                "TWITTER_OAUTH_CONSUMER_KEY" to "",
                "TWITTER_OAUTH_CONSUMER_SECRET" to "",
                "TWITTER_OAUTH_ACCESS_TOKEN" to "",
                "TWITTER_OAUTH_ACCESS_TOKEN_SECRET" to ""
            )
        )

        jvmArgs = listOf("-Duser.timezone=UTC")
// Uncomment to enable remote debugging from an IDE
//         "-Xdebug",
//         "-Xrunjdwp:server=y,transport=dt_socket,address=${(project.property("port").toInt() + 1},suspend=y")

    }
    useJUnitPlatform()
}

tasks.register("markDeploy") {
    doLast {
        if(project.hasProperty("newRelicRestApiKey") && project.hasProperty("newRelicApplicationId")) {
            // Create deployment marker payload
            val json = JsonOutput.toJson(
                mapOf("deployment" to
                    mapOf(
                        "revision" to "git rev-parse HEAD".runCommand().trim(),
                        "changelog" to "git log -1 --pretty=%B".runCommand().trim(),
                        "description" to "Deployment of ${project.name}",
                        "user" to System.getProperty("user.name")
                    )
                )
            )

            // Send request
            val client = OkHttpClient()
            val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("https://api.newrelic.com/v2/applications/${project.property("newRelicApplicationId")}/deployments.json")
                .header("X-Api-Key", project.property("newRelicRestApiKey").toString())
                .post(body)
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    project.logger.error("Unable to mark deployment: $response")
                } else {
                    project.logger.info("Deployment marker response: ${response.body!!.string()}")
                }
            }
        } else {
            project.logger.lifecycle("Skipping recording of deployment:  \"newRelicRestApiKey\" " +
                    "and \"newRelicApplicationId\" properties must both be set.")
        }
    }
}

tasks.register("formatSource") {
    doLast {
        val formatter = Formatter(JavaFormatterOptions.builder()
            .style(JavaFormatterOptions.Style.GOOGLE)
            .build())

        project.sourceSets.forEach { sourceSet ->
            sourceSet.allJava.srcDirs.forEach { javaSourceDir ->
                if (Files.exists(javaSourceDir.toPath())) {
                    javaSourceDir.walkTopDown().forEach { file ->
                        if (file.toString().endsWith(".java")) {
                            val input = file.readText(Charsets.UTF_8)
                            val output = formatter.formatSourceAndFixImports(input)
                            if (output != input) {
                                val outputSink = GoogleFiles.asByteSink(file)
                                    .asCharSink(Charset.defaultCharset())
                                outputSink.write(output)
                                project.logger.lifecycle("Re-formatted ${file}.")
                            }
                        }
                    }
                }
            }
        }
    }
}

tasks.register("validateYaml") {
    doLast {
        val input = File(project.projectDir, "src/main/resources/application.yml")
        Yaml().loadAll(input.inputStream()).forEach { configFile ->
            project.logger.debug(
                "Section '${configFile}' in configuration file '${input.name}' is valid.")
        }
        project.logger.lifecycle("File '${input.name}' passed validation.")
    }
}

// Test Configuration
tasks {
    clean {
        doLast {
            delete("src/main/resources/json")
        }
    }

    jacocoTestReport {
        reports {
            html.required.set(true)
            xml.required.set(true)
            csv.required.set(false)
        }

        classDirectories.setFrom(
            files(
                classDirectories.files.stream().map {
                    fileTree(
                        mapOf(
                            "dir" to it,
                            "excludes" to
                                listOf(
                                    "com/jdpgrailsdev/oasis/timeline/config/**",
                                    "com/jdpgrailsdev/oasis/timeline/Application*"
                                )
                        )
                    )
                }.collect(Collectors.toList())
            )
        )
    }

    jacocoTestCoverageVerification {
        violationRules {
            rule {
                element = "CLASS"
                excludes = listOf(
                    "**/*Test*", "**/*Spec*", "**/*$$*", "**closure*",
                    "**Application*", "**Configuration*", "**TweetContext*"
                )
                limit {
                    counter = "BRANCH"
                    minimum = "0.8".toBigDecimal()
                }
                limit {
                    counter = "INSTRUCTION"
                    minimum = "0.8".toBigDecimal()
                }
            }
        }
    }

    spotbugsMain {
        reports.maybeCreate("xml").isEnabled = false
        reports.maybeCreate("html").isEnabled = true
    }

    test {
        useJUnitPlatform()
        doFirst {
            jvmArgs = listOf("-Duser.timezone=UTC")
        }
        jacoco {
            enabled = true
        }
    }
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask>() {
    reports.maybeCreate("xml").isEnabled = true
    reports.maybeCreate("html").isEnabled = true
}

tasks.withType<ca.cutterslade.gradle.analyze.AnalyzeDependenciesTask>() {
    warnUsedUndeclared = true
    warnUnusedDeclared = true
}

// Task Dependencies
tasks.named("buildDockerImage") {
    dependsOn(":${project.name}:createDockerfile")
}
tasks.named("check") {
    dependsOn(listOf(
        ":${project.name}:intTest",
        ":${project.name}:formatSource",
        ":${project.name}:jacocoTestCoverageVerification"
        )
    )
}
tasks.named("checkstyleMain") {
    dependsOn(":${project.name}:formatSource")
}
tasks.named("copyDataFile") {
    outputs.upToDateWhen { false }
}
tasks.named("createDockerfile") {
    dependsOn(listOf(":${project.name}:copyAgent", ":${project.name}:bootJar"))
}
tasks.named("deployHeroku") {
    dependsOn(listOf(":${project.name}:copyAgent", ":${project.name}:bootJar"))
}
tasks.named("intTest") {
    dependsOn(":${project.name}:validateYaml")

    /*
     * Set the integration tests to run during the check task
     * immediately after the unit tests have run.
     */
    mustRunAfter(":${project.name}:test")
}
tasks.named("jacocoTestReport") {
    outputs.upToDateWhen { false }
}
tasks.named("processResources") {
    dependsOn(":${project.name}:copyDataFile")
}
tasks.named("publish") {
    dependsOn(listOf(":${project.name}:build", ":${project.name}:deployHeroku"))
    finalizedBy(":${project.name}:markDeploy")
}
tasks.named("spotbugsTest") {
    enabled = false
}
tasks.named("spotbugsIntTest") {
    enabled = false
}
tasks.named("test") {
    finalizedBy(":${project.name}:jacocoTestReport")
    outputs.upToDateWhen { false }
}
tasks.named("deployHeroku") {
    dependsOn(":${project.name}:bootJar", ":${project.name}:jar")
}
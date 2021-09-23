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
import okhttp3.RequestBody

val javaVersion = JavaVersion.VERSION_11

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
            "com.google.googlejavaformat:google-java-format:${project.property("google-java-format.version")}"
        ).forEach { classpath(it) }
    }
}

plugins {
    id("java")
    id("com.heroku.sdk.heroku-gradle") version "2.0.0"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("org.springframework.boot") version "2.5.5"
    id("com.gorylenko.gradle-git-properties") version "2.2.2"
    id("checkstyle")
    id("pmd")
    id("com.github.spotbugs") version "4.7.3"
    id("jacoco")
}

configure<JavaPluginConvention> {
    sourceCompatibility = javaVersion
}

val agent by configurations.creating

sourceSets {
    create("intTest") {
        java.srcDir(File("src/intTest/java"))
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val intTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

configurations["intTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

// Static Analysis Plugin Configuration

checkstyle {
    setConfigFile(project.file("config/checkstyle/checkstyle.xml"))
    setToolVersion(project.property("checkstyle.version").toString())
}

jacoco {
    setToolVersion(project.property("jacoco.version").toString())
}

pmd {
    setConsoleOutput(true)
    setIgnoreFailures(false)
    setRuleSets(listOf())
    setRuleSetFiles(files("config/pmd/pmd.xml"))
    setToolVersion(project.property("pmd.version").toString())
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
        "com.google.guava:guava:${project.property("guava.version")}",
        "com.newrelic.agent.java:newrelic-api:${project.property("newrelic.version")}",
        "com.newrelic.telemetry:micrometer-registry-new-relic:${project.property("micrometer-registry-new-relic.version")}",
        "io.micrometer:micrometer-core",
        "org.springframework:spring-context",
        "org.springframework.boot:spring-boot",
        "org.springframework.boot:spring-boot-autoconfigure",
        "org.springframework.boot:spring-boot-starter-actuator",
        "org.springframework.boot:spring-boot-starter-security",
        "org.springframework.boot:spring-boot-starter-thymeleaf",
        "org.springframework.boot:spring-boot-starter-web",
        "io.projectreactor:reactor-core",
        "org.twitter4j:twitter4j-core:${project.property("twitter4j-core.version")}",
        "org.slf4j:slf4j-api"
    ).forEach { implementation(it) }

    listOf(
        "com.fasterxml.jackson.datatype:jackson-datatype-jsr310"
    ).forEach { runtimeOnly(it) }

    listOf(
        "org.junit:junit-bom"
    ).forEach {
        testImplementation(platform(it))
    }

    listOf(
        "commons-io:commons-io:${project.property("commons-io.version")}",
        "org.springframework.boot:spring-boot-starter-test",
        "org.junit.jupiter:junit-jupiter",
        "org.junit.jupiter:junit-jupiter-api",
        "org.junit.jupiter:junit-jupiter-params",
        "org.mockito:mockito-core:${project.property("mockito-core.version")}",

    ).forEach {
        testImplementation(it)
    }

    listOf(
        "org.junit.jupiter:junit-jupiter-engine",
        "org.junit.platform:junit-platform-runner"
    ).forEach {
        testRuntimeOnly(it)
    }

    listOf(
        "org.spockframework:spock-spring:${project.property("spock.version")}"
    ).forEach { intTestImplementation(it) }
}

// Deployment Configuration

heroku {
    setAppName(project.name)
    setJdkVersion(javaVersion.getMajorVersion())
    setIncludeBuildDir(false)
    setIncludes(
        listOf(
            "autobot/build/libs/${project.name}-${project.version}.jar".toString(),
            "autobot/build/libs/newrelic-agent.jar"
        )
    )
    setProcessTypes(
        mapOf(
            "web" to listOf("java", "-Dserver.port=\$PORT",
                "-Duser.timezone=UTC",
                "-Dnewrelic.config.distributed_tracing.enabled=true",
                "-Dnewrelic.config.span_events=true",
                "-Dnewrelic.environment=production",
                "-XX:-OmitStackTraceInFastThrow",
                "-javaagent:autobot/build/libs/newrelic-agent.jar",
                "-jar",
                "autobot/build/libs/${project.name}-${project.version}.jar".toString()
            ).joinToString(" ")
        )
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
    filter({ line ->
        line.replace("<i>", "'").replace("</i>", "'").replace("<[^>]*>", "")
    })
}

tasks.register("downloadAgent") {
    doLast {
        agent.resolvedConfiguration.getFirstLevelModuleDependencies()
            .forEach { module ->
                module.getModuleArtifacts().forEach { artifact ->
                    project.copy {
                        from(artifact.file)
                        into("${project.buildDir}/libs")
                        rename("newrelic-agent-.*\\.jar", "newrelic-agent.jar")
                    }
                }
            }
    }
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
                "TWITTER_OAUTH_CONSUMER_KEY" to "",
                "TWITTER_OAUTH_CONSUMER_SECRET" to "",
                "TWITTER_OAUTH_ACCESS_TOKEN" to "",
                "TWITTER_OAUTH_ACCESS_TOKEN_SECRET" to ""
            )
        )

        setJvmArgs(listOf("-Duser.timezone=UTC")
// Uncomment to enable remote debugging from an IDE
//         "-Xdebug",
//         "-Xrunjdwp:server=y,transport=dt_socket,address=${(project.property("port").toInt() + 1},suspend=y",
        )

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
            val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)
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
            sourceSet.getAllJava().getSrcDirs().forEach { javaSourceDir ->
                if (Files.exists(javaSourceDir.toPath())) {
                    javaSourceDir.walkTopDown().forEach { file ->
                        if (file.toString().endsWith(".java")) {
                            val input = file.readText(Charsets.UTF_8)
                            val output = formatter.formatSourceAndFixImports(input)
                            if (output != input) {
                                val outputSink = GoogleFiles.asByteSink(file)
                                    .asCharSink(Charset.defaultCharset())
                                outputSink.write(output)
                                project.getLogger().lifecycle("Re-formatted ${file}.")
                            }
                        }
                    }
                }
            }
        }
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
                    minimum = BigDecimal(0.8)
                }
                limit {
                    counter = "INSTRUCTION"
                    minimum = BigDecimal(0.8)
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

// Task Dependencies
tasks.named("assemble") {
    dependsOn(":${project.name}:downloadAgent")
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
tasks.named("intTest") {
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
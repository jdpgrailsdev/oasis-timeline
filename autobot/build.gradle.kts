import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import groovy.json.JsonOutput
import java.util.stream.Collectors
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.yaml.snakeyaml.Yaml

val javaVersion = JavaVersion.VERSION_21

fun String.runCommand(currentWorkingDir: File = file("./")): String {
  val process =
    ProcessBuilder(this.split("\\s".toRegex()))
      .directory(currentWorkingDir)
      .redirectOutput(ProcessBuilder.Redirect.PIPE)
      .redirectError(ProcessBuilder.Redirect.PIPE)
      .start()
  process.waitFor()
  return process.inputStream.bufferedReader(Charsets.UTF_8).readText()
}

buildscript {
  dependencies {
    listOf(
        libs.okhttp3,
        libs.guava,
        libs.google.java.format,
        libs.snakeyaml,
        libs.spotbugs.annotations,
        libs.commons.io,
        libs.jgit,
        libs.commons.compress,
      )
      .forEach { classpath(it) }
  }
}

plugins {
  id("java")
  alias(libs.plugins.heroku.gradle)
  alias(libs.plugins.spring.dependency.management)
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.gradle.git.properties)
  id("checkstyle")
  //    id("pmd")
  alias(libs.plugins.spotbugs.gradle)
  id("jacoco")
  alias(libs.plugins.versions.gradle)
  //    alias(libs.plugins.ca.cutterslade.analyze)
  alias(libs.plugins.docker.gradle)
  alias(libs.plugins.kotlin.gradle)
  alias(libs.plugins.kotlin.plugin.spring)
  alias(libs.plugins.spotless)
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

val intTestImplementation: Configuration by
  configurations.getting { extendsFrom(configurations.testImplementation.get()) }

configurations["intTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

configurations.checkstyle {
  resolutionStrategy.capabilitiesResolution.withCapability(
    "com.google.collections:google-collections"
  ) {
    select("com.google.guava:guava:0")
  }
}

// Static Analysis Plugin Configuration

checkstyle {
  configFile = project.file("config/checkstyle/checkstyle.xml")
  toolVersion = libs.versions.checkstyle.get()
}

jacoco { toolVersion = libs.versions.jacoco.get() }

// pmd {
//    isConsoleOutput = true
//    isIgnoreFailures = false
//    ruleSets = listOf()
//    ruleSetFiles = files("config/pmd/pmd.xml")
//    toolVersion = libs.versions.pmd.get()
// }

spotbugs {
  ignoreFailures.set(false)
  effort.set(Effort.MAX)
  reportLevel.set(Confidence.LOW)
  showProgress.set(false)
  toolVersion.set(libs.versions.spotbugs.get())
}

spotless {
  java {
    target("**/*.java")
    importOrder()
    //        licenseHeaderFile("${project.rootProject.projectDir}/LICENSE_HEADER",
    // "").updateYearWithLatest(true)
    googleJavaFormat(libs.versions.google.java.format.get())
    trimTrailingWhitespace()
  }
  kotlin {
    target("**/*.kt")
    ktfmt().googleStyle()
    ktlint(libs.versions.ktlint.get())
      .editorConfigOverride(
        mapOf(
          "ktlint_code_style" to "ktlint_official",
          "indent_size" to 2,
          "max_line_length" to 150,
          "ij_kotlin_packages_to_use_import_on_demand" to "unset",
        )
      )
    //        licenseHeaderFile("${project.rootProject.projectDir}/LICENSE_HEADER",
    // "").updateYearWithLatest(true)
  }
  kotlinGradle {
    target("**/*.gradle.kts")
    ktfmt().googleStyle()
  }
}

// Project Dependencies
dependencies {
  listOf(libs.newrelic.agent).forEach { agent(it) }

  listOf(libs.spotbugs).forEach { spotbugs(it) }

  listOf(
      libs.spotbugs.annotations,
      "com.fasterxml.jackson.core:jackson-annotations",
      "com.fasterxml.jackson.core:jackson-core",
      "com.fasterxml.jackson.core:jackson-databind",
      "com.fasterxml.jackson.module:jackson-module-kotlin",
      libs.jackson.datatype.jsr310,
      libs.guava,
      libs.newrelic.api,
      libs.micrometer.registry.new.relic,
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
      "org.springframework.boot:spring-boot-starter-cache",
      "org.springframework.boot:spring-boot-starter-security",
      "org.springframework.boot:spring-boot-starter-thymeleaf",
      "org.springframework.boot:spring-boot-starter-web",
      "org.springframework.security:spring-security-config",
      "org.thymeleaf:thymeleaf",
      "org.thymeleaf:thymeleaf-spring6",
      libs.scribe.java,
      libs.twitter.api.java.sdk,
      libs.bundles.redis,
      libs.okhttp3,
      libs.kotlin.logging,
      "org.jetbrains.kotlin:kotlin-reflect",
      libs.caffeine,
      libs.commons.beanutils,
    )
    .forEach { implementation(it) }

  implementation(libs.java.faker) { exclude(group = "org.yaml") }

  listOf("org.springframework.boot:spring-boot-properties-migrator").forEach { runtimeOnly(it) }

  //    listOf(
  //        "org.springframework.boot:spring-boot-starter-actuator",
  //        "org.springframework.boot:spring-boot-starter-security",
  //        "org.springframework.boot:spring-boot-starter-thymeleaf",
  //        "org.springframework.boot:spring-boot-starter-web"
  //    ).forEach { permitUnusedDeclared(it) }

  listOf(libs.junit.bom).forEach { testImplementation(platform(it)) }

  listOf(
      libs.commons.io,
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
      libs.spring.cloud.starter.contract.stub.runner,
      libs.mockito.core,
      libs.mockk,
    )
    .forEach { testImplementation(it) }

  listOf("org.junit.jupiter:junit-jupiter-engine", "org.junit.platform:junit-platform-runner")
    .forEach { testRuntimeOnly(it) }

  listOf(libs.jedis.mock, libs.spring.security.test).forEach { intTestImplementation(it) }
  testImplementation(kotlin("test"))
}

// Deployment Configuration

heroku {
  appName = project.name
  jdkVersion = javaVersion.majorVersion
  isIncludeBuildDir = false
  includes =
    listOf(
      "autobot/build/libs/${project.name}-${project.version}.jar",
      "autobot/build/libs/newrelic-agent.jar",
    )
  processTypes =
    mapOf(
      "web" to
        listOf(
            "java",
            "-Dserver.port=\$PORT",
            "-Duser.timezone=UTC",
            "-Dnewrelic.config.distributed_tracing.enabled=true",
            "-Dnewrelic.config.span_events=true",
            "-Dnewrelic.environment=production",
            "-XX:-OmitStackTraceInFastThrow",
            "-javaagent:autobot/build/libs/newrelic-agent.jar",
            "-jar",
            "autobot/build/libs/${project.name}-${project.version}.jar",
          )
          .joinToString(" ")
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
      "TWITTER_OAUTH2_CLIENT_ID" to "",
      "TWITTER_OAUTH2_CLIENT_SECRET" to "",
      "TWITTER_OAUTH2_ACCESS_TOKEN" to "",
      "TWITTER_OAUTH2_REFRESH_TOKEN" to "",
      "BLUESKY_URL" to "http://localhost",
      "BLUESKY_HANDLE" to "user",
      "BLUESKY_PASSWORD" to "password",
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
  filter { line -> line.replace("<i>", "'").replace("</i>", "'").replace("<[^>]*>".toRegex(), "") }
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
  defaultCommand(
    "-Duser.timezone=UTC",
    "-Dnewrelic.config.distributed_tracing.enabled=true",
    "-Dnewrelic.config.span_events=true",
    "-Dnewrelic.environment=\${SPRING_PROFILES_ACTIVE}",
    "-XX:-OmitStackTraceInFastThrow",
    "-javaagent:/app/newrelic-agent.jar",
    "-jar",
    "/app/oasis-timeline-autobot.jar",
  )
}

tasks.register<com.bmuschko.gradle.docker.tasks.image.DockerBuildImage>("buildDockerImage") {
  dockerFile.set(project.layout.buildDirectory.file("/docker/Dockerfile"))
  images.add("${project.name}:latest")
  inputDir.set(project.layout.buildDirectory.dir("."))
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
        "TWITTER_API_BASE_PATH" to "http://localhost:9093",
        "TWITTER_OAUTH2_CLIENT_ID" to "clientid",
        "TWITTER_OAUTH2_CLIENT_SECRET" to "secret",
        "TWITTER_OAUTH2_ACCESS_TOKEN" to "",
        "TWITTER_OAUTH2_REFRESH_TOKEN" to "",
        "BLUESKY_PUBLIC_URL" to "http://localhost:9093",
        "BLUESKY_URL" to "http://localhost:9093",
        "BLUESKY_HANDLE" to "user",
        "BLUESKY_PASSWORD" to "password",
      )
    )

    jvmArgs = listOf("-Duser.timezone=UTC")
    // Uncomment to enable remote debugging from an IDE
    //         "-Xdebug",
    //         "-Xrunjdwp:server=y,transport=dt_socket,address=${(project.property("port").toInt() +
    // 1},suspend=y")

  }
  useJUnitPlatform()
}

tasks.register("markDeploy") {
  doLast {
    if (project.hasProperty("newRelicRestApiKey") && project.hasProperty("newRelicApplicationId")) {
      // Create deployment marker payload
      val json =
        JsonOutput.toJson(
          mapOf(
            "deployment" to
              mapOf(
                "revision" to "git rev-parse HEAD".runCommand().trim(),
                "changelog" to "git log -1 --pretty=%B".runCommand().trim(),
                "description" to "Deployment of ${project.name}",
                "user" to System.getProperty("user.name"),
              )
          )
        )

      // Send request
      val client = OkHttpClient()
      val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
      val request =
        Request.Builder()
          .url(
            "https://api.newrelic.com/v2/applications/${project.property("newRelicApplicationId")}/deployments.json"
          )
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
      project.logger.lifecycle(
        "Skipping recording of deployment:  \"newRelicRestApiKey\" " +
          "and \"newRelicApplicationId\" properties must both be set."
      )
    }
  }
}

tasks.register("validateYaml") {
  doLast {
    val input = File(project.projectDir, "src/main/resources/application.yml")
    Yaml().loadAll(input.inputStream()).forEach { configFile ->
      project.logger.debug(
        "Section '{}' in configuration file '{}' is valid.",
        configFile,
        input.name,
      )
    }
    project.logger.lifecycle("File '${input.name}' passed validation.")
  }
}

// Test Configuration
tasks {
  clean { doLast { delete("src/main/resources/json") } }

  jacocoTestReport {
    reports {
      html.required.set(true)
      xml.required.set(true)
      csv.required.set(false)
    }

    classDirectories.setFrom(
      files(
        classDirectories.files
          .stream()
          .map {
            fileTree(
              mapOf(
                "dir" to it,
                "excludes" to
                  listOf(
                    "com/jdpgrailsdev/oasis/timeline/config/**",
                    "com/jdpgrailsdev/oasis/timeline/Application*",
                  ),
              )
            )
          }
          .collect(Collectors.toList())
      )
    )
  }

  jacocoTestCoverageVerification {
    violationRules {
      isFailOnViolation = false
      rule {
        element = "CLASS"
        excludes =
          listOf(
            "**/*Test*",
            "**/*Spec*",
            "**/*$$*",
            "**closure*",
            "**Application*",
            "**Configuration*",
            "**SocialContext*",
            "**PostContext*",
            "**BlueSkyContext*",
            "**TweetContext*",
            "**/*\$logger$*",
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
    reports.maybeCreate("xml").required.set(false)
    reports.maybeCreate("html").required.set(true)
  }

  test {
    useJUnitPlatform()
    doFirst { jvmArgs = listOf("-Duser.timezone=UTC") }
    jacoco { enabled = true }
  }
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
  reports.maybeCreate("xml").required.set(false)
  reports.maybeCreate("html").required.set(true)
}

// tasks.withType<ca.cutterslade.gradle.analyze.AnalyzeDependenciesTask>() {
//    warnUsedUndeclared = true
//    warnUnusedDeclared = true
// }

// Task Dependencies
tasks.named("buildDockerImage") { dependsOn(":${project.name}:createDockerfile") }

tasks.named("check") {
  dependsOn(
    listOf(
      ":${project.name}:intTest",
      ":${project.name}:spotlessApply",
      ":${project.name}:jacocoTestCoverageVerification",
    )
  )
}

tasks.named("checkstyleMain") { dependsOn(":${project.name}:spotlessApply") }

tasks.named("checkstyleTest") { dependsOn(":${project.name}:spotlessApply") }

tasks.named("copyDataFile") {
  dependsOn(":${project.name}:spotlessApply")
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

tasks.named("jacocoTestReport") { outputs.upToDateWhen { false } }

tasks.named("processResources") { dependsOn(":${project.name}:copyDataFile") }

tasks.named("publish") {
  dependsOn(listOf(":${project.name}:build", ":${project.name}:deployHeroku"))
  finalizedBy(":${project.name}:markDeploy")
}

tasks.named("spotbugsTest") { enabled = false }

tasks.named("spotbugsIntTest") { enabled = false }

tasks.named("test") {
  finalizedBy(":${project.name}:jacocoTestReport")
  outputs.upToDateWhen { false }
}

tasks.named("deployHeroku") { dependsOn(":${project.name}:bootJar", ":${project.name}:jar") }

tasks.withType<KotlinCompile> { compilerOptions { freeCompilerArgs.add("-Xjsr305=strict") } }

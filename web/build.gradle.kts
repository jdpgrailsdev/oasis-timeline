import com.github.gradle.node.npm.task.NpmTask
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")
val updatedAt: String = LocalDate.now().format(formatter)

plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.node.gradle)
    alias(libs.plugins.git.publish)
}

gitPublish {
    repoUri.set("https://github.com/jdpgrailsdev/oasis-timeline.git")
    branch.set("gh-pages")

    contents {
        from(projectDir) {
            include("CNAME")
        }
        from(project.layout.buildDirectory.dir('.')) {
            include("**.js")
            include("**.json")
            include("**.html")
            exclude("dist/*.tar")
            exclude("__test__/**")
        }
        from(project.layout.buildDirectory.dir('favicons')) {
            into("favicons")
        }
        from(project.layout.buildDirectory.dir('fonts')) {
            into("fonts")
        }
        from(project.layout.buildDirectory.dir('images')) {
            into("images")
        }
        from(project.layout.buildDirectory.dir('sources')) {
            into("sources")
        }
        from(project.layout.buildDirectory.dir('static')) {
            into("static")
        }
        from(project.layout.buildDirectory.dir('stylesheets')) {
            into("stylesheets")
        }
    }

    commitMessage.set("Updating site.")
}

node {
    version.set(libs.versions.node.get())
}

spotless {
    format("styling") {
        target("public/stylesheets/**/*.css")
        //        prettier().config(["parser": "css", "printWidth": 150, "singleQuote": true, "tabWidth": 4])
        licenseHeaderFile("${project.rootProject.projectDir}/LICENSE_HEADER", "")
    }
    typescript {
        target("src/**/*.tsx")
//        tsfmt().config(["indentSize": 4, "convertTabsToSpaces": true])
        licenseHeaderFile("${project.rootProject.projectDir}/LICENSE_HEADER", "(import|const|declare|export|var) ")
    }
}

tasks.register<NpmTask>("install") {
    npmCommand.set(listOf("install"))
}

tasks.register<NpmTask>("start") {
    npmCommand.set(listOf("run-script"))
    args.set(listOf("start"))
    environment.set(mapOf("REACT_APP_UPDATED_AT" to updatedAt))
}

tasks.register<NpmTask>("buildPackage") {
    npmCommand.set(listOf("run-script"))
    args.set(listOf("build"))
    environment.set(mapOf("REACT_APP_UPDATED_AT" to updatedAt))
}

tasks.register<NpmTask>("npmTest") {
    npmCommand.set(listOf("run-script"))
    args.set(listOf("test", "--", "--watchAll=false"))
}

tasks.register<Delete>("delete") {
    delete(project.layout.buildDirectory)
}

tasks.register<Tar>("archive") {
    archiveBaseName.set(project.name)
    destinationDirectory.set(File("${project.layout.buildDirectory}/dist"))
    from ("$project.layout.buildDirectory") {
        exclude("dist/*.tar")
        exclude("__test__/**")
    }
}

tasks.register("fixSpotless") {
    doLast {
        listOf("spotless-node-modules-prettier-format",
            "spotless-node-modules-tsfmt-format").forEach {
            val tmpDir = File(project.layout.buildDirectory.toString(), it)
            if (!tmpDir.exists()) {
                tmpDir.mkdirs()
            }
        }
    }
}

tasks.named("archive") {
    dependsOn(":${project.name}:build")
}
tasks.named("build") {
    dependsOn(listOf(":${project.name}:npmTest", ":${project.name}:buildPackage"))
    finalizedBy(":${project.name}:archive")
}
tasks.named("buildPackage") {
    dependsOn(listOf(":${project.name}:clean", ":${project.name}:install"))
}
tasks.named("check") {
    dependsOn(":${project.name}:npmTest")
}
tasks.named("clean") {
    dependsOn(":${project.name}:delete")
}
tasks.named("gitPublishCopy") {
    dependsOn(":${project.name}:archive")
    outputs.upToDateWhen { false }
}
tasks.named("gitPublishPush") {
    dependsOn(":${project.name}:archive")
    outputs.upToDateWhen { false }
}
tasks.named("publish") {
    dependsOn(listOf(":${project.name}:archive", ":${project.name}:gitPublishPush"))
}
tasks.named("spotlessCheck") {
    dependsOn(":${project.name}:fixSpotless")
}
tasks.named("npmTest") {
    dependsOn(listOf(":${project.name}:clean", ":${project.name}:install"))
}
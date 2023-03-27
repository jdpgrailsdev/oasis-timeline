import com.github.gradle.node.npm.task.NpmTask
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")
val updatedAt: String = LocalDate.now().format(formatter)

plugins {
    id("com.diffplug.spotless") version "6.17.0"
    id("com.github.node-gradle.node") version "3.5.1"
    id("org.ajoberstar.git-publish") version "4.1.1"
}

gitPublish {
    repoUri.set("https://github.com/jdpgrailsdev/oasis-timeline.git")
    branch.set("gh-pages")

    contents {
        from(projectDir) {
            include("CNAME")
        }
        from(buildDir) {
            include("**.js")
            include("**.json")
            include("**.html")
            exclude("dist/*.tar")
            exclude("__test__/**")
        }
        from("${buildDir}/favicons") {
            into("favicons")
        }
        from("${buildDir}/fonts") {
            into("fonts")
        }
        from("${buildDir}/images") {
            into("images")
        }
        from("${buildDir}/sources") {
            into("sources")
        }
        from("${buildDir}/static") {
            into("static")
        }
        from("${buildDir}/stylesheets") {
            into("stylesheets")
        }
    }

    commitMessage.set("Updating site.")
}

node {
    version.set(project.property("node.version").toString())
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
    delete(buildDir)
}

tasks.register<Tar>("archive") {
    archiveBaseName.set(project.name)
    destinationDirectory.set(File("${buildDir}/dist"))
    from ("$buildDir") {
        exclude("dist/*.tar")
        exclude("__test__/**")
    }
}

tasks.register("fixSpotless") {
    doLast {
        listOf("spotless-node-modules-prettier-format",
            "spotless-node-modules-tsfmt-format").forEach {
            val tmpDir = File(project.buildDir, it)
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
import com.moowork.gradle.node.npm.NpmTask
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")
val updatedAt: String = LocalDate.now().format(formatter)

plugins {
    id("com.diffplug.spotless") version "5.12.2"
    id("com.github.node-gradle.node") version "2.2.4"
    id("org.ajoberstar.git-publish") version "3.0.0"
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
    version = project.property("node.version").toString()
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
    setNpmCommand("install")
}

tasks.register<NpmTask>("start") {
    setNpmCommand("run-script")
    setArgs(listOf("start"))
    setEnvironment(mapOf("REACT_APP_UPDATED_AT" to updatedAt))
}

tasks.register<NpmTask>("buildPackage") {
    setNpmCommand("run-script")
    setArgs(listOf("build"))
    setEnvironment(mapOf("REACT_APP_UPDATED_AT" to updatedAt))
}

tasks.register<NpmTask>("npmTest") {
    setNpmCommand("run-script")
    setArgs(listOf("test", "--", "--watchAll=false"))
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
tasks.named("gitPublishPush") {
    outputs.upToDateWhen { false }
}
tasks.named("publish") {
    dependsOn(listOf(":${project.name}:build", ":${project.name}:gitPublishPush"))
}
tasks.named("spotlessCheck") {
    dependsOn(":${project.name}:fixSpotless")
}
tasks.named("npmTest") {
    dependsOn(listOf(":${project.name}:clean", ":${project.name}:install"))
}
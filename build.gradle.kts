import java.io.ByteArrayOutputStream

allprojects {
    buildscript {
        repositories {
            mavenCentral()
        }
    }

    tasks.register("publish") {}

    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks.register<Exec>("buildImage") {
        // Docker build command
        commandLine = listOf("docker", "build", "-t", project.name, ".")

        //store the output instead of printing to the console
        standardOutput = ByteArrayOutputStream()

        // Set working directory to project in order to find Dockerfile
        workingDir = project.projectDir
    }

    tasks.named("buildImage") {
        dependsOn(":${project.name}:build")
    }
}
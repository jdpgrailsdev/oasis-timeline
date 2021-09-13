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
        setCommandLine(listOf("docker", "build", "-t", project.name, "."))

        //store the output instead of printing to the console
        setStandardOutput(ByteArrayOutputStream())

        // Set working directory to project in order to find Dockerfile
        setWorkingDir(project.projectDir)
    }

    tasks.named("buildImage") {
        dependsOn(":${project.name}:build")
    }
}
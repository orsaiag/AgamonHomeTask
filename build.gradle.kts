plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("io.github.classgraph:classgraph:4.8.158")
    implementation("javax.ws.rs:javax.ws.rs-api:2.1.1")
    implementation("org.jsoup:jsoup:1.15.3")

    // Dropwizard
    implementation("io.dropwizard:dropwizard-core:2.1.5")
    implementation("io.dropwizard:dropwizard-jetty:2.1.0")
    implementation("io.dropwizard:dropwizard-configuration:2.1.0")

    // Jackson Dependencies
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.3")

    // Testing
    testImplementation("io.dropwizard:dropwizard-testing:2.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

application {
    mainClass.set("MainKt")
}

tasks.create<Jar>("customJar") {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    mergeServiceFiles()
}

tasks {
    shadowJar {
        archiveBaseName.set("AgamonHomeTask")
        archiveVersion.set("1.0-SNAPSHOT")
        archiveClassifier.set("all")
    }
    test {
        useJUnitPlatform()
    }
}
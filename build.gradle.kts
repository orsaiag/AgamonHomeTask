plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    id("nu.studer.jooq") version "7.2"
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

    // Dropwizard (Make sure it's using a compatible Jackson version)
    implementation("io.dropwizard:dropwizard-core:2.1.5")
    implementation("com.fasterxml.jackson.module:jackson-module-blackbird:2.15.3")
    implementation("io.dropwizard:dropwizard-jetty:2.1.0")
    implementation("io.dropwizard:dropwizard-configuration:2.1.0")

    // Jackson Dependencies (Explicitly Define Versions)
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.3")

   // Database & jOOQ
    implementation("org.jooq:jooq:3.18.6")
    jooqGenerator("org.postgresql:postgresql:42.6.0")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Networking (OkHttp)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Testing
    testImplementation("io.dropwizard:dropwizard-testing:2.1.0")
}

application {
    mainClass.set("MainKt")
}

tasks.named("build") {
    dependsOn("generateJooq")
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

sourceSets {
    main {
        java {
            srcDir("build/generated-jooq")
        }
        withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
            kotlin.srcDirs("src/main/kotlin")
        }
    }
}

tasks {
    shadowJar {
        archiveBaseName.set("AgamonHomeTask")
        archiveVersion.set("1.0-SNAPSHOT")
        archiveClassifier.set("all")
    }
}
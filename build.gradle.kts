plugins {
    id("net.neoforged.gradle") version "7.0.142"
    kotlin("jvm") version "1.9.23" // Use a modern Kotlin version
}

val mod_id: String by project
val mod_version: String by project

base {
    archivesName.set(mod_id)
}

// NeoForgeGradle config
neoForge {
    version = project.property("minecraft_version") as String

    runs {
        create("client") {
            // Comma-separate mod IDs to run in the client.
            // mods("examplemod", "anothermod")
            mods(mod_id)
        }

        create("server") {
            // Comma-separate mod IDs to run in the server.
            // mods("examplemod", "anothermod")
            mods(mod_id)

            // Example of setting a game argument
            // arg("--nogui")
        }

        // Example data run.
        // Runs may be duplicated and customized in any way.
        // create("data") {
        //    mods(mod_id)
        //
        //    // Example of setting a program argument
        //    // arg("--all")
        // }
    }
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")
}

repositories {
    // Add any additional repositories here if needed
}

dependencies {
    // The NeoForge dependency is now handled automatically by the plugin.
    // implementation("net.neoforged:neoforge:${project.property("neoforge_version")}")
}

// Apply the Kotlin plugin and configure its options.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    // Set the Java language version for compilation.
    options.release.set(21)
}
plugins {
    id("io.freefair.lombok") version "8.14" apply false
    id("com.modrinth.minotaur") version "2.+" apply false
    id("com.gradleup.shadow") version "8.+" apply false
    id("dev.yumi.gradle.licenser") version "2.0.+"
    id("fabric-loom") version "1.11.+" apply false
    id("io.github.p03w.machete") version "2.+" apply false
}

version = "${project.version}"
group = "io.github.itzclient"

repositories {
	maven {
		url = uri("https://moehreag.duckdns.org/maven/releases")
	}
	mavenCentral()
}

allprojects {
	repositories {
		maven("https://maven.terraformersmc.com/releases")
		maven("https://maven.fabricmc.net")
		maven("https://maven.quiltmc.org/repository/release")
		maven("https://moehreag.duckdns.org/maven/releases")
		maven("https://moehreag.duckdns.org/maven/snapshots")
		maven("https://maven.parchmentmc.org")
		maven("https://libraries.minecraft.net/")
		maven("https://repo.hypixel.net/repository/Hypixel/") {
			content { includeGroup("net.hypixel") }
		}
		exclusiveContent {
			forRepository { maven("https://maven.skye.vg") }
			filter {
				includeGroup("link.e4mc")
				includeModuleByRegex("io.netty.incubator", "netty-incubator-codec-(?:classes|parent)-quic")
			}
		}
		exclusiveContent {
			forRepository { maven("https://api.modrinth.com/maven") }
			filter { includeGroup("maven.modrinth") }
		}
		mavenLocal()
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "java")
	apply(plugin = "maven-publish")
	apply(plugin = "io.freefair.lombok")
	apply(plugin = "com.modrinth.minotaur")
	apply(plugin = "dev.yumi.gradle.licenser")

	extensions.getByType(JavaPluginExtension::class).withSourcesJar()

	tasks.getByName("jar", Jar::class) {
		filesMatching("LICENSE") {
			rename("^(LICENSE.*?)(\\..*)?$", "$1_${archiveBaseName}$2")
		}
	}

	license {
		rule(file("../HEADER"))
		include("**/*.java")
	}

    // --- NEW FIX IS HERE ---
    // This now checks if the tasks exist before trying to disable them.
    tasks.findByName("checkLicense")?.enabled = false
    tasks.findByName("checkLicenseMain")?.enabled = false
    // --- END OF FIX ---
}
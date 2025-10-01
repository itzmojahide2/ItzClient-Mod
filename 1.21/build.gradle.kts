plugins {
	id("fabric-loom")
	id("io.github.p03w.machete")
}

group = project.property("maven_group") as String
version = "${project.property("version")}+${project.property("minecraft_121")}"
base.archivesName.set(project.property("archives_base_name") as String)

loom {
	accessWidenerPath.set(file("src/main/resources/itzclient.accesswidener"))
	mods {
		create("itzclient") {
			sourceSet("main")
		}
		create("itzclient-test") {
			sourceSet("test")
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${project.property("minecraft_121")}")
	mappings("org.quiltmc:quilt-mappings:${project.property("mappings_121")}:intermediary-v2")

	modImplementation("net.fabricmc:fabric-loader:${project.property("fabric_loader")}")

	modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fapi_121")}+${project.property("minecraft_121")}")

	modImplementation("io.github.axolotlclient:AxolotlClient-config:${project.property("config")}+${project.property("minecraft_121")}") {
		exclude(group = "com.terraformersmc")
		exclude(group = "org.lwjgl")
	}
	include("io.github.axolotlclient:AxolotlClient-config:${project.property("config")}+${project.property("minecraft_121")}")
	modImplementation("io.github.axolotlclient.AxolotlClient-config:AxolotlClientConfig-common:${project.property("config")}")

	modCompileOnlyApi("com.terraformersmc:modmenu:8.0.0") {
		exclude(group = "net.fabricmc")
	}

	// This line includes the 'common' project we built in the first script
	implementation(include(project(path = ":common", configuration = "shadow"))!!)

	api("org.lwjgl:lwjgl-nanovg:3.3.3")
	runtimeOnly("org.lwjgl:lwjgl-nanovg:3.3.3:natives-linux")
	runtimeOnly("org.lwjgl:lwjgl-nanovg:3.3.3:natives-windows")
	runtimeOnly("org.lwjgl:lwjgl-nanovg:3.3.3:natives-macos")

	modCompileOnly("maven.modrinth:world-host:0.5.0+1.21.1-fabric")
	modCompileOnly("link.e4mc:e4mc_minecraft-fabric:5.3.1")

	implementation("net.hypixel:mod-api:1.0.1")
	include(modImplementation("maven.modrinth:hypixel-mod-api:1.0.1+build.1+mc1.21")!!)
}

tasks.processResources {
	inputs.property("version", project.version)
	filesMatching("fabric.mod.json") {
		expand("version" to project.version)
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.release.set(21)
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

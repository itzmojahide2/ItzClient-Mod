plugins {
	id("java")
	id("com.gradleup.shadow")
}

group = project.property("maven_group").toString() + "." + project.property("archives_base_name").toString()
base.archivesName.set(project.property("archives_base_name").toString() + "-common")

dependencies {
	compileOnly("net.fabricmc:fabric-loader:${project.property("fabric_loader")}")
	testCompileOnly("net.fabricmc:fabric-loader:${project.property("fabric_loader")}")
	compileOnly("net.fabricmc:sponge-mixin:0.16.1+mixin.0.8.7")
	compileOnly("org.jetbrains:annotations:24.0.0")

	// --- THIS IS THE CORRECTED SYNTAX ---
	// We use `implementation` for dependencies that will be bundled.
	// The `shadow` plugin, applied at the top, knows to handle these automatically.
	implementation("io.github.CDAGaming:DiscordIPC:0.10.2") {
		isTransitive = false
	}
	implementation("com.kohlschutter.junixsocket:junixsocket-common:2.10.1")
	implementation("com.kohlschutter.junixsocket:junixsocket-native-common:2.10.1")
	implementation("com.github.mizosoft.methanol:methanol:1.8.0")
	implementation("io.nayuki:qrcodegen:1.8.0")
	// --- END OF FIX ---

	compileOnly("net.hypixel:mod-api:1.0.1")
	compileOnly("com.mojang:brigadier:1.0.18")
}

tasks.jar {
	enabled = false
}

tasks.build {
	dependsOn(tasks.shadowJar)
}

tasks.processResources {
	inputs.property("version", project.version)
	filesMatching("fabric.mod.json") {
		expand("version" to project.version)
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.release.set(17)
}

tasks.shadowJar {
	archiveClassifier.set("")
	mergeServiceFiles()
	// The 'minimize' block is no longer needed with this setup, but keep the relocations.
	
	relocate("com.jagrosh", "io.github.itzclient.shadow.jagrosh")
	relocate("com.github.mizosoft", "io.github.itzclient.shadow.mizosoft")
	relocate("io.nayuki", "io.github.itzclient.shadow.nayuki")

	append("../LICENSE")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}
import de.undercouch.gradle.tasks.download.Download

plugins {
	java
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.dm)
	alias(libs.plugins.graalvm)
	alias(libs.plugins.undercouch.download)
}

group = "dev.quochung2003"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring dependencies bundle, see section "bundles" inside "gradle/libs.versions.toml" to check what dependency belongs to this type.
	implementation(libs.bundles.spring)
	developmentOnly(libs.spring.devtools)

	// Runtime dependencies bundle, see section "bundles" inside "gradle/libs.versions.toml" to check what dependency belongs to this type.
	runtimeOnly(libs.bundles.runtime)

	// Liquibase
	implementation(libs.liquibase)

	// Lombok
	compileOnly(libs.lombok)
	annotationProcessor(libs.lombok)

	// MapStruct
	implementation(libs.mapstruct.core)
	annotationProcessor(libs.mapstruct.processor)

	// Swagger (WebFlux)
	implementation(libs.swagger)

	// Test dependencies bundle, see section "bundles" inside "gradle/libs.versions.toml" to check what dependency belongs to this type.
	testImplementation(libs.bundles.test)
	testRuntimeOnly(libs.junit)
}

tasks {
	withType<Test> {
		useJUnitPlatform()
	}

	compileJava {
		options.compilerArgs.addAll(
			listOf(
				"-Amapstruct.suppressGeneratorTimestamp=true",
				"-Amapstruct.suppressGeneratorVersionInfoComment=true",
				"-Amapstruct.verbose=true"
			)
		)
	}

	register<Download>("downloadNewrelic") {
		val newrelicDir = file("newrelic")
		newrelicDir.mkdirs()
		src("https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip")
		dest(newrelicDir)
	}

	register<Copy>("unzipNewrelic") {
		val newrelicZip = file("newrelic/newrelic-java.zip")
		from(zipTree(newrelicZip))
		into(rootDir)

		// Must have this line to avoid Gradle error.
		doNotTrackState("Unreadable file")
	}
}

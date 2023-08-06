import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.8.21"
	kotlin("kapt") version "1.8.21"
	kotlin("plugin.spring") version "1.8.21" apply false
	kotlin("plugin.jpa") version "1.8.21" apply false

	id("io.spring.dependency-management") version "1.1.0"
	id("org.springframework.boot") version "3.1.0"
	id("org.jmailen.kotlinter") version "3.15.0" apply false
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

allprojects {
	group = "com.github.oopgurus.refactoringproblems"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.kapt")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "org.jmailen.kotlinter")

	dependencyManagement {
		imports {
			mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.3")
		}
	}

	dependencies {
		implementation("org.springframework.boot:spring-boot-starter-data-jpa")
		implementation("org.springframework.boot:spring-boot-starter-web")
		implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
		developmentOnly("org.springframework.boot:spring-boot-devtools")
		runtimeOnly("com.h2database:h2")
		runtimeOnly("com.mysql:mysql-connector-j")
		annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
		testImplementation("io.kotest:kotest-assertions-core:5.6.2")
		testImplementation("io.kotest:kotest-property:5.6.2")
		testImplementation("io.mockk:mockk:1.13.5")
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs += "-Xjsr305=strict"
			jvmTarget = "17"
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	configurations {
		compileOnly {
			extendsFrom(configurations.annotationProcessor.get())
		}
	}
}

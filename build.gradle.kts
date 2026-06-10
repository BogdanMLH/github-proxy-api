plugins {
	kotlin("jvm") version "2.1.20"
	kotlin("plugin.spring") version "2.1.20"
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.atipera"
version = "0.0.1-SNAPSHOT"
description = "github-proxy-api"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.wiremock:wiremock-standalone:3.9.1")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
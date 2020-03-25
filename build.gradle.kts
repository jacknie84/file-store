plugins {
    kotlin("jvm").version("1.3.61")
    `java-library`
    `maven-publish`
}

group = "com.jacknie"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.jcraft:jsch:0.1.55")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("io.mockk:mockk:1.9")
}

publishing {

    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "file-delivery"
            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
    }
}
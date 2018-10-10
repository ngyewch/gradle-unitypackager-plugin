buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("ca.cutterslade.gradle:gradle-dependency-analyze:1.2.2")
    }
}

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.github.ben-manes.versions") version ("0.20.0")
}

apply {
    plugin("ca.cutterslade.analyze")
}

repositories {
    mavenLocal()
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

group = "com.github.ngyewch.gradle"
version = "0.0.1"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    compile("com.github.ngyewch.unitypackager:unity-packager:0.0.1")
    compile(gradleApi())
}

gradlePlugin {
    (plugins) {
        "unitypackager" {
            id = "com.github.ngyewch.unitypackager"
            implementationClass = "com.github.ngyewch.unity.packager.gradle.UnityPackagerPlugin"
        }
    }
}

publishing {
    publications.create("mavenJava", MavenPublication::class.java) {
        from(components.getByName("java"))
        groupId = "com.github.ngyewch.gradle"
        artifactId = "unitypackager"
        version = "0.0.1"
    }
}

tasks {
    "build" {
        dependsOn("publishToMavenLocal")
    }

    "dependencyUpdates"(com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask::class) {
        resolutionStrategy {
            componentSelection {
                all {
                    val rejected = listOf("alpha", "beta", "rc", "cr", "m")
                            .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
                            .any { it.matches(candidate.version) }
                    if (rejected) {
                        reject("Release candidate")
                    }
                }
            }
        }
    }
}

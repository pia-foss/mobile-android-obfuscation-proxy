// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.library") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.5.1" apply true
    id("org.mozilla.rust-android-gradle.rust-android") version "0.9.3" apply false
    id("maven-publish") apply true
}

allprojects {
    group = "com.kape.android"
    version = "0.0.4.4.3"
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "maven-publish")
}

subprojects {
    project.afterEvaluate {
        if (!plugins.hasPlugin("android")) {
            publishing {
                publications {
                    create<MavenPublication>("Maven") {
                        afterEvaluate {
                            artifactId = project.name
                            if (plugins.hasPlugin("java")) {
                                from(components["java"])
                            } else if (plugins.hasPlugin("android-library")) {
                                from(components["release"])
                            }
                        }
                        repositories {
                            maven {
                                url = uri("https://maven.pkg.github.com/pia-foss/mobile-android-obfuscation-proxy/")
                                credentials {
                                    username = System.getenv("GITHUB_USERNAME")
                                    password = System.getenv("GITHUB_TOKEN")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

import org.gradle.kotlin.dsl.support.listFilesOrdered

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.mozilla.rust-android-gradle.rust-android")
    id("maven-publish")
}

android {
    namespace = "com.kape.obfuscator"
    ndkVersion = sdkDirectory.resolve("ndk").listFilesOrdered().last().name

    compileSdk = 36
    defaultConfig {
        minSdk = 24
        externalNativeBuild.ndkBuild {
            abiFilters("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            arguments("-j${Runtime.getRuntime().availableProcessors()}")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

kotlin {
    jvmToolchain(17)
}

cargo {
    module = "src/main/rust/shadowsocks-rust"
    libname = "sslocal"
    targets = listOf("arm", "arm64", "x86", "x86_64")
    profile = "release"
    extraCargoBuildArguments = listOf("--bin", libname!!)
    features {
        noDefaultBut(
            arrayOf(
                "local-tunnel",
                "logging",
                "local-dns",
                "local-tun",
                "stream-cipher",
                "aead-cipher-2022",
                "aead-cipher-extra"
            )
        )
    }
    exec = { spec, toolchain ->
        run {
            try {
                Runtime.getRuntime().exec(arrayOf("python3", "-V"))
                spec.environment("RUST_ANDROID_GRADLE_PYTHON_COMMAND", "python3")
                project.logger.lifecycle("Python 3 detected.")
            } catch (e: java.io.IOException) {
                project.logger.lifecycle("No python 3 detected.")
                try {
                    Runtime.getRuntime().exec(arrayOf("python", "-V"))
                    spec.environment("RUST_ANDROID_GRADLE_PYTHON_COMMAND", "python")
                    project.logger.lifecycle("Python detected.")
                } catch (e: java.io.IOException) {
                    throw GradleException("No any python version detected. You should install the python first to compile project.")
                }
            }
            // https://developer.android.com/guide/practices/page-sizes#other-build-systems
            spec.environment("RUST_ANDROID_GRADLE_CC_LINK_ARG", "-Wl,-z,max-page-size=16384,-soname,lib$libname.so")
            spec.environment("RUST_ANDROID_GRADLE_LINKER_WRAPPER_PY", "$projectDir/$module/../linker-wrapper.py")
            spec.environment("RUST_ANDROID_GRADLE_TARGET", "target/${toolchain.target}/$profile/lib$libname.so")
        }
    }
}

tasks.whenTaskAdded {
    when (name) {
        "mergeDebugJniLibFolders", "mergeReleaseJniLibFolders" -> dependsOn("cargoBuild")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
}

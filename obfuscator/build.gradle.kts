import org.gradle.kotlin.dsl.support.listFilesOrdered

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.mozilla.rust-android-gradle.rust-android")
    id("maven-publish")
}

android {
    namespace = "com.kape.obfuscator"
    ndkVersion = sdkDirectory.resolve("ndk").listFilesOrdered().lastOrNull()?.name ?: "26.1.10909125"

    compileSdk = 34
    defaultConfig {
        minSdk = 24
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
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

cargo {
    module = "src/main/rust/shadowsocks-rust"
    libname = "sslocal"
    targets = listOf("arm", "arm64", "x86", "x86_64")
    profile = "release"
    extraCargoBuildArguments = listOf("--bin", libname!!)
    cargoCommand = "${System.getProperty("user.home")}/.cargo/bin/cargo"
    rustcCommand = "${System.getProperty("user.home")}/.cargo/bin/rustc"
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
                Runtime.getRuntime().exec("python3 -V >/dev/null 2>&1")
                spec.environment("RUST_ANDROID_GRADLE_PYTHON_COMMAND", "python3")
            } catch (e: java.io.IOException) {
                throw GradleException("Please install Python3 in order to compile.")
            }
            spec.environment("RUST_ANDROID_GRADLE_CC_LINK_ARG", "-Wl,-z,max-page-size=16384,-soname,lib$libname.so")
            spec.environment("RUST_ANDROID_GRADLE_LINKER_WRAPPER_PY", "$projectDir/$module/../linker-wrapper.py")
            spec.environment("RUST_ANDROID_GRADLE_TARGET", "target/${toolchain.target}/$profile/lib$libname.so")
        }
    }
}

tasks.whenTaskAdded {
    when (this.name) {
        // Ensure the .so files are built before native libs are merged into the AAR.
        // mergeJniLibFolders (AGP <8) and mergeNativeLibs (AGP 8+) both trigger packaging;
        // without an explicit dependency either can race ahead of cargoBuild in parallel builds.
        "mergeDebugJniLibFolders", "mergeReleaseJniLibFolders",
        "mergeDebugNativeLibs", "mergeReleaseNativeLibs",
        "javaPreCompileDebug", "javaPreCompileRelease" -> dependsOn("cargoBuild")
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}

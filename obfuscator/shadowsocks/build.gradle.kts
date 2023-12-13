import org.gradle.kotlin.dsl.support.listFilesOrdered

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.mozilla.rust-android-gradle.rust-android")
}

android {
    namespace = "com.kape.obfuscator.shadowsocks"
    ndkVersion = sdkDirectory.resolve("ndk").listFilesOrdered().last().name

    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
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
                "logging",
                "local-dns",
                "local-tun",
                "aead-cipher-2022"
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
            spec.environment("RUST_ANDROID_GRADLE_LINKER_WRAPPER_PY", "$projectDir/$module/../linker-wrapper.py")
            spec.environment("RUST_ANDROID_GRADLE_TARGET", "target/${toolchain.target}/$profile/lib$libname.so")
        }
    }
}

tasks.whenTaskAdded {
    if (this.name == "javaPreCompileDebug" || this.name == "javaPreCompileRelease") {
        this.dependsOn("cargoBuild")
    }
}

dependencies {
}


plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.sentryenterprises.sentry.sdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        externalNativeBuild {
            cmake {
                cppFlags ( "")
            }
        }

        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            consumerProguardFiles(
                "consumer-rules.pro"
            )
        }

        debug {
            isJniDebuggable = true
        }
    }
    externalNativeBuild {
        cmake {
            path = File("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    lint {
        abortOnError = true
        checkReleaseBuilds = false
        explainIssues = true
        ignoreWarnings = false
        quiet = false
    }

}

dependencies {
    implementation ("net.java.dev.jna:jna:5.13.0@aar")

    implementation (libs.core.ktx)
    implementation (libs.appcompat)

    // Tests
    testImplementation (libs.junit)
}

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(17)
    sourceSets.all {
        languageSettings {
            enableLanguageFeature("ExplicitBackingFields")
        }

    }
}

android {
    namespace = "com.secure.jnet.wallet"

    compileSdk = 34

    defaultConfig {
        minSdk = 31
        targetSdk = 34
        versionCode = 40
        versionName = "0.0.${versionCode}"
    }


    kotlinOptions {
        jvmTarget = "17"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }

    signingConfigs {
        create("release") {
            storeFile = file("../deployKey.keystore")
            storePassword = System.getenv("SENTRY_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("SENTRY_KEYSTORE_ALIAS")
            keyPassword = System.getenv("SENTRY_KEYSTORE_ALIAS_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
            ndk {
                isDebuggable = true
            }
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }


}

dependencies {
    implementation (project(":sdk"))

    implementation (libs.core.ktx)
    implementation (libs.appcompat)
    implementation (libs.material)

    implementation (libs.lifecycle.viewmodel.ktx)
    implementation (libs.navigation.ui.ktx)
    implementation (libs.kotlinx.coroutines.android)

    implementation (platform(libs.compose.bom))
    implementation (libs.bundles.compose)
    implementation (libs.lottie.compose)
    implementation (libs.lottie)

    testImplementation (libs.kotlin.test)
    testImplementation (libs.junit)

    implementation (libs.timber)
}

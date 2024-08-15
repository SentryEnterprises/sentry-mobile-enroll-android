
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
    }


    buildTypes["debug"].apply {
        isDebuggable = true
        ndk {
            isDebuggable = true
        }
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }

    buildFeatures {
        compose = true
    }

}

dependencies {
    implementation (project(":JCWKit"))

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
    testImplementation (libs.mockk)

    implementation (libs.timber)
}

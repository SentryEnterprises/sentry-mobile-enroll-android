
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

    buildFeatures {
        compose = true
        dataBinding = true
    }

}

dependencies {
//    implementation fileTree(dir: "libs", include: ["*.jar"])

    implementation (project(":JCWKit"))

    // Android
    implementation ("androidx.core:core-ktx:1.13.1")
    implementation ("androidx.fragment:fragment-ktx:1.8.2")
    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")

    // Navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    implementation (platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.lottie.compose)

    // Lottie
    implementation ("com.airbnb.android:lottie:5.2.0")

    // Secure shared preferences
    implementation ("androidx.security:security-crypto:1.0.0")

    // Tests
    testImplementation ("org.jetbrains.kotlin:kotlin-test")
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("io.mockk:mockk:1.13.8")
    androidTestImplementation ("androidx.test.ext:junit:1.2.1")

    // Timber
    implementation ("com.jakewharton.timber:timber:5.0.1")

}

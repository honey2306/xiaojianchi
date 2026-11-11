plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

val littleStepsKeystoreFile = providers.environmentVariable("LITTLE_STEPS_KEYSTORE_FILE").orElse("").get()
val littleStepsKeystorePassword = providers.environmentVariable("LITTLE_STEPS_KEYSTORE_PASSWORD").orElse("").get()
val littleStepsKeyAlias = providers.environmentVariable("LITTLE_STEPS_KEY_ALIAS").orElse("").get()
val littleStepsKeyPassword = providers.environmentVariable("LITTLE_STEPS_KEY_PASSWORD").orElse("").get()
val littleStepsSigningEnabled = littleStepsKeystoreFile.isNotBlank() &&
    littleStepsKeystorePassword.isNotBlank() &&
    littleStepsKeyAlias.isNotBlank() &&
    littleStepsKeyPassword.isNotBlank()

android {
    namespace = "com.xiaojianchi.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.xiaojianchi.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        buildConfigField("String", "UPDATE_MANIFEST_URL", "\"https://github.com/honey2306/xiaojianchi/releases/latest/download/update.json\"")
        buildConfigField("String", "UPDATE_FALLBACK_URL", "\"https://honey2306.github.io/xiaojianchi/update.json\"")
    }

    signingConfigs {
        if (littleStepsSigningEnabled) {
            create("release") {
                storeFile = file(littleStepsKeystoreFile)
                storePassword = littleStepsKeystorePassword
                keyAlias = littleStepsKeyAlias
                keyPassword = littleStepsKeyPassword
            }
        }
    }
    buildTypes {
        release {
            if (littleStepsSigningEnabled) signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures { compose = true; buildConfig = true }
    lint { disable += "GradleDependency" }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

ksp { arg("room.schemaLocation", "$projectDir/schemas") }

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.compose.ui:ui:1.7.6")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.6")
    implementation("androidx.compose.foundation:foundation:1.7.6")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.material:material-icons-extended:1.7.6")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    ksp("androidx.room:room-compiler:2.6.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.6")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.6")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.6")
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "nz.co.chrisdrake.receipts"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = (project.property("BASE_APPLICATION_ID") as String) + ".receipts"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = rootProject.file("keystore/debug.keystore")
            storePassword = project.property("RECEIPTS_DEBUG_KEYSTORE_PASSWORD") as String
            keyAlias = project.property("RECEIPTS_DEBUG_KEY_ALIAS") as String
            keyPassword = project.property("RECEIPTS_DEBUG_KEY_PASSWORD") as String
        }

        create("release") {
            storeFile = rootProject.file("keystore/app-release.jks")
            storePassword = project.property("RECEIPTS_RELEASE_KEYSTORE_PASSWORD") as String
            keyAlias = project.property("RECEIPTS_RELEASE_KEY_ALIAS") as String
            keyPassword = project.property("RECEIPTS_RELEASE_KEY_PASSWORD") as String
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
        }

        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}

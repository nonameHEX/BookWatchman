import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.com.google.devtools.ksp)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").reader())

android {
    val versionMajor = 1
    val versionMinor = 0
    val versionPatch = 0
    val myVersionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
    val myVersionName = "${versionMajor}.${versionMinor}.${versionPatch}"

    namespace = "cz.mendelu.bookwatchman"
    compileSdk = 35

    defaultConfig {
        applicationId = "cz.mendelu.bookwatchman"
        minSdk = 26
        targetSdk = 34
        versionCode = myVersionCode
        versionName = myVersionName

        testInstrumentationRunner = "cz.mendelu.bookwatchman.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        manifestPlaceholders["googleMapsApiKey"] = properties.getProperty("apikey")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", properties.getProperty("baseurldevel"))
            buildConfigField("String", "API_KEY", "\"${properties.getProperty("apikey")}\"")
        }
        debug {
            buildConfigField("String", "BASE_URL", properties.getProperty("baseurlproduction"))
            buildConfigField("String", "API_KEY", "\"${properties.getProperty("apikey")}\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.kapt)

    // Hilt testy
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.46")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.51.1")

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.retrofit.okhtt3)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)

    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ktx)
    implementation(libs.navigation.compose)

    // Lifecycle
    implementation(libs.lifecycle)

    // Room DB
    implementation(libs.lifecycle)
    implementation(libs.room.ktx)
    implementation(libs.room.viewmodel)
    implementation(libs.room.lifecycle)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler.kapt)

    // DataStore
    implementation(libs.datastore.core)
    implementation(libs.datastore.preferences)

    // Map
    implementation(libs.googlemap)
    implementation(libs.googlemap.compose)
    implementation(libs.googlemap.foundation)
    implementation(libs.googlemap.utils)
    implementation(libs.googlemap.widgets)
    implementation(libs.googlemap.compose.utils)
    implementation(libs.googlemap.location.services)

    // Places
    implementation (libs.googleplaces)

    // Coil pro images
    implementation(libs.coil.compose)

    // ML Kit knihovny
    // To recognize Latin script
    implementation(libs.mlkit.text.recognition)
    // CameraX
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.camerax.mlkit.vision)
    implementation(libs.camerax.extensions)
}
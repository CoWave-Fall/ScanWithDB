plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.ksp) // Changed from kapt to ksp
    alias(libs.plugins.hilt) 
}



android {
    namespace = "sys.tianr.test.scanwithdb"
    compileSdk = 36

    defaultConfig {
        applicationId = "sys.tianr.test.scanwithdb"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }



    buildFeatures {
    viewBinding = true
    buildConfig = true
    }
}

dependencies {
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

// Core & UI
//implementation("androidx.core:core-ktx:1.12.0")
//implementation("androidx.appcompat:appcompat:1.6.1")
implementation(libs.google.material)
implementation(libs.constraintlayout)

// Navigation Component (for Fragment navigation)
implementation(libs.androidx.navigation.fragment.ktx)
implementation(libs.androidx.navigation.ui.ktx)

// Lifecycle, ViewModel, LiveData
implementation(libs.androidx.lifecycle.viewmodel.ktx)
implementation(libs.androidx.lifecycle.livedata.ktx)
implementation(libs.androidx.fragment.ktx)

// Room (for local database)
implementation(libs.androidx.room.runtime)
ksp(libs.androidx.room.compiler) // Changed from kapt to ksp
implementation(libs.androidx.room.ktx) // Kotlin Coroutines support for Room

// Hilt (for Dependency Injection)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler) // Changed from kapt to ksp
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0") //

// CameraX (for camera operations)
val cameraxVersion = "1.4.2"
implementation("androidx.camera:camera-core:${cameraxVersion}")
implementation("androidx.camera:camera-camera2:${cameraxVersion}")
implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
implementation("androidx.camera:camera-view:${cameraxVersion}")



// ML Kit (for barcode scanning)
    implementation(libs.mlkit.barcode.scanning)
}

kotlin {
    jvmToolchain(17) // Target JVM 17
    // Re-sync
}
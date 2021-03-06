import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

}

android {
    signingConfigs {
        create("release"){
            val properties = Properties().apply {
                load(File("signing.properties").reader())
            }

            storeFile = File(properties.getProperty("key_store"))
            storePassword = properties.getProperty("key_store_password")
            keyPassword = properties.getProperty("key_password")
            keyAlias = properties.getProperty("key_alias")
        }

        getByName("debug") {
            storeFile = rootProject.file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }

    }

    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    buildFeatures {
        compose =  true
    }

    lintOptions {

        isAbortOnError = false
        lintOptions {
            checkOnly("release")
        }
    }

    composeOptions {
        kotlinCompilerVersion = "1.4.21"
        kotlinCompilerExtensionVersion ="1.0.0-alpha09"
    }

    compileOptions {
        sourceCompatibility =  JavaVersion.VERSION_1_8
        targetCompatibility  = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xallow-jvm-ir-dependencies",
            "-Xskip-prerelease-check",
            "-Xopt-in=kotlin.Experimental",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    defaultConfig {
        applicationId ="com.imfibit.activitytracker"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 2
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            manifestPlaceholders["crashlyticsCollectionEnabled"] = true

            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            versionNameSuffix = " - PROD"
        }

        getByName("debug") {
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            debuggable(true)

            applicationIdSuffix =  ".debug"
            versionNameSuffix = " - DEBUG"
        }

        create("personal") {
            isMinifyEnabled = false
            manifestPlaceholders["crashlyticsCollectionEnabled"] = true

            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            debuggable(true)

            applicationIdSuffix =  ".personal"
            versionNameSuffix = " - TEST"
        }

    }

}

dependencies {


    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.21")
    implementation("com.thedeanda:lorem:2.1")

    implementation(platform("com.google.firebase:firebase-bom:25.11.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    implementation("androidx.navigation:navigation-compose:1.0.0-alpha04")

    implementation("androidx.compose.compiler:compiler:1.0.0-alpha09")
    implementation("androidx.compose.ui:ui:1.0.0-alpha09")
    implementation("androidx.compose.material:material:1.0.0-alpha09")
    implementation("androidx.compose.runtime:runtime:1.0.0-alpha09")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-alpha09")
    implementation("androidx.compose.foundation:foundation:1.0.0-alpha09")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-alpha09")

    kapt("androidx.room:room-compiler:2.3.0-alpha04")
    implementation ("androidx.room:room-runtime:2.3.0-alpha04")
    implementation ("androidx.room:room-ktx:2.3.0-alpha04")
}

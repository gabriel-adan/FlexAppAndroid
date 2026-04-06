import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.mapsplatform.secrets.plugin)
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

android {
    namespace = "com.gas.flexapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gas.flexapp"
        minSdk = 29
        targetSdk = 35
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
            buildConfigField("String", "FLEXAPP_BASE_URL", "\"" + localProperties["flexAppBaseUrl"] + "\"")
            buildConfigField("String", "CIPHER_KEY", "\"" + localProperties["cipherKey"] + "\"")
            buildConfigField("String", "DATABASE_NAME", "\"" + localProperties["databaseName"] + "\"")
            buildConfigField("String", "DATABASE_VERSION", "\"" + localProperties["databaseVersion"] + "\"")
            buildConfigField("String", "MAPS_DIRECTIONS_URL", "\"" + localProperties["googleMapsDirectionBaseUrl"] + "\"")
        }
        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
            buildConfigField("String", "FLEXAPP_BASE_URL", "\"" + localProperties["flexAppBaseUrl"] + "\"")
            buildConfigField("String", "CIPHER_KEY", "\"" + localProperties["cipherKey"] + "\"")
            buildConfigField("String", "DATABASE_NAME", "\"" + localProperties["databaseName"] + "\"")
            buildConfigField("String", "DATABASE_VERSION", "\"" + localProperties["databaseVersion"] + "\"")
            buildConfigField("String", "MAPS_DIRECTIONS_URL", "\"" + localProperties["googleMapsDirectionBaseUrl"] + "\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.android.maps.utils)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    implementation(project(":model"))
    implementation(project(":components"))
    implementation(files("../libs/androidorm.aar"))

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.androidx.espresso.core)
    testImplementation(libs.fragment.testing)
    testImplementation(libs.navigation.testing)
    testImplementation(libs.espresso.contrib)
    debugImplementation(libs.fragment.testing.manifest)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kover {
    currentProject {
        createVariant("custom") {

        }
    }

    reports {
        filters {
            excludes {
                classes(
                    "**androidx**",
                    "**databinding**",
                    "dagger**",
                    "hilt**",
                    "**Hilt_**",
                    "**_Factory**",
                    "**_HiltModules**",
                    "**Module_Bind**",
                    "**MainApplication**",
                    "**HiltTestActivity**"
                )
            }
        }
    }
}
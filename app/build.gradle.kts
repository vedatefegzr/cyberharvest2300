plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.cyberharvest2300"

    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.cyberharvest2300"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    val room_version = "2.8.4"

    implementation(
        "androidx.room:room-runtime:$room_version"
    )

    implementation(
        "androidx.room:room-ktx:$room_version"
    )

    implementation(
        "androidx.room:room-paging:$room_version"
    )

    ksp(
        "androidx.room:room-compiler:$room_version"
    )

    implementation(
        "androidx.core:core-splashscreen:1.0.0"
    )

    implementation(
        "androidx.navigation:navigation-fragment-ktx:2.9.3"
    )

    implementation(
        "androidx.navigation:navigation-ui-ktx:2.9.3"
    )
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(28)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    api(project(":api"))
    api(project(":data"))

    implementation(Depends.KotlinCoroutines)
    implementation(Depends.KotlinStdLib)
    implementation(Depends.KoinScope)

    testImplementation(Depends.JUnit)

    androidTestImplementation(Depends.TestRunner)
    androidTestImplementation(Depends.EspressoCore)
}

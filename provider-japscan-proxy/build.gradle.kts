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

        buildConfigField("String", "JAPSCAN_PROXY_BASE_URL", "\"https://japscan-proxy.herokuapp.com\"")
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

    implementation(Depends.KotlinStdLib)
    implementation(Depends.KotlinCoroutines)
    implementation(Depends.Gson)

    testImplementation(Depends.JUnit)

    androidTestImplementation(Depends.TestRunner)
    androidTestImplementation(Depends.EspressoCore)
}

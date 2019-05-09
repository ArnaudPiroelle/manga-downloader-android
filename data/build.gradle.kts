plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(28)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments = mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(Depends.KotlinStdLib)
    implementation(Depends.KotlinCoroutines)

    implementation(Depends.KoinScope)

    api(Depends.LifecycleExtensions)
    kapt(Depends.LifecycleCompiler)

    api(Depends.PagingRuntime)

    api(Depends.RoomRuntime)
    api(Depends.RoomCoroutines)
    kapt(Depends.RoomCompiler)

    testImplementation(Depends.JUnit)

    androidTestImplementation(Depends.TestRunner)
    androidTestImplementation(Depends.EspressoCore)
}
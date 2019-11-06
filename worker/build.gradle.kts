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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}


dependencies {
    implementation(project(":data"))
    implementation(project(":api"))
    implementation(project(":interactors"))

    implementation(Depends.KotlinStdLib)

    implementation(Depends.KoinScope)
    implementation(Depends.Timber)

    api(Depends.WorkRuntime)

    testImplementation(Depends.JUnit)

    androidTestImplementation(Depends.TestRunner)
    androidTestImplementation(Depends.EspressoCore)
}


tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "com.arnaudpiroelle.manga"

        minSdkVersion(24)
        targetSdkVersion(28)

        versionCode = 21
        versionName = "3.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packagingOptions {
        exclude("LICENSE.txt")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    signingConfigs {
        create("release") {
            storeFile = file("manga-downloader.keystore")
            storePassword = propOrDef("APP_KEYSTORE_PASSWORD", "manga-downloader")
            keyAlias = "manga-downloader"
            keyPassword = propOrDef("APP_KEYSTORE_PASSWORD", "manga-downloader")
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".dev"
            isMinifyEnabled = false
            isTestCoverageEnabled = true

            signingConfig = signingConfigs.getByName("release")
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

            signingConfig = signingConfigs.getByName("release")
        }
    }

    lintOptions {
        isAbortOnError = false
    }

}

kapt {
    correctErrorTypes = true
}

dependencies {
    api(project(":data"))
    api(project(":provider-japscan-proxy"))
    api(project(":worker"))
    api(project(":interactors"))

    implementation(Depends.AppCompat)
    implementation(Depends.Preference)
    implementation(Depends.Palette)

    implementation(Depends.LifecycleExtensions)
    kapt(Depends.LifecycleCompiler)

    implementation(Depends.ConstraintLayout)

    implementation(Depends.KotlinCoroutines)
    implementation(Depends.KotlinStdLib)

    implementation(Depends.MaterialDesign)

    implementation(Depends.NavigationUi)
    implementation(Depends.NavigationFragment)

    implementation(Depends.StorageChooser)

    implementation(Depends.RoomRuntime)
    kapt(Depends.RoomCompiler)

    implementation(Depends.GlideRuntime)
    kapt(Depends.GlideCompiler)

    implementation(Depends.StethoRuntime)
    implementation(Depends.StethoOkhttp)
    implementation(Depends.Timber)

    implementation(Depends.KoinScope)
    implementation(Depends.KoinViewModel)

    implementation(Depends.IndicatorFastScroll)

    debugImplementation(Depends.LeakCanary)

    testImplementation(Depends.JUnit)
    testImplementation(Depends.Robolectric)
}

fun <T> propOrDef(propertyName: String, defaultValue: T): T {
    return if (hasProperty(propertyName)) property(propertyName) as T else defaultValue
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
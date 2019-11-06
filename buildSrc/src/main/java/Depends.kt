object Depends {

    private object Versions {
        const val kotlinVersion = "1.3.50"
        const val okHttpVersion = "3.12.6"
        const val lifecycleVersion = "2.2.0-rc01"
        const val roomVersion = "2.2.1"
        const val koinVersion = "2.0.1"
        const val navigationVersion = "2.2.0-rc01"
        const val glideVersion = "4.10.0"
        const val stethoVersion = "1.5.1"
        const val retrofitVersion = "2.6.2"
    }

    // Gradle Plugins
    const val AndroidGradlePlugin = "com.android.tools.build:gradle:4.0.0-alpha01"
    const val KotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val FabricGradlePlugin = "io.fabric.tools:gradle:1.28.0"
    const val NavigationGradlePlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigationVersion}"


    // AndroidX
    const val LifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleVersion}"
    const val LifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycleVersion}"
    const val PagingRuntime = "androidx.paging:paging-runtime-ktx:2.1.0"
    const val RoomRuntime = "androidx.room:room-runtime:${Versions.roomVersion}"
    const val RoomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
    const val RoomCoroutines = "androidx.room:room-ktx:${Versions.roomVersion}"
    const val AppCompat = "androidx.appcompat:appcompat:1.1.0"
    const val Preference = "androidx.preference:preference-ktx:1.1.0"
    const val Palette = "androidx.palette:palette-ktx:1.0.0"
    const val ConstraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta3"
    const val WorkRuntime = "androidx.work:work-runtime-ktx:2.3.0-alpha03"
    const val NavigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigationVersion}"
    const val NavigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigationVersion}"
    const val MaterialDesign = "com.google.android.material:material:1.2.0-alpha01"

    // Dependencies
    const val KotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}"
    const val KotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2"
    const val OkHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttpVersion}"
    const val OkHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttpVersion}"
    const val Gson = "com.google.code.gson:gson:2.8.6"
    const val KoinCore = "org.koin:koin-core:${Versions.koinVersion}"
    const val KoinScope = "org.koin:koin-androidx-scope:${Versions.koinVersion}"
    const val KoinViewModel = "org.koin:koin-androidx-viewmodel:${Versions.koinVersion}"
    const val Timber = "com.jakewharton.timber:timber:4.7.1"
    const val StorageChooser = "com.github.codekidX:storage-chooser:2.0.4.4"
    const val GlideRuntime = "com.github.bumptech.glide:glide:${Versions.glideVersion}"
    const val GlideCompiler = "com.github.bumptech.glide:compiler:${Versions.glideVersion}"
    const val StethoRuntime = "com.facebook.stetho:stetho:${Versions.stethoVersion}"
    const val StethoOkhttp = "com.facebook.stetho:stetho-okhttp3:${Versions.stethoVersion}"
    const val IndicatorFastScroll = "com.reddit:indicator-fast-scroll:1.2.0"
    const val LeakCanary = "com.squareup.leakcanary:leakcanary-android:2.0-beta-3"
    const val Retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}"
    const val RetrofitGsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofitVersion}"
    const val RetrofitCoroutinesAdapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"

    // Tests
    const val JUnit = "junit:junit:4.12"
    const val Robolectric = "org.robolectric:robolectric:4.0.2"

    // Android Tests
    const val TestRunner = "androidx.test:runner:1.1.1"
    const val EspressoCore = "androidx.test.espresso:espresso-core:3.1.1"
}
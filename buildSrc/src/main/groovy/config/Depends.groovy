package config

interface Depends {
    // Versions
    static final kotlinVersion = "1.3.50"
    static final okHttpVersion = "4.9.0"
    static final lifecycleVersion = "2.2.0-rc01"
    static final roomVersion = "2.2.1"
    static final koinVersion = "2.0.1"
    static final navigationVersion = "2.2.0-rc01"
    static final glideVersion = "4.10.0"
    static final stethoVersion = "1.5.1"
    static final retrofitVersion = "2.9.0"

    // Gradle Plugins
    static final AndroidGradlePlugin = "com.android.tools.build:gradle:4.1.0-beta05"
    static final KotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}"
    static final FabricGradlePlugin = "io.fabric.tools:gradle:1.28.0"
    static final NavigationGradlePlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:${navigationVersion}"

    // AndroidX
    static final LifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${lifecycleVersion}"
    static final LifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${lifecycleVersion}"
    static final PagingRuntime = "androidx.paging:paging-runtime-ktx:2.1.0"
    static final RoomRuntime = "androidx.room:room-runtime:${roomVersion}"
    static final RoomCompiler = "androidx.room:room-compiler:${roomVersion}"
    static final RoomCoroutines = "androidx.room:room-ktx:${roomVersion}"
    static final AppCompat = "androidx.appcompat:appcompat:1.1.0"
    static final Preference = "androidx.preference:preference-ktx:1.1.0"
    static final Palette = "androidx.palette:palette-ktx:1.0.0"
    static final ConstraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta3"
    static final WorkRuntime = "androidx.work:work-runtime-ktx:2.3.0-alpha03"
    static final NavigationFragment = "androidx.navigation:navigation-fragment-ktx:${navigationVersion}"
    static final NavigationUi = "androidx.navigation:navigation-ui-ktx:${navigationVersion}"
    static final MaterialDesign = "com.google.android.material:material:1.2.0-alpha01"

    // Dependencies
    static final KotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}"
    static final KotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2"
    static final OkHttp = "com.squareup.okhttp3:okhttp:${okHttpVersion}"
    static final OkHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${okHttpVersion}"
    static final Gson = "com.google.code.gson:gson:2.8.6"
    static final KoinCore = "org.koin:koin-core:${koinVersion}"
    static final KoinScope = "org.koin:koin-androidx-scope:${koinVersion}"
    static final KoinViewModel = "org.koin:koin-androidx-viewmodel:${koinVersion}"
    static final Timber = "com.jakewharton.timber:timber:4.7.1"
    static final StorageChooser = "com.github.codekidX:storage-chooser:2.0.4.4"
    static final GlideRuntime = "com.github.bumptech.glide:glide:${glideVersion}"
    static final GlideCompiler = "com.github.bumptech.glide:compiler:${glideVersion}"
    static final StethoRuntime = "com.facebook.stetho:stetho:${stethoVersion}"
    static final StethoOkhttp = "com.facebook.stetho:stetho-okhttp3:${stethoVersion}"
    static final IndicatorFastScroll = "com.reddit:indicator-fast-scroll:1.2.0"
    static final LeakCanary = "com.squareup.leakcanary:leakcanary-android:2.0-beta-3"
    static final Retrofit = "com.squareup.retrofit2:retrofit:${retrofitVersion}"
    static final RetrofitGsonConverter = "com.squareup.retrofit2:converter-gson:${retrofitVersion}"
    static final RetrofitCoroutinesAdapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"

    // Tests
    static final JUnit = "junit:junit:4.12"
    static final Robolectric = "org.robolectric:robolectric:4.0.2"

    // Android Tests
    static final TestRunner = "androidx.test:runner:1.1.1"
    static final EspressoCore = "androidx.test.espresso:espresso-core:3.1.1"
}
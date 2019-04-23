object Depends {
    // Versions
    private const val kotlinVersion = "1.3.30"
    private const val okHttpVersion = "3.11.0"
    private const val lifecycleVersion = "2.0.0"
    private const val roomVersion = "2.1.0-alpha07"
    private const val koinVersion = "1.0.2"

    // Gradle Plugins
    const val AndroidGradlePlugin = "com.android.tools.build:gradle:3.5.0-alpha13"
    const val KotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val FabricGradlePlugin = "io.fabric.tools:gradle:1.25.4"
    const val NavigationGradlePlugin = "android.arch.navigation:navigation-safe-args-gradle-plugin:1.0.0"


    // Dependencies
    const val KotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
    const val KotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.0.0"
    const val OkHttp = "com.squareup.okhttp3:okhttp:$okHttpVersion"
    const val OkHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$okHttpVersion"
    const val Gson = "com.google.code.gson:gson:2.8.5"
    const val KoinScope = "org.koin:koin-androidx-scope:$koinVersion"
    const val KoinViewModel = "org.koin:koin-androidx-viewmodel:$koinVersion"
    const val Timber = "com.jakewharton.timber:timber:4.7.1"
    const val WorkRuntime = "android.arch.work:work-runtime-ktx:1.0.1"
    const val LifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:$lifecycleVersion"
    const val LifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:$lifecycleVersion"
    const val PagingRuntime = "androidx.paging:paging-runtime:2.1.0"
    const val RoomRuntime = "androidx.room:room-runtime:$roomVersion"
    const val RoomCompiler = "androidx.room:room-compiler:$roomVersion"
    const val AppCompat = "androidx.appcompat:appcompat:1.1.0-alpha05"
    const val Preference = "androidx.preference:preference:1.0.0"
    const val Palette = "androidx.palette:palette:1.0.0"
    const val ConstraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-alpha5"
    const val MaterialDesign = "com.google.android.material:material:1.1.0-alpha05"
    const val NavigationFragment = "android.arch.navigation:navigation-fragment-ktx:1.0.0"
    const val NavigationUi = "android.arch.navigation:navigation-ui-ktx:1.0.0"
    const val StorageChooser = "com.github.codekidX:storage-chooser:2.0.4.2"
    const val PermissionDispatcherRuntime = "org.permissionsdispatcher:permissionsdispatcher:4.3.0"
    const val PermissionDispatcherProcessor = "org.permissionsdispatcher:permissionsdispatcher-processor:4.3.0"
    const val GlideRuntime = "com.github.bumptech.glide:glide:4.8.0"
    const val GlideCompiler = "com.github.bumptech.glide:compiler:4.8.0"
    const val StethoRuntime = "com.facebook.stetho:stetho:1.5.0"
    const val StethoOkhttp = "com.facebook.stetho:stetho-okhttp3:1.5.0"
    const val IndicatorFastScroll = "com.reddit:indicator-fast-scroll:1.1.0-beta1"
    const val LeakCanary = "com.squareup.leakcanary:leakcanary-android:2.0-alpha-1"

    // Tests
    const val JUnit = "junit:junit:4.12"
    const val Robolectric = "org.robolectric:robolectric:4.0.2"

    // Android Tests
    const val TestRunner = "androidx.test:runner:1.1.1"
    const val EspressoCore = "androidx.test.espresso:espresso-core:3.1.1"
}
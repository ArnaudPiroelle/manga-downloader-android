object Depends {

    private object Versions {
        const val kotlinVersion = "1.3.30"
        const val okHttpVersion = "3.11.0"
        const val lifecycleVersion = "2.0.0"
        const val roomVersion = "2.1.0-alpha07"
        const val koinVersion = "1.0.2"
        const val navigationVersion = "1.0.0"
        const val permissionDispatcherVersion = "4.3.0"
        const val glideVersion = "4.8.0"
        const val stethoVersion = "1.5.0"
    }

    // Gradle Plugins
    const val AndroidGradlePlugin = "com.android.tools.build:gradle:3.5.0-alpha13"
    const val KotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val FabricGradlePlugin = "io.fabric.tools:gradle:1.25.4"
    const val NavigationGradlePlugin = "android.arch.navigation:navigation-safe-args-gradle-plugin:${Versions.navigationVersion}"


    // Dependencies
    const val KotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}"
    const val KotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.0.0"
    const val OkHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttpVersion}"
    const val OkHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttpVersion}"
    const val Gson = "com.google.code.gson:gson:2.8.5"
    const val KoinScope = "org.koin:koin-androidx-scope:${Versions.koinVersion}"
    const val KoinViewModel = "org.koin:koin-androidx-viewmodel:${Versions.koinVersion}"
    const val Timber = "com.jakewharton.timber:timber:4.7.1"
    const val WorkRuntime = "android.arch.work:work-runtime-ktx:1.0.1"
    const val LifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleVersion}"
    const val LifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycleVersion}"
    const val PagingRuntime = "androidx.paging:paging-runtime:2.1.0"
    const val RoomRuntime = "androidx.room:room-runtime:${Versions.roomVersion}"
    const val RoomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
    const val AppCompat = "androidx.appcompat:appcompat:1.1.0-alpha05"
    const val Preference = "androidx.preference:preference:1.0.0"
    const val Palette = "androidx.palette:palette:1.0.0"
    const val ConstraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-alpha3"
    const val MaterialDesign = "com.google.android.material:material:1.1.0-alpha05"
    const val NavigationFragment = "android.arch.navigation:navigation-fragment-ktx:${Versions.navigationVersion}"
    const val NavigationUi = "android.arch.navigation:navigation-ui-ktx:${Versions.navigationVersion}"
    const val StorageChooser = "com.github.codekidX:storage-chooser:2.0.4.2"
    const val PermissionDispatcherRuntime = "org.permissionsdispatcher:permissionsdispatcher:${Versions.permissionDispatcherVersion}"
    const val PermissionDispatcherProcessor = "org.permissionsdispatcher:permissionsdispatcher-processor:${Versions.permissionDispatcherVersion}"
    const val GlideRuntime = "com.github.bumptech.glide:glide:${Versions.glideVersion}"
    const val GlideCompiler = "com.github.bumptech.glide:compiler:${Versions.glideVersion}"
    const val StethoRuntime = "com.facebook.stetho:stetho:${Versions.stethoVersion}"
    const val StethoOkhttp = "com.facebook.stetho:stetho-okhttp3:${Versions.stethoVersion}"
    const val IndicatorFastScroll = "com.reddit:indicator-fast-scroll:1.1.0-beta1"
    const val LeakCanary = "com.squareup.leakcanary:leakcanary-android:2.0-alpha-1"

    // Tests
    const val JUnit = "junit:junit:4.12"
    const val Robolectric = "org.robolectric:robolectric:4.0.2"

    // Android Tests
    const val TestRunner = "androidx.test:runner:1.1.1"
    const val EspressoCore = "androidx.test.espresso:espresso-core:3.1.1"
}
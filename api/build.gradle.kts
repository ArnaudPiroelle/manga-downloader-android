plugins {
    id("kotlin")
}

dependencies {
    implementation(Depends.KotlinStdLib)
    implementation(Depends.KoinCore)

    api(Depends.OkHttp)
    api(Depends.OkHttpLoggingInterceptor)

    testImplementation(Depends.JUnit)
}

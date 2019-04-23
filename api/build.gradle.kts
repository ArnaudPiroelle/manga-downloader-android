plugins {
    id("kotlin")
}

dependencies {
    implementation(Depends.KotlinStdLib)

    api(Depends.OkHttp)
    api(Depends.OkHttpLoggingInterceptor)

    testImplementation(Depends.JUnit)
}

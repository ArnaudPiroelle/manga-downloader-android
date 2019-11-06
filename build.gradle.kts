buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://maven.fabric.io/public")
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    }

    dependencies {
        classpath(Depends.AndroidGradlePlugin)
        classpath(Depends.KotlinGradlePlugin)
        classpath(Depends.FabricGradlePlugin)
        classpath(Depends.NavigationGradlePlugin)
    }
}

allprojects {
    repositories {
        jcenter()
        google()
        maven(url = "https://maven.fabric.io/public")
        maven(url = "https://jitpack.io")
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    }
}
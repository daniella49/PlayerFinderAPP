buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.google.services)
    }
}

plugins {
    id("com.android.application") version "8.7.0" apply false
    id("com.android.library") version "8.7.0" apply false
}

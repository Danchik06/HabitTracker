plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.devtools.ksp") version "2.3.6" apply false
    id("com.google.dagger.hilt.android") version "2.60.1" apply false
    alias(libs.plugins.kotlin.compose) apply false
}
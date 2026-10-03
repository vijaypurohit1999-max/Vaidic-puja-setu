// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Configured for Gradle 8.4 and Android Gradle Plugin (AGP) 8.2.2 (see gradle/libs.versions.toml).
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.secrets) apply false
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  if (gradle.gradleVersion.startsWith("9.")) {
    dependencies {
      classpath("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:2.2.10")
    }
  }
}

plugins {
  id("com.android.application") version "8.2.2" apply false
  id("org.jetbrains.kotlin.android") version "1.9.22" apply false
  id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}

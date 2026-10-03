pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
  if (gradle.gradleVersion.startsWith("9.")) {
    resolutionStrategy {
      eachPlugin {
        when (requested.id.id) {
          "com.android.application" -> useVersion("9.1.1")
          "org.jetbrains.kotlin.android" -> useVersion("2.2.10")
          "com.google.devtools.ksp" -> useVersion("2.3.5")
        }
      }
    }
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
  if (gradle.gradleVersion.startsWith("9.")) {
    versionCatalogs {
      create("libs") {
        version("agp", "9.1.1")
        version("coreKtx", "1.18.0")
        version("junitVersion", "1.3.0")
        version("espressoCore", "3.7.0")
        version("lifecycleRuntimeKtx", "2.8.7")
        version("lifecycleViewmodelCompose", "2.8.7")
        version("lifecycleRuntimeCompose", "2.8.7")
        version("activityCompose", "1.10.1")
        version("kotlin", "2.2.10")
        version("composeBom", "2024.09.00")
        version("googleDevtoolsKsp", "2.3.5")
        version("navigationCompose", "2.8.9")
        version("roomRuntime", "2.7.0")
        version("roomKtx", "2.7.0")
        version("roomCompiler", "2.7.0")
        version("kotlinxCoroutinesTest", "1.10.2")
        version("core", "1.6.1")
        version("runner", "1.6.2")
        version("retrofit", "2.12.0")
        version("converterMoshi", "2.12.0")
        version("kotlinxCoroutinesAndroid", "1.10.2")
        version("kotlinxCoroutinesCore", "1.10.2")
        version("accompanistPermissions", "0.37.3")
        version("cameraCamera2", "1.5.0")
        version("cameraLifecycle", "1.5.0")
        version("cameraView", "1.5.0")
        version("cameraCore", "1.5.0")
        version("loggingInterceptor", "4.10.0")
        version("okhttp", "4.10.0")
        version("moshiKotlin", "1.15.2")
        version("moshiKotlinCodegen", "1.15.2")
        version("datastorePreferences", "1.1.7")
        version("robolectric", "4.16.1")
        version("firebaseBom", "34.17.0")
        version("googleServices", "4.5.0")
        version("credentials", "1.5.0")
      }
    }
  }
}

rootProject.name = "Vaidik Puja"

include(":app")

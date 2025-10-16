pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // 🔥 Firebase için GEREKLİ
        mavenCentral()
    }
}

rootProject.name = "dermAI"
include(":app")

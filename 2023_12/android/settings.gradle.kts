pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    // Default libraries located here -> gradle/libs.versions.toml
    // If not default name then use 'versionCatalogs':
    // versionCatalogs { create("libs") { from(files("gradle/otherLibs.versions.toml")) } }
    defaultLibrariesExtensionName = "libs"
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "Example 2023"
include(":app")

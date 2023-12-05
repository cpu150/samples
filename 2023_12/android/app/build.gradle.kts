import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android.plugin)
    alias(libs.plugins.kps)
    alias(libs.plugins.kotlin.serialization)
}

val runCommand: (project: Project, cmd: String) -> String = { project: Project, cmd: String ->
    val byteOut = ByteArrayOutputStream()
    project.exec {
        commandLine = cmd.split(" ")
        standardOutput = byteOut
    }
    String(byteOut.toByteArray()).trim()
}

android {
    val signingConfigName = "debug"
    val gitBranch = runCommand(project, "git rev-parse --abbrev-ref HEAD")
        .takeIf { it.isNotBlank() } ?: "no_branch"
    val gitTag = runCommand(project, "git tag -l --points-at HEAD")
        .takeIf { it.isNotBlank() } ?: "no_tag"
    val gitHash = runCommand(project, "git rev-parse --short=8 HEAD")
        .takeIf { it.isNotBlank() } ?: "no_hash"
    val gitRevCount = runCommand(project, "git rev-list --count HEAD").toIntOrNull() ?: -1
    val prodProguardFiles = listOf(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
        "proguard-serializable-rules.pro",
    ).toTypedArray()

    namespace = "com.example.example2023"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.example2023"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = gitRevCount
        versionName = "1.0"

        val projectName = applicationId?.split(".")?.last() ?: "no_project_name"
        archivesName =
            "$projectName-$versionName-$versionCode-$gitHash-${gitBranch.replace("/", "_")}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName(signingConfigName) {
            keyAlias = "localSigning"
            keyPassword = "Password"
            storeFile = file("../app/certificates/localSigning.keystore")
            storePassword = "Password"
        }
    }
    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    flavorDimensions += "environment"
    productFlavors {
        val commonVersionNameSuffix = "$gitBranch.$gitTag.$gitHash"
        val randomUserApiVer = "1.4"

        create("dev") {
            dimension = "environment"
            signingConfig = signingConfigs.getByName(signingConfigName)
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev ($commonVersionNameSuffix)"
            resValue("string", "app_name", "Example 2023 dev")
            buildConfigField(
                "String",
                "RANDOM_USER_BASE_URL",
                "\"https://randomuser.me/api/$randomUserApiVer/\""
            )
        }
        create("qa") {
            dimension = "environment"
            signingConfig = signingConfigs.getByName(signingConfigName)
            applicationIdSuffix = ".qa"
            versionNameSuffix = "-qa ($commonVersionNameSuffix)"
            matchingFallbacks += "debug"
            proguardFiles(*(prodProguardFiles + "proguard-rules-qa.pro"))
            resValue("string", "app_name", "Example 2023 QA")
            buildConfigField(
                "String",
                "RANDOM_USER_BASE_URL",
                "\"https://randomuser.me/api/$randomUserApiVer/\""
            )
        }
        create("prod") {
            dimension = "environment"
            proguardFiles(*prodProguardFiles)
            buildConfigField(
                "String",
                "RANDOM_USER_BASE_URL",
                "\"https://randomuser.me/api/$randomUserApiVer/\""
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    @Suppress("UnstableApiUsage")
    testOptions {
        animationsDisabled = true
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    // Ktx
    implementation(libs.androidx.core.ktx)

    // Android
    implementation(libs.androidx.appcompat)

    // Material Design
    implementation(libs.android.material)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    // Compose
    implementation(libs.androidx.material3.compose)
    implementation(libs.androidx.navigation.compose)

    // Compose optional - Integration with lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Compose Android Studio Preview support
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Retrofit
    implementation(libs.retrofit)

    // Kotlin JSON serialization
    implementation(libs.kotlinx.serialization.json)

    // Kotlinx Serialization Converter
    implementation(libs.serialization.converter)

    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Tests
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.junit.android)

    // UI Tests
    androidTestImplementation(libs.test.ui.espresso)
}

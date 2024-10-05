import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android.plugin)
    alias(libs.plugins.kps)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
}

val roomSchemaPath = "$projectDir/roomschemas"

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
        "proguard-retrofit-rules.pro",
        "proguard-room-rules.pro",
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
        val archivesName =
            "$projectName-$versionName-$versionCode-$gitHash-${gitBranch.replace("/", "_")}"
        extensions
            .getByType(BasePluginExtension::class.java)
            .archivesName
            .set(archivesName)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm.ss")
        val buildDate = LocalDateTime.now().format(formatter)
        buildConfigField("String", "BUILD_DATE", "\"$buildDate\"")
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
            isPseudoLocalesEnabled = true
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName(signingConfigName)
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        val appName = "Ex 2023"
        val appNameKey = "app_name"
        val commonVersionNameSuffix = "$gitBranch.$gitTag.$gitHash"
        val randomUserApiVer = "1.4"

        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev ($commonVersionNameSuffix)"
            resValue("string", appNameKey, "$appName Dev")
            buildConfigField(
                "String",
                "RANDOM_USER_BASE_URL",
                "\"http://randomuser.me/api/$randomUserApiVer/\""
            )
        }
        create("uat") {
            dimension = "environment"
            applicationIdSuffix = ".uat"
            versionNameSuffix = "-uat ($commonVersionNameSuffix)"
            matchingFallbacks += "debug"
            proguardFiles(*(prodProguardFiles + "proguard-rules-uat.pro"))
            resValue("string", appNameKey, "$appName UAT")
            buildConfigField(
                "String",
                "RANDOM_USER_BASE_URL",
                "\"https://randomuser.me/api/$randomUserApiVer/\""
            )
        }
        create("prod") {
            dimension = "environment"
            proguardFiles(*prodProguardFiles)
            resValue("string", appNameKey, appName)
            buildConfigField(
                "String",
                "RANDOM_USER_BASE_URL",
                "\"https://randomuser.me/api/$randomUserApiVer/\""
            )
        }
    }

    /*
     * https://developer.android.com/build/jdks#jdk-config-in-studio
     * Increase -> Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK
     * Android 14 (compileSdk=34) -> Java 17
     */
    val javaVersion = JavaVersion.VERSION_17
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }

    hilt {
        // When the project has several modules and not all included in 'app' module
        enableAggregatingTask = true
    }

    sourceSets {
        // Adds exported schema location as test app assets
        getByName("androidTest").assets.srcDir(roomSchemaPath)
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
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

// Flavor's specific implementation
//    val uatImplementation by configurations // Created by default
//    val prodDebugImplementation: Configuration by configurations.creating // Not created by default

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

    // Tests
    testImplementation(libs.androidx.junit.ktx)

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

    // Kotlinx Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.test)

    // Kotlin JSON serialization
    implementation(libs.kotlinx.serialization.json)

    // Kotlinx Serialization Converter
    implementation(libs.serialization.converter)

    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx.extension)
    ksp(libs.androidx.room.compiler)

    // Tests Room
    androidTestImplementation(libs.androidx.room.testing)

    // Coil
    implementation(libs.coil.core)
    implementation(libs.coil.compose)

    // Timber
    implementation(libs.timber)

    // Leakcanary
    debugImplementation(libs.leakcanary)

    // Tests
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockk)
    androidTestImplementation(libs.test.junit.android)

    // UI Tests
    androidTestImplementation(libs.test.ui.espresso)
}

room {
    schemaDirectory(roomSchemaPath)
}

ksp {
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    val signingConfigName = "debug"

    namespace = "com.example.example2023"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.example2023"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            signingConfig = signingConfigs.getByName(signingConfigName)
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "Example 2023 dev")
            buildConfigField("String", "RANDOM_USER_BASE_URL", "\"https://randomuser.me/api/1.2/\"")
        }
        create("qa") {
            dimension = "environment"
            signingConfig = signingConfigs.getByName(signingConfigName)
            applicationIdSuffix = ".qa"
            versionNameSuffix = "-qa"
            matchingFallbacks += "debug"
            resValue("string", "app_name", "Example 2023 QA")
            buildConfigField("String", "RANDOM_USER_BASE_URL", "\"https://randomuser.me/api/1.2/\"")

        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "RANDOM_USER_BASE_URL", "\"https://randomuser.me/api/1.2/\"")
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
        kotlinCompilerExtensionVersion = "1.5.4"
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
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // Android
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Material Design
    implementation("com.google.android.material:material:1.10.0")

    // Hilt
    val hiltVer = "2.49"
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("com.google.dagger:hilt-android:$hiltVer")
    ksp("com.google.dagger:hilt-android-compiler:$hiltVer")

    // Compose
    val composeNavVer = "2.7.5"
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.navigation:navigation-compose:$composeNavVer")

    // Compose optional - Integration with lifecycle
    val composeOptionalVer = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$composeOptionalVer")

    // Android Studio Preview support
    val toolingVer = "1.5.4"
    implementation("androidx.compose.ui:ui-tooling-preview:$toolingVer")
    debugImplementation("androidx.compose.ui:ui-tooling:$toolingVer")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    // UI Tests
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

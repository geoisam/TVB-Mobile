import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.pjs.tvbox"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.pjs.tvbox"
        minSdk = 28
        targetSdk = 36
        versionCode = getDateTimeNumber()
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.addAll(listOf("arm64-v8a"))
        }
    }

    signingConfigs {
        create("release") {
            enableV1Signing = false
            enableV2Signing = true
            enableV3Signing = false
            enableV4Signing = false
        }
    }

    /*
    lint {
        abortOnError = false
        checkReleaseBuilds = false
        baseline = file("lint-baseline.xml")
        ignoreWarnings = true
    }
    */

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    applicationVariants.all {
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName = "${applicationId}-v${versionName}-${versionCode}.apk"
        }
    }

    compileOptions {
        // isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    // implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.cn.lunar)
    implementation(libs.org.apache.commons.text)
    implementation(libs.io.github.carguo.gsyvideoplayer)

    implementation(libs.com.squareup.okhttp3)
    implementation(libs.com.squareup.okio)

    implementation(libs.io.coil3.coil)
    implementation(libs.io.coil3.compose)
    implementation(libs.io.coil3.network.okhttp)

    implementation(libs.org.commonmark)
    implementation(libs.org.commonmark.ext.image)
    implementation(libs.org.commonmark.ext.autolink)
    implementation(libs.org.commonmark.ext.footnotes)
    implementation(libs.org.commonmark.ext.ins)
    implementation(libs.org.commonmark.ext.strikethrough)
    implementation(libs.org.commonmark.ext.tables)
    implementation(libs.org.commonmark.ext.tasklist)

    // coreLibraryDesugaring(libs.desugar.jdk.libs)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

fun getDateTimeNumber(): Int {
    val currentDate = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyMMddHH")
    return currentDate.format(formatter).toInt()
}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.levelup_gamer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.levelup_gamer"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Coroutines para Firebase y operaciones asíncronas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")

    // ViewModel para Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Iconos extendidos para Material
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // Material3
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material3:material3-window-size-class:1.1.2")

    // Para snapshotFlow usado en el scroll infinito
    implementation("androidx.compose.runtime:runtime-livedata:1.6.0")

    //Dependencias CameraX
    //Para inicializar la camara
    implementation("androidx.camera:camera-core:1.3.0")

    //Permite controloar la camara en dispositivos actuales
    implementation("androidx.camera:camera-camera2:1.3.0")

    //Abre y cierra la camara automaticamente cuando la paantalla aparece o desaparece
    implementation("androidx.camera:camera-lifecycle:1.3.0")

    //Permite mostrar la camara en pantalla
    implementation("androidx.camera:camera-view:1.3.0")

    //Sirve para detectar codigos de barrar y QR en imagenes en tiempo real
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    implementation("io.coil-kt:coil-compose:2.2.2")

    implementation("com.google.accompanist:accompanist-permissions:0.33.0-alpha")

}
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

    // Agregar el plugin de Crashlytics
    id("com.google.firebase.crashlytics")

}

android {
    namespace = "com.cheiviz.triktak"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cheiviz.triktak"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.camera.viewfinder:viewfinder-core:1.5.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))

    // Firebase dependencies (sin versión cuando usas BoM)
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database-ktx")

    // Agregar Crashlytics
    implementation("com.google.firebase:firebase-crashlytics")

    // Autenticacion
    implementation("com.google.firebase:firebase-auth-ktx")

    // Anuncios
    implementation("com.google.android.gms:play-services-ads:24.8.0")

    // Firebase Servicio de Mensajes
    implementation("com.google.firebase:firebase-messaging:24.0.0")
}
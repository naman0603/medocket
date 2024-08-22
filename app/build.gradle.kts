plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fcm_kotlin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fcm_kotlin"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
      //Authentication
    implementation("com.google.firebase:firebase-auth")
      //Firestore
    implementation("com.google.firebase:firebase-firestore")
     // Cloud Messaging
    implementation("com.google.firebase:firebase-messaging:23.4.1")


    //Animation Lottie file
    implementation ("com.airbnb.android:lottie:6.3.0")
    //Spin Kit View for loaders
    implementation ("com.github.ybq:Android-SpinKit:1.4.0")
    //Firebase Storage
    implementation("com.google.firebase:firebase-storage")
    // meow bottom navigation
    implementation("com.github.qamarelsafadi:CurvedBottomNavigation:0.1.3")
    //Gson
    implementation ("com.google.code.gson:gson:2.10.1")


}
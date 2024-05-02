plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.ic_google.gms.ic_google-services")
    id("com.ic_google.firebase.crashlytics")
    id("kotlin-kapt")
}

android {
    namespace = "com.baraa.training.ecommerce"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.baraa.training.ecommerce"
        minSdk = 23
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

    buildFeatures{
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.ic_google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")


    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Firebase dependencies
    implementation(platform("com.ic_google.firebase:firebase-bom:32.8.0"))
    implementation("com.ic_google.firebase:firebase-analytics")
    implementation("com.ic_google.firebase:firebase-crashlytics")

    // third party libraries
    implementation("com.github.pwittchen:reactivenetwork-rx2:3.0.8")

    // Navigation components
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
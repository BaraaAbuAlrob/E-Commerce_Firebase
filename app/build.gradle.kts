plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.protobuf") version "0.9.4" apply true
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

        forEach {
            it.buildConfigField(
                "String",
                "clientServerId",
                "\"364834184261-5lvjvf7pt82i66bcflps3j8amjitmnoi.apps.googleusercontent.com\""
            )

            it.resValue(
                "string",
                "facebook_app_id",
                "\"2233780743625951\""
            )

            it.resValue(
                "string",
                "fb_login_protocol_scheme",
                "\"fb2233780743625951\""
            )

            it.resValue(
                "string",
                "facebook_client_token",
                "\"c98ce6e53ebcbb0c42fe3c226f4e9cb2\""
            )

            it.resValue(
                "string",
                "facebook_app_secret",
                "\"7ab35de007b68495f78c180a426deba9\""
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
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")


    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")


    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Facebook auth dependency
    implementation("com.facebook.android:facebook-android-sdk:16.0.0")


    // third party libraries
    implementation("com.github.pwittchen:reactivenetwork-rx2:3.0.8")

    // Navigation components
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")

    val lifecycle_version = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.google.protobuf:protobuf-kotlin-lite:4.26.0")

    // Material Design
    api("com.google.android.material:material:1.12.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.26.0"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}
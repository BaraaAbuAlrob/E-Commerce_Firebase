plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
    id("com.android.legacy-kapt")
    id("kotlin-parcelize")
    id("com.google.protobuf") version "0.10.0" apply true
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.baraa.training.ecommerce"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.baraa.training.ecommerce"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }

        forEach {
            it.buildConfigField(
                "String",
                "clientServerId",
                "\"364834184261-u5j7hkvu3kc54j32fen5gbdclcrdq8ep.apps.googleusercontent.com\""
            )

            it.resValue(
                "string", "facebook_app_id", "\"2233780743625951\""
            )

            it.resValue(
                "string", "fb_login_protocol_scheme", "\"fb2233780743625951\""
            )

            it.resValue(
                "string", "facebook_client_token", "\"c98ce6e53ebcbb0c42fe3c226f4e9cb2\""
            )

            it.resValue(
                "string", "facebook_app_secret", "\"7ab35de007b68495f78c180a426deba9\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        resValues = true
        dataBinding = true
        viewBinding = true
        compose = true
    }

    packaging {
        resources {
            excludes += "/google/protobuf/*.proto"
        }
    }
}
dependencies {
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.2")
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.fragment:fragment-ktx:1.8.9")

    // Datastore

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.2.0")

    // compose
    val composeBom = platform("androidx.compose:compose-bom:2024.10.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.36.0")
    implementation("com.google.accompanist:accompanist-webview:0.36.0")
    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.11.1")
    implementation("androidx.fragment:fragment-compose:1.8.9")

    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:34.17.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-functions")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.6.0")

    // Facebook auth dependency
    implementation("com.facebook.android:facebook-android-sdk:18.3.0")

    // third party libraries
    implementation("com.github.pwittchen:reactivenetwork-rx2:3.0.8")

    // Facebook Shimmer Effect
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:5.0.9")
    ksp("com.github.bumptech.glide:compiler:5.0.9")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.4.0")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.60.1")
    implementation("androidx.hilt:hilt-navigation-fragment:1.4.0")
    ksp("com.google.dagger:hilt-android-compiler:2.60.1")
    ksp("androidx.hilt:hilt-compiler:1.4.0")

    // Navigation components
    val navVersion = "2.9.8"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")

    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    // Protobuf
    implementation("com.google.protobuf:protobuf-javalite:3.25.5")
    implementation("com.google.protobuf:protobuf-kotlin-lite:3.25.5")

    // Material Design
    implementation("com.google.android.material:material:1.14.0")
    api("com.google.android.material:material:1.14.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.5"
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
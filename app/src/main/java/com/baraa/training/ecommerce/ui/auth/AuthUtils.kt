package com.baraa.training.ecommerce.ui.auth

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.baraa.training.ecommerce.BuildConfig

fun getGoogleRequestIntent(context: Activity): Intent {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.clientServerId).requestEmail().requestProfile()
        .requestServerAuthCode(BuildConfig.clientServerId).build()

    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
    googleSignInClient.signOut()
    return googleSignInClient.signInIntent

    // End getGoogleRequestIntent() method

    // Comments...

    /* I'm get the value "clientServerId" from google-services.json file (the firebase file), in this section:
    "services": {
    "appinvite_service": {
      "other_platform_oauth_client": [
        {
          "client_id": "364834184261-5lvjvf7pt82i66bcflps3j8amjitmnoi.apps.googleusercontent.com",   <<<<<<<<<<<<
          "client_type": 3
        }
      ]
    }
  }
    */

    /* I'm stored the id in the build.gradle.kts file, in this section:

    buildTypes {
    release {
        isMinifyEnabled = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
    */
    /**forEach {
    it.buildConfigField(
    "String", "clientServerId", "\"364834184261-5lvjvf7pt82i66bcflps3j8amjitmnoi.apps.googleusercontent.com\""
    )
    }
    }

    Don't forget add this features to gradle.properties (Project properties) file, the line is:
    android.defaults.buildfeatures.buildconfig=true

    then Sync and rebuild the project to create (automatically) the BuildConfig.java class
     */
}
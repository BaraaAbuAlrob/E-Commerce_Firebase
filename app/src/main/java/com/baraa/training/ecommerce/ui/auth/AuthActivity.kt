package com.baraa.training.ecommerce.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.baraa.training.ecommerce.R
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        // Initialize the Facebook SDK
        FacebookSdk.fullyInitialize()
        // Optionally enable logging of app events
        AppEventsLogger.activateApp(application)
    }
}
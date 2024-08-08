package com.baraa.training.ecommerce.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.baraa.training.ecommerce.R
import com.baraa.training.ecommerce.ui.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}
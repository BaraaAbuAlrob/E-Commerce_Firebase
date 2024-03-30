package com.baraa.training.ecommerce.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import com.baraa.training.ecommerce.data.repository.user.UserPreferenceRepository

class LoginViewModel (
    val userPrefs: UserPreferenceRepository
) : ViewModel() {
}
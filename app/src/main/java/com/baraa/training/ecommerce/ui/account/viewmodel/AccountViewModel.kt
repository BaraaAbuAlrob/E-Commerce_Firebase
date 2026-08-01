package com.baraa.training.ecommerce.ui.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baraa.training.ecommerce.data.models.user.UserDetailsModel
import com.baraa.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.baraa.training.ecommerce.data.repository.common.AppPreferenceRepository
import com.baraa.training.ecommerce.data.repository.user.UserPreferenceRepository
import com.baraa.training.ecommerce.domain.models.toUserDetailsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val appPreferenceRepository: AppPreferenceRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<UserDetailsModel?>(null)
    val userState: StateFlow<UserDetailsModel?> = _userState

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            userPreferenceRepository.getUserDetails().collectLatest { userPref ->
                if (userPref != null) {
                    _userState.value = userPref.toUserDetailsModel()
                } else {
                    _userState.value = UserDetailsModel(
                        id = "u1",
                        email = "user@ecommerce.com",
                        name = "Valued Customer"
                    )
                }
            }
        }
    }

    fun signOut(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            firebaseAuthRepository.logout()
            userPreferenceRepository.clearUserPreferences()
            appPreferenceRepository.saveLoginState(false)
            onLoggedOut()
        }
    }
}

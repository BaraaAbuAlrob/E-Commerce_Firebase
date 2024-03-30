package com.baraa.training.ecommerce.ui.login.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baraa.training.ecommerce.R
import com.baraa.training.ecommerce.data.repository.user.UserPreferenceRepositoryImplementation
import com.baraa.training.ecommerce.databinding.FragmentLoginBinding
import com.baraa.training.ecommerce.ui.login.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    val viewModel: LoginViewModel by lazy {
        LoginViewModel(userPrefs = UserPreferenceRepositoryImplementation(requireActivity()))
    }

    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        const val TAG = "LoginFragment"
    }
}
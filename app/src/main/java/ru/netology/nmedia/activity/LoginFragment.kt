package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.databinding.FragmentLoginBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.LoginViewModel

class LoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        val authViewModel by viewModels<AuthViewModel>()
        val loginViewModel: LoginViewModel by viewModels(ownerProducer = ::requireParentFragment)

        binding.userLogin.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            val userName = binding.userName.editText?.text.toString()
            val userPassword = binding.userPassword.editText?.text.toString()
            if (userName.isBlank() || userPassword.isBlank()) {
                Snackbar.make(binding.root, "Login or password is empty", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            loginViewModel.login(userName, userPassword)
            authViewModel.state.observe(viewLifecycleOwner) {
                if (authViewModel.authorized) {
                    findNavController().navigateUp()
                }
            }
        }
        return binding.root
    }
}
package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentLoginBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.LoginViewModel

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        binding.userLogin.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            val userName = binding.userName.editText?.text.toString()
            val userPassword = binding.userPassword.editText?.text.toString()
            if (userName.isBlank() || userPassword.isBlank()) {
                Snackbar.make(binding.root, "Login or password is empty", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.login(userName, userPassword)
            authViewModel.state.observe(viewLifecycleOwner) {
                if (authViewModel.authorized) {
                    findNavController().navigateUp()
                }
            }
        }
        return binding.root
    }
}
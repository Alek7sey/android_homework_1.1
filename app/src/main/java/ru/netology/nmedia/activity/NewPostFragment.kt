package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.StringProperty
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)
        arguments?.textArg?.let {
            binding.editText.setText(it)
        }
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
        binding.editText.requestFocus()
        binding.btnOk.setOnClickListener {
            val content = binding.editText.text.toString()
            if (content.isNotBlank()) {
                viewModel.changeContent(content)
                viewModel.save()
            } else {
                viewModel.clear()
                binding.editText.clearFocus()
            }
            findNavController().navigateUp()
        }
//        this.onBackPressedDispatcher.addCallback(
//            this, object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    activity?.setResult(RESULT_CANCELED, intent)
//                    activity?.finish()
//                }
//            }
//        )
        return binding.root
    }

    companion object {
        var Bundle.textArg by StringProperty
    }
}
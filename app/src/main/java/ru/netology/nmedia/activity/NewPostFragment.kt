package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.StringProperty
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg by StringProperty
    }

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> {
                    return@registerForActivityResult
                }

                Activity.RESULT_OK -> {
                    val uri = requireNotNull(it.data?.data)
                    val file = uri.toFile()

                    viewModel.setPhoto(uri, file)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)
        arguments?.textArg?.let {
            binding.editText.setText(it)
        }

        if (binding.editText.text.isNullOrBlank()) {
            binding.editText.setText(viewModel.edited.value?.content.toString())
        }
        binding.editText.requestFocus()

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.edited.value?.id == 0L) {
                        val content = binding.editText.text.toString()
                        viewModel.changeContent(content)
                    } else {
                        viewModel.clear()
                    }
                    findNavController().navigateUp()
                }
            }
        )

        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.save_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.save -> {
                            val content = binding.editText.text.toString()
                            if (content.isNotBlank()) {
                                viewModel.changeContent(content)
                                viewModel.save()
                                AndroidUtils.hideKeyboard(requireView())
                            } else {
                                viewModel.clear()
                                binding.editText.clearFocus()
                            }
                            true
                        }

                        else -> false
                    }
            },
            viewLifecycleOwner
        )

        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            if (photo == null) {
                binding.photoContainer.isGone = true
                return@observe
            }
            binding.photoContainer.isVisible = true
            binding.photo.setImageURI(photo.uri)
        }

        binding.gallery.setOnClickListener {
            ImagePicker.Builder(this)
                .galleryOnly()
                .crop()
                .maxResultSize(2048, 2048)
                .createIntent(photoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .cameraOnly()
                .crop()
                .maxResultSize(2048, 2048)
                .createIntent(photoLauncher::launch)
        }

        binding.removeAttachment.setOnClickListener {
            viewModel.clearPhoto()
        }

        return binding.root
    }

}
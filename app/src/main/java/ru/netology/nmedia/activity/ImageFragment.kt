package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.databinding.FragmentImageBinding

class ImageFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentImageBinding.inflate(layoutInflater, container, false)

        val urlAttach = "${BuildConfig.BASE_URL}/media"
        val downloadAttachUrl = "${urlAttach}/${arguments?.getString("urlAttach")}"
        Glide.with(binding.imageAttachment)
            .load(downloadAttachUrl)
            .centerCrop()
            .into(binding.imageAttachment)

        return binding.root
    }
}
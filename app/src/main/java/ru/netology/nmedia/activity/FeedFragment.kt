package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(layoutInflater, container, false)

        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onViews(post: Post) {
                viewModel.viewById(post.id)
            }

//            val editPostLauncher = registerForActivityResult(EditPostResultContract()) { result ->
//                result ?: return@registerForActivityResult
//                viewModel.changeContent(result)
//                viewModel.save()
//            }

            override fun onEdit(post: Post) {
//                editPostLauncher.launch(post.content)
//                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    }
                )
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onRunVideo(post: Post) {
                val videoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(post.linkVideo))
                startActivity(videoIntent)
            }
        })

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

//        val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
//            result ?: return@registerForActivityResult
//            viewModel.changeContent(result)
//            viewModel.save()
//        }
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        return binding.root
    }
}
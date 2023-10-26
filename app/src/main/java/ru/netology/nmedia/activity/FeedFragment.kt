package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(layoutInflater, container, false)
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                if (authViewModel.authorized) {
                    viewModel.likeById(post)
                } else {
                    findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
                }
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
                viewModel.shareById(post.id)
            }

            override fun onViews(post: Post) {
                viewModel.viewById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
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

            override fun onViewPost(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().apply { textArg = post.id.toString() })
            }

            override fun onSend(post: Post) {
                viewModel.send(post)
            }

            override fun onImage(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_imageFragment,
                    Bundle().apply { putString("urlAttach", post.attachment?.url) })
            }
        })

        binding.list.adapter = adapter

        /*@Suppress("DEPRECATION")
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }*/

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest(adapter::submitData)
            }
        }

//        viewModel.data.observe(viewLifecycleOwner) { state ->
//            binding.empty.isVisible = state.empty
//            adapter.submitList(state.posts)
//        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swipeRefreshLayout.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry) {
                        //viewModel.loadPosts()
                        adapter.refresh()
                    }
                    .show()
            }
            //  binding.progress.isVisible = state.loading
        }

        binding.retry.setOnClickListener {
            // viewModel.loadPosts()
            adapter.retry()
        }

        /* @Suppress("DEPRECATION")
         lifecycleScope.launchWhenCreated {
             adapter.loadStateFlow.collectLatest {
                 binding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
                         || it.append is LoadState.Loading
                         || it.prepend is LoadState.Loading
             }
         }*/

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swipeRefreshLayout.isRefreshing =
                        state.refresh is LoadState.Loading ||
                                state.prepend is LoadState.Loading ||
                                state.append is LoadState.Loading
                }
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            //viewModel.refreshPosts()
            adapter.refresh()
        }

        binding.fab.setOnClickListener {
            viewModel.state.observe(viewLifecycleOwner) {
                if (authViewModel.authorized) {
                    findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                } else {
                    findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
                }
            }
        }

        /*viewModel.newerCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.loadNewPosts.visibility = View.VISIBLE
                binding.loadNewPosts.text = "load new posts $it"
            } else {
                binding.loadNewPosts.visibility = View.GONE
            }
        }*/

        binding.loadNewPosts.setOnClickListener {
            viewModel.readAll()
            binding.loadNewPosts.visibility = View.GONE
        }

        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        })

        return binding.root

    }
}
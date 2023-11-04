package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.SeparatorViewItemBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.SeparatorItem
import ru.netology.nmedia.utils.clicksCount
import ru.netology.nmedia.utils.load

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onViews(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onRunVideo(post: Post) {}
    fun onViewPost(post: Post) {}
    fun onSend(post: Post) {}
    fun onImage(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallBack()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            is SeparatorItem -> R.layout.separator_view_item
            null -> error("unknown item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }

            R.layout.card_ad -> {
                val binding = CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }

            R.layout.separator_view_item -> {
                val binding = SeparatorViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SeparatorTimingHolder(binding)
            }

            else -> error("unknown item type: $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is SeparatorItem -> (holder as? SeparatorTimingHolder)?.bind(item)
            null -> error("unknown item type")
        }
    }
}

class AdViewHolder(
    private val binding: CardAdBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {
        binding.image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
    }
}

class SeparatorTimingHolder(
    private val binding: SeparatorViewItemBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(separatorItem: SeparatorItem) {
        binding.separatorTime.text = separatorItem.timing
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            likesButton.isChecked = post.likedByMe
            likesButton.text = clicksCount(post.likes)
            shareButton.text = clicksCount(post.shareCount)
            viewsButton.text = clicksCount(post.viewsCount)
            if (post.linkVideo != null) {
                groupVideo.visibility = View.VISIBLE
            } else {
                groupVideo.visibility = View.GONE
            }
            if (post.unposted == 1) {
                send.visibility = View.VISIBLE
            } else {
                send.visibility = View.GONE
            }


            val urlAvatar = "${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}"
            Glide.with(binding.avatar)
                .load(urlAvatar)
                .circleCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .timeout(10_000)
                .into(binding.avatar)

            if (post.attachment != null) {
                binding.attachment.load("${BuildConfig.BASE_URL}/media/${post.attachment.url}")
                /*val url = "http://10.0.2.2:9999/media/${post.attachment.url}"
                Glide.with(binding.attachment)
                    .load(url)
                    .timeout(10_000)
                    .centerInside()
                    .into(binding.attachment)*/
                attachment.visibility = View.VISIBLE
            } else {
                attachment.visibility = View.GONE
            }

            menu.isVisible = post.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.option_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            likesButton.setOnClickListener {
                likesButton.isChecked = !likesButton.isChecked
                onInteractionListener.onLike(post)
            }
            shareButton.setOnClickListener { onInteractionListener.onShare(post) }
            viewsButton.setOnClickListener { onInteractionListener.onViews(post) }
            groupVideo.setAllOnClickListener { onInteractionListener.onRunVideo(post) }
            root.setOnClickListener { onInteractionListener.onViewPost(post) }
            send.setOnClickListener { onInteractionListener.onSend(post) }
            attachment.setOnClickListener { onInteractionListener.onImage(post) }
        }
    }
}

fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}

class PostDiffCallBack : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}
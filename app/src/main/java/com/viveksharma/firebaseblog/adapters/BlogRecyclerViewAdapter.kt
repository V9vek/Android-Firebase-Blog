package com.viveksharma.firebaseblog.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.viveksharma.firebaseblog.R
import com.viveksharma.firebaseblog.models.Post
import com.viveksharma.firebaseblog.utils.convertedDate
import com.viveksharma.firebaseblog.utils.smartTruncate
import kotlinx.android.synthetic.main.layout_post.view.*

class BlogRecyclerViewAdapter : RecyclerView.Adapter<BlogRecyclerViewAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var differCallback = object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_post, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(post.profileImage).placeholder(ivProfileImage.drawable)
                .into(ivProfileImage)
            tvUsername.text = post.username
            tvTimestamp.text = convertedDate(post.timestamp)
            Glide.with(this).load(post.postImage).placeholder(ivPostImage.drawable)
                .into(ivPostImage)
            tvTitle.text = post.title
            tvDescription.text = post.description.smartTruncate(50)
        }
    }
}
package com.viveksharma.firebaseblog.ui.home.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.viveksharma.firebaseblog.R
import com.viveksharma.firebaseblog.models.Post
import com.viveksharma.firebaseblog.utils.convertedDate
import kotlinx.android.synthetic.main.fragment_single_post.*

class SinglePostFragment : Fragment(R.layout.fragment_single_post) {

    private val args: SinglePostFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val post = args.post
        setPostDetails(post)

        ivBackBtn.setOnClickListener {
            this.findNavController().navigateUp()
        }
    }

    private fun setPostDetails(post: Post) {
        Glide.with(this).load(post.profileImageUrl).placeholder(ivProfileImage.drawable).into(ivProfileImage)
        tvUsername.text = post.username
        tvEmail.text = post.email
        tvTimestamp.text = convertedDate(post.timestamp)
        Glide.with(this).load(post.postImageUrl).placeholder(ivPostImage.drawable).into(ivPostImage)
        tvTitle.text = post.title
        tvDescription.text = post.description
    }
}
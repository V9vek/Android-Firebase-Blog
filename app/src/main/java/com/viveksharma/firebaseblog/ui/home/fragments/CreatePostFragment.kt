package com.viveksharma.firebaseblog.ui.home.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.viveksharma.firebaseblog.R
import com.viveksharma.firebaseblog.ui.MainActivity
import com.viveksharma.firebaseblog.ui.home.HomeViewModel
import com.viveksharma.firebaseblog.utils.Constants
import com.viveksharma.firebaseblog.utils.Resource
import kotlinx.android.synthetic.main.fragment_create_post.*

class CreatePostFragment : Fragment(R.layout.fragment_create_post) {

    private lateinit var viewModel: HomeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).homeViewModel

        updatingPostImage()

        btnCreatePost.setOnClickListener {
            uploadPost()
        }

        viewModel.uploadPostState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    this.findNavController()
                        .navigate(R.id.action_createPostFragment_to_homeFragment)
                    viewModel.donePostImageUri()
                }
                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
            viewModel.donePostState()
        })

        ivBackBtn.setOnClickListener {
            Toast.makeText(activity, "Post Creation Cancelled", Toast.LENGTH_SHORT).show()
            this.findNavController().navigateUp()
            viewModel.donePostImageUri()
        }
    }

    private fun updatingPostImage() {
        ivPostImage.setOnClickListener {
            pickImageFromGallery()
        }

        viewModel.postImageUri.observe(viewLifecycleOwner, Observer {
            it?.let {
                Glide.with(this).load(it).into(ivPostImage)
            }
        })
        viewModel.profileImageUri.observe(viewLifecycleOwner, Observer {
            Glide.with(this).load(it).into(ivProfileImage)
        })
    }

    private fun uploadPost() {
        val title = etPostTitle.text.toString()
        val description = etPostDescription.text.toString()
        viewModel.uploadPostDetailsToFirestore(title, description)
    }

    private fun showProgressBar() {
        btnCreatePost.visibility = View.INVISIBLE
        createProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        btnCreatePost.visibility = View.VISIBLE
        createProgressBar.visibility = View.INVISIBLE
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.IMAGE_PICK_CODE) {
            data?.data?.let {
                viewModel.setPostImageUri(it)
            }
        }
    }

}
package com.viveksharma.firebaseblog.ui.profile

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
import com.viveksharma.firebaseblog.utils.Constants
import com.viveksharma.firebaseblog.utils.Resource
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var viewModel: ProfileViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).profileViewModel

        setImageAndUsername()

        viewModel.updateState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
            viewModel.doneUpdateState()
        })

        ivProfileImage.setOnClickListener {
            pickImageFromGallery()
        }

        ivBackBtn.setOnClickListener {
            Toast.makeText(activity, "Profile Not Saved", Toast.LENGTH_SHORT).show()
            this.findNavController().navigateUp()
            viewModel.doneProfileImageAndUsername()
        }

        btnUpdate.setOnClickListener {
            updateProfile()
        }
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
                viewModel.setImageUri(it)
            }
        }
    }

    private fun setImageAndUsername() {
        viewModel.getCurrentUserDetails()
        viewModel.profileImageUri.observe(viewLifecycleOwner, Observer {
            Glide.with(this).load(it).placeholder(ivProfileImage.drawable).into(ivProfileImage)
        })
        viewModel.profileUsername.observe(viewLifecycleOwner, Observer {
            it?.let {
                etProfileUsername.setText(it)
            }
        })
    }

    private fun updateProfile() {
        val username = etProfileUsername.text.toString()
        viewModel.updateProfile(username)
    }

    private fun showProgressBar() {
        btnUpdate.visibility = View.INVISIBLE
        updateProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        btnUpdate.visibility = View.VISIBLE
        updateProgressBar.visibility = View.INVISIBLE
    }
}
package com.viveksharma.firebaseblog.ui.auth.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.viveksharma.firebaseblog.R
import com.viveksharma.firebaseblog.ui.MainActivity
import com.viveksharma.firebaseblog.ui.auth.AuthViewModel
import com.viveksharma.firebaseblog.utils.Constants.IMAGE_PICK_CODE
import com.viveksharma.firebaseblog.utils.Constants.REQUEST_CODE_STORAGE_PERMISSION
import com.viveksharma.firebaseblog.utils.GalleryUtility
import com.viveksharma.firebaseblog.utils.Resource
import kotlinx.android.synthetic.main.fragment_register.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class RegisterFragment : Fragment(R.layout.fragment_register), EasyPermissions.PermissionCallbacks {

    private lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).authViewModel

        btnSignup.setOnClickListener {
            val username = etSignupUsername.text.toString()
            val email = etSignupEmail.text.toString()
            val password = etSignupPassword.text.toString()
            val confirmPassword = etSignupConfirmPassword.text.toString()
            viewModel.registerUser(username, email, password, confirmPassword)
        }

        btnSignupProfile.setOnClickListener {
            requestPermissions()
        }
        viewModel.profileImageUri.observe(viewLifecycleOwner, Observer {
                ivSignupProfile.setImageURI(it)
                btnSignupProfile.alpha = 0f
        })

        viewModel.registerState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                }

                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
            viewModel.doneRegisterState()
        })

        tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun requestPermissions() {
        if (GalleryUtility.hasStoragePermission(requireContext())) {
            pickImageFromGallery()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept permission",
                REQUEST_CODE_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            data?.data.let {
                viewModel.setProfileImageUri(it!!)
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        pickImageFromGallery()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun showProgressBar() {
        btnSignup.visibility = View.INVISIBLE
        signupProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        btnSignup.visibility = View.VISIBLE
        signupProgressBar.visibility = View.INVISIBLE
    }
}
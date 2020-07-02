package com.viveksharma.firebaseblog.ui.auth.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.viveksharma.firebaseblog.R
import com.viveksharma.firebaseblog.ui.MainActivity
import com.viveksharma.firebaseblog.ui.auth.AuthViewModel
import com.viveksharma.firebaseblog.utils.Resource
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyUserIsLoggedIn()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).authViewModel

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString()
            val password = etLoginPassword.text.toString()
            viewModel.loginUser(email, password)
        }

        viewModel.loginState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    this.findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }

                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
            viewModel.doneLoginState()
        })

        tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun showProgressBar() {
        btnLogin.visibility = View.INVISIBLE
        loginProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        btnLogin.visibility = View.VISIBLE
        loginProgressBar.visibility = View.INVISIBLE
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null) {
            this.findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }
}
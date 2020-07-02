package com.viveksharma.firebaseblog.ui.auth

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viveksharma.firebaseblog.models.User
import com.viveksharma.firebaseblog.repository.BlogRepository
import com.viveksharma.firebaseblog.utils.Resource
import kotlinx.coroutines.launch


class AuthViewModel(
    private val blogRepository: BlogRepository
) : ViewModel() {

    val registerState = MutableLiveData<Resource>()
    val loginState = MutableLiveData<Resource>()
    val profileImageUri = MutableLiveData<Uri>()

    fun registerUser(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) =
        viewModelScope.launch {

            registerState.postValue(Resource.Loading())
            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() && profileImageUri.value != null) {
                if (password == confirmPassword) {
                    try {
                        blogRepository.registerUser(email, password)
                        uploadImageToFirebaseStorage(username, email)
                    } catch (e: Exception) {
                        registerState.postValue(e.message?.let { Resource.Error(it) })
                    }
                } else {
                    registerState.postValue(Resource.Error("Password and Confirm Password do not match"))
                }
            } else {
                registerState.postValue(Resource.Error("Please Fill The Details or Select Photo!"))
            }
        }

    fun loginUser(email: String, password: String) = viewModelScope.launch {

        loginState.postValue(Resource.Loading())
        if (email.isNotEmpty() && password.isNotEmpty()) {
            try {
                blogRepository.loginUser(email, password)
                loginState.postValue(Resource.Success("Successfully Logged In"))
            } catch (e: Exception) {
                loginState.postValue(e.message?.let { Resource.Error(it) })
            }
        } else {
            loginState.postValue(Resource.Error("Please Fill The Details!"))
        }
    }

    private fun uploadImageToFirebaseStorage(username: String, email: String) =
        viewModelScope.launch {
            try {
                profileImageUri.value?.let {
                    val profileImageUri = blogRepository.uploadProfileImage(it)
                    saveUserToFirestoreDatabase(username, email, profileImageUri.toString())
                }
            } catch (e: Exception) {
                registerState.postValue(e.message?.let { Resource.Error(it) })
            }
        }

    private fun saveUserToFirestoreDatabase(
        username: String,
        email: String,
        profileImageUri: String
    ) = viewModelScope.launch {
        val user = User(username, email, profileImageUri)
        try {
            blogRepository.saveUserToFirestore(user)
            registerState.postValue(Resource.Success("Successfully Registered"))
        } catch (e: Exception) {
            registerState.postValue(e.message?.let { Resource.Error(it) })
        }
    }

    fun setProfileImageUri(uri: Uri) {
        profileImageUri.postValue(uri)
    }

    fun doneRegisterState() {
        registerState.postValue(null)
    }

    fun doneLoginState() {
        loginState.postValue(null)
    }
}
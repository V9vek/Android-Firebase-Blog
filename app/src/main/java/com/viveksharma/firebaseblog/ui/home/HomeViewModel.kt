package com.viveksharma.firebaseblog.ui.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.viveksharma.firebaseblog.models.Post
import com.viveksharma.firebaseblog.repository.BlogRepository
import com.viveksharma.firebaseblog.utils.Resource
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel(
    private val blogRepository: BlogRepository
) : ViewModel() {

    val uploadPostState = MutableLiveData<Resource>()
    val getPostsState = MutableLiveData<Resource>()

    val postImageUri = MutableLiveData<Uri>()
    val username = MutableLiveData<String>()
    private val email = MutableLiveData<String>()
    val profileImageUri = MutableLiveData<String>()
    var postList = MutableLiveData<ArrayList<Post>>()

    init {
        getAllPost()
    }

    fun userLogout() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
    }

    fun getCurrentUserDetails() = viewModelScope.launch {
        try {
            val currentUser = blogRepository.getCurrentlyLoggedInUserDetails()
            username.postValue(currentUser.username)
            email.postValue(currentUser.email)
            profileImageUri.postValue(currentUser.profileImageUrl)
        } catch (e: Exception) {
            Log.d(TAG, "getCurrentUserDetails: ${e.message}")
        }
    }

    fun uploadPostDetailsToFirestore(title: String, description: String) {
        uploadPostState.postValue(Resource.Loading())
        try {
            if (title.isNotEmpty() && description.isNotEmpty() && postImageUri.value != null) {
                uploadPostImageToFirebaseStorage(title, description)
            } else {
                uploadPostState.postValue(Resource.Error("Please Fill the Details or Select Image"))
            }
        } catch (e: Exception) {
            uploadPostState.postValue(e.message?.let { Resource.Error(it) })
        }
    }

    private fun uploadPostImageToFirebaseStorage(title: String, description: String) =
        viewModelScope.launch {
            try {
                postImageUri.value?.let {
                    val uploadedPostImageUri = blogRepository.uploadPostImage(it)
                    savePostToFirestoreDatabase(title, description, uploadedPostImageUri.toString())
                }
            } catch (e: Exception) {
                uploadPostState.postValue(e.message?.let { Resource.Error(it) })
            }
        }

    private fun savePostToFirestoreDatabase(
        title: String,
        description: String,
        uploadedPostImageUri: String
    ) = viewModelScope.launch {
        val post = Post(
            title,
            description,
            uploadedPostImageUri,
            email.value.toString(),
            username.value.toString(),
            profileImageUri.value.toString(),
            System.currentTimeMillis()
        )
        try {
            blogRepository.savePostToFirestore(post)
            uploadPostState.postValue(Resource.Success("Post Uploaded Successfully"))
        } catch (e: Exception) {
            uploadPostState.postValue(e.message?.let { Resource.Error(it) })
        }
    }

    private fun getAllPost() {
        getPostsState.postValue(Resource.Loading())
        try {
            postList = blogRepository.getAllPosts()
            getPostsState.postValue(Resource.Success("New Post"))
        } catch (e: Exception) {
            getPostsState.postValue(e.message?.let { Resource.Error(it) })
        }
    }

    fun setPostImageUri(uri: Uri) {
        postImageUri.value = uri
    }

    fun donePostImageUri() {
        postImageUri.postValue(null)
    }

    fun donePostState() {
        uploadPostState.postValue(null)
    }
}
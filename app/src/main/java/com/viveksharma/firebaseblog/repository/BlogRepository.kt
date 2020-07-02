package com.viveksharma.firebaseblog.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageReference
import com.viveksharma.firebaseblog.models.Post
import com.viveksharma.firebaseblog.models.User
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class BlogRepository(
    private val auth: FirebaseAuth,
    private val storageRef: StorageReference,
    private val firestoreRef: FirebaseFirestore
) {

    suspend fun loginUser(email: String, password: String): AuthResult =
        auth.signInWithEmailAndPassword(email, password).await()

    suspend fun registerUser(email: String, password: String): AuthResult =
        auth.createUserWithEmailAndPassword(email, password).await()

    suspend fun uploadProfileImage(photoUri: Uri): Uri {
        val filename = UUID.randomUUID().toString()
        val ref = storageRef.child("profileImages/$filename")
        ref.putFile(photoUri).await()                                                           //uploaded image file
        return ref.downloadUrl.await()                                                          //getting image file uri
    }

    suspend fun saveUserToFirestore(user: User) {
        val userCollectionRef = firestoreRef.collection("users")
        userCollectionRef.add(user).await()
    }

    suspend fun getCurrentlyLoggedInUserDetails(): User {
        val currentUserEmail = auth.currentUser?.email
        var currentUser: User? = null

        val userCollectionRef = firestoreRef.collection("users")
        val querySnapshot = userCollectionRef
            .whereEqualTo("email", currentUserEmail)
            .get().await()

        for (document in querySnapshot.documents) {
            currentUser = document.toObject<User>()
        }
        return currentUser!!
    }

    suspend fun uploadPostImage(uri: Uri): Uri {
        val filename = UUID.randomUUID().toString()
        val ref = storageRef.child("postImages/$filename")
        ref.putFile(uri).await()                                                        //uploading image file
        return ref.downloadUrl.await()                                                  //getting its uploaded url
    }

    suspend fun savePostToFirestore(post: Post) {
        val postCollectionRef = firestoreRef.collection("posts")
        postCollectionRef.add(post).await()
    }

    fun getAllPosts(): MutableLiveData<ArrayList<Post>> {
        val postCollectionRef = firestoreRef.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val _posts = MutableLiveData<ArrayList<Post>>()

        postCollectionRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                throw it
            }
            querySnapshot?.let {
                val postList = ArrayList<Post>()
                for (document in querySnapshot.documents) {
                    val post = document.toObject<Post>()
                    postList.add(post!!)
                }
                _posts.value = postList
            }
        }
        return _posts
    }

    suspend fun updateProfile(currentUser: User, updatedUserMap: Map<String, Any>) {
        val userCollectionRef = firestoreRef.collection("users")
        val userQuery = userCollectionRef
            .whereEqualTo("email", currentUser.email)
            .get()
            .await()

        if (userQuery.documents.isNotEmpty()) {
            for (document in userQuery.documents) {
                userCollectionRef.document(document.id).set(
                    updatedUserMap, SetOptions.merge()
                ).await()
            }
        }
    }
}









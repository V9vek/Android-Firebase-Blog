package com.viveksharma.firebaseblog.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.viveksharma.firebaseblog.R
import com.viveksharma.firebaseblog.repository.BlogRepository
import com.viveksharma.firebaseblog.ui.auth.AuthViewModel
import com.viveksharma.firebaseblog.ui.auth.AuthViewModelProviderFactory
import com.viveksharma.firebaseblog.ui.home.HomeViewModel
import com.viveksharma.firebaseblog.ui.home.HomeViewModelProviderFactory
import com.viveksharma.firebaseblog.ui.profile.ProfileViewModel
import com.viveksharma.firebaseblog.ui.profile.ProfileViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var authViewModel: AuthViewModel
    lateinit var homeViewModel: HomeViewModel
    lateinit var profileViewModel: ProfileViewModel

    lateinit var auth: FirebaseAuth
    lateinit var storageRef: StorageReference
    lateinit var firestoreRef: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        storageRef = Firebase.storage.reference
        firestoreRef = Firebase.firestore

        val blogRepository = BlogRepository(auth, storageRef, firestoreRef)

        //auth
        val authViewModelProviderFactory = AuthViewModelProviderFactory(blogRepository)
        authViewModel =
            ViewModelProvider(this, authViewModelProviderFactory).get(AuthViewModel::class.java)

        //home
        val homeViewModelProviderFactory = HomeViewModelProviderFactory(blogRepository)
        homeViewModel =
            ViewModelProvider(this, homeViewModelProviderFactory).get(HomeViewModel::class.java)

        //profile
        val profileViewModelProviderFactory = ProfileViewModelProviderFactory(blogRepository)
        profileViewModel =
            ViewModelProvider(this, profileViewModelProviderFactory).get(ProfileViewModel::class.java)
    }
}
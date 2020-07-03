package com.viveksharma.firebaseblog.ui.home.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.viveksharma.firebaseblog.R
import com.viveksharma.firebaseblog.adapters.BlogRecyclerViewAdapter
import com.viveksharma.firebaseblog.ui.MainActivity
import com.viveksharma.firebaseblog.ui.home.HomeViewModel
import com.viveksharma.firebaseblog.utils.Resource
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private lateinit var blogAdapter: BlogRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).homeViewModel

        setCurrentUserDetails()

        setAllPosts()

        ivProfile.setOnClickListener {
            this.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        ivLogout.setOnClickListener {
            confirmLogout()
        }

        fabAddPost.setOnClickListener {
            this.findNavController().navigate(R.id.action_homeFragment_to_createPostFragment)
        }
    }

    private fun setAllPosts() {
        viewModel.getPostsState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    postsProgressBar.visibility = View.INVISIBLE
                }
                is Resource.Error -> {
                    postsProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    postsProgressBar.visibility = View.VISIBLE
                }
            }
        })

        setupRecyclerView()

        viewModel.postList.observe(viewLifecycleOwner, Observer {
            it?.let {
                blogAdapter.differ.submitList(it)
            }
        })

        //OnClickListener
        blogAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("post", it)
            }
            this.findNavController().navigate(R.id.action_homeFragment_to_singlePostFragment, bundle)
        }
    }

    private fun setupRecyclerView() {
        blogAdapter = BlogRecyclerViewAdapter()
        rvPosts.apply {
            adapter = blogAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setCurrentUserDetails() {
        viewModel.getCurrentUserDetails()
        viewModel.username.observe(viewLifecycleOwner, Observer {
            tvUsername.text = it
        })
        viewModel.profileImageUri.observe(viewLifecycleOwner, Observer {
            Glide.with(this).load(it).placeholder(ivProfile.drawable).into(ivProfile)
        })
    }

    private fun confirmLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to Logout")
            .setPositiveButton("Logout") { dialog, _ ->
                viewModel.userLogout()
                this.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                dialog.cancel()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}
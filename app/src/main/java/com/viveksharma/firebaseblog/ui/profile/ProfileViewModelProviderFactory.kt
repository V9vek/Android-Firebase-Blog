package com.viveksharma.firebaseblog.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viveksharma.firebaseblog.repository.BlogRepository

class ProfileViewModelProviderFactory(
    private val blogRepository: BlogRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(blogRepository) as T
    }
}
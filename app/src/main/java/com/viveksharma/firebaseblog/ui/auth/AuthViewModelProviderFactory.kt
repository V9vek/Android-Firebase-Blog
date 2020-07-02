package com.viveksharma.firebaseblog.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viveksharma.firebaseblog.repository.BlogRepository

class AuthViewModelProviderFactory(
    private val blogRepository: BlogRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(blogRepository) as T
    }
}
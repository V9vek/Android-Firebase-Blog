package com.viveksharma.firebaseblog.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viveksharma.firebaseblog.repository.BlogRepository

class HomeViewModelProviderFactory(
    private val blogRepository: BlogRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(blogRepository) as T
    }
}
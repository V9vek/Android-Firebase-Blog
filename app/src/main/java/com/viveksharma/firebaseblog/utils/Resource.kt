package com.viveksharma.firebaseblog.utils

sealed class Resource(
    val message: String? = null
) {
    class Success(message: String) : Resource(message)
    class Error(message: String) : Resource(message)
    class Loading : Resource()
}
package com.viveksharma.firebaseblog.models

data class Post(
    val title: String = "",
    val description: String = "",
    val postImageUrl: String = "",
    val email: String = "",
    val username: String = "",
    val profileImageUrl: String = "",
    val timestamp: Long = -1
)


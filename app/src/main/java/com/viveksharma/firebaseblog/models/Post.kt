package com.viveksharma.firebaseblog.models

data class Post(
    val title: String = "",
    val description: String = "",
    val postImage: String = "",
    val email: String = "",
    val username: String = "",
    val profileImage: String = "",
    val timestamp: Long = -1
)
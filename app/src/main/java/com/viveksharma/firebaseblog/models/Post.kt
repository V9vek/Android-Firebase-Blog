package com.viveksharma.firebaseblog.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    val title: String = "",
    val description: String = "",
    val postImageUrl: String = "",
    val email: String = "",
    val username: String = "",
    val profileImageUrl: String = "",
    val timestamp: Long = -1
) : Parcelable


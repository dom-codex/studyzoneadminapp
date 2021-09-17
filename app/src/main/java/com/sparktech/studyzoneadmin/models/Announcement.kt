package com.sparktech.studyzoneadmin.models

import com.google.gson.annotations.SerializedName

data class Announcement(
    val message: String,
    val subject: String,
    val createdAt: String,
    @SerializedName("aid") val hash: String,
    val isLoading: Boolean = false
)
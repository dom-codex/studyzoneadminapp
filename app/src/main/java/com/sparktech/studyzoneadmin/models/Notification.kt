package com.sparktech.studyzoneadmin.models

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("nid") val hash: String,
    val notification: String,
    val createdAt: String,
    val subject: String,
    val isLoading:Boolean = false
)

package com.sparktech.studyzoneadmin.request_models

data class PostNotification(
    val adminId: String,
    val message: String,
    val subject: String,
    val user: String
)

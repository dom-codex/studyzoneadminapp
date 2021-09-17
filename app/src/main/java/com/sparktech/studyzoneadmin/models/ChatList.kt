package com.sparktech.studyzoneadmin.models

data class ChatList(
    val name: String,
    val email: String,
    val user: String,
    val lastMessage: String?,
    val time: String,
    val isLoading:Boolean = false
)

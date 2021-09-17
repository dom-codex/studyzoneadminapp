package com.sparktech.studyzoneadmin.socket_models

data class UpdateChatList(
    val name: String,
    val email: String,
    val sender: String,
    val group: String,
    val message: String
)

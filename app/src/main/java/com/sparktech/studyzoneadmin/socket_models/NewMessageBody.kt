package com.sparktech.studyzoneadmin.socket_models

data class NewMessageBody(
    val message: String,
    val name: String,
    val email: String,
    val sender: String,
    val group: String,
    val time:String,
    val chatId:String
)

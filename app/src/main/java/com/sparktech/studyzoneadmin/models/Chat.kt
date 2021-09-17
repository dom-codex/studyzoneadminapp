package com.sparktech.studyzoneadmin.models


data class Chat(
    val chatId: String,
    val message: String,
    val time: String,
    val sender: String,
    val messageType: String,
    val file:String?,
    val groupChat:String
)
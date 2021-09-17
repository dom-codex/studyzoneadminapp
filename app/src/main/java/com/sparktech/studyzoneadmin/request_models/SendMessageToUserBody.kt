package com.sparktech.studyzoneadmin.request_models

data class SendMessageToUserBody(
    val message:String,
    val group:String,
    val sender:String,
    val time:String,
    val adminId:String,
    val receiver:String
)
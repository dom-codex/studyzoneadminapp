package com.sparktech.studyzoneadmin.response_models

import com.sparktech.studyzoneadmin.models.Chat

data class GetChatsResponse(val code:Int,val message:String?,val chats:List<Chat>)
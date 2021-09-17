package com.sparktech.studyzoneadmin.response_models

import com.sparktech.studyzoneadmin.models.ChatList

data class GetChatListResponse(val chatlist: List<ChatList>, val code: Int, val message: String?)

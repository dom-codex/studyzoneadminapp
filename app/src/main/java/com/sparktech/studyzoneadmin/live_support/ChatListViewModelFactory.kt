package com.sparktech.studyzoneadmin.live_support

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatListViewModelFactory(val app:Application,val admin:String):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ChatListViewModel::class.java)){
            return ChatListViewModel(app,admin) as T
        }
        throw  Exception("invalid class")
    }
}
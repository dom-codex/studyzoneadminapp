package com.sparktech.studyzoneadmin.live_support

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.Exception

class ChatsViewModelFactory(private val application: Application,private val group:String,val admin:String):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ChatsViewModel::class.java)){
            return ChatsViewModel(application,group,admin) as T
        }
        throw Exception("invalid class")
    }
}
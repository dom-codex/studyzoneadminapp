package com.sparktech.studyzoneadmin.notification

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NotificationViewModelFactory(val app:Application,val admin:String):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NotificationViewModel::class.java)){
            return NotificationViewModel(app,admin) as T
        }
        throw Exception("invalid class")
    }
}
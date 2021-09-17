package com.sparktech.studyzoneadmin.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.Exception

class SettingsViewModelFactory(private val app:Application,private val admin:String):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            return SettingsViewModel(app,admin) as T
        }
        throw Exception("invalid class")
    }
}
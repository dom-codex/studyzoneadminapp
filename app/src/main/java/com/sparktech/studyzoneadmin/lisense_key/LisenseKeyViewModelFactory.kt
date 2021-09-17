package com.sparktech.studyzoneadmin.lisense_key

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LisenseKeyViewModelFactory(val app:Application,val admin:String):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LisenseKeyViewModel::class.java)){
            return LisenseKeyViewModel(app,admin) as T
        }
        throw Exception("invalid class")
    }
}
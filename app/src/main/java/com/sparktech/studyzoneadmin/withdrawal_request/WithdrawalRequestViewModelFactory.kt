package com.sparktech.studyzoneadmin.withdrawal_request

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WithdrawalRequestViewModelFactory(val app:Application,val admin:String):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(WithdrawalRequestViewModel::class.java)){
            return WithdrawalRequestViewModel(app,admin) as T
        }
        throw Exception("invalid class passed in")
    }
}
package com.sparktech.studyzoneadmin.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sparktech.studyzoneadmin.models.Settings
import com.sparktech.studyzoneadmin.network.Network
import com.sparktech.studyzoneadmin.request_models.UpdateSettingsRequestBody
import kotlinx.coroutines.*

class SettingsViewModel(val app: Application, val admin: String) : AndroidViewModel(app) {
    private val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    val settings = mutableListOf<Settings>()
    init {
        fetchSettings()
    }
    fun fetchSettings() {
        _isLoading.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.getSettings(admin)
                withContext(Dispatchers.Main) {
                    if (res.code == 200) {
                        settings.addAll(res.settings)
                    }
                    _isLoading.value = false
                }
            } catch (e: retrofit2.HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }
    fun updateSettings(body:UpdateSettingsRequestBody,cb:(err:Boolean)->Unit){
        networkScope.launch {
            try{
                val res = Network.apiService.updateSettings(body)
                if(res.code==200){
                    updateSettingsList(body.name,body.value)
                    cb(false)
                }
            }catch (e:retrofit2.HttpException){
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    cb(true)
                }
            }
        }
    }
    fun updateSettingsList(name:String,value:String){
        val index = settings.indexOfFirst {
            it.name == name
        }
        var setting = settings[index]
        setting = setting.copy(value = value)
        settings.add(index,setting)
    }
}
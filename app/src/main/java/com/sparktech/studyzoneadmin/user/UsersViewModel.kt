package com.sparktech.studyzoneadmin.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sparktech.studyzoneadmin.models.Download
import com.sparktech.studyzoneadmin.models.Referral
import com.sparktech.studyzoneadmin.models.Transaction
import com.sparktech.studyzoneadmin.models.User
import com.sparktech.studyzoneadmin.network.Network
import kotlinx.coroutines.*

class UsersViewModel : ViewModel() {
    private val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)
    private val _isLoading = MutableLiveData(false)
    private val _isLoadingTransactions = MutableLiveData(false)
    private val _isLoadingReferral = MutableLiveData(false)
    private val _isLoadingDownloads = MutableLiveData(false)
    val isLoadingReferral:LiveData<Boolean>
    get() = _isLoadingReferral
    val isLoadingTransaction:LiveData<Boolean>
    get() = _isLoadingTransactions
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    val isLoadingDownloads:LiveData<Boolean>
    get() = _isLoadingDownloads
    val users = mutableListOf<User>()
    val usersTransaction = mutableListOf<Transaction>()
    val userReferral = mutableListOf<Referral>()
    val userDownloads = mutableListOf<Download>()
    fun fetchSchool(adminId: String, page: Int = 0) {
        try {
            _isLoading.value = true
            networkScope.launch {
                val res = Network.apiService.getUsers(adminId, page)
                withContext(Dispatchers.Main) {
                    users.addAll(res.users)
                    _isLoading.value = false
                }
            }
        } catch (e: Exception) {
            _isLoading.value = false
            e.printStackTrace()
        }
    }
    fun fetchUserTransactions(adminId: String,user:String,page:Int=0){
        try{
           _isLoadingTransactions.value = true
            val options = HashMap<String,String>()
            options["adminId"] = adminId
            options["user"] = user
           networkScope.launch {
               val res = Network.apiService.getUserTransactions(page,options)
               withContext(Dispatchers.Main){
                  usersTransaction.addAll(res.transactions)
                   _isLoadingTransactions.value = false
               }
           }
        }catch (e:Exception){
            e.printStackTrace()
            _isLoadingTransactions.value = false
        }
    }
    fun fetchUserReferral(adminId: String,user: String,page: Int=0){
        try{
            _isLoadingReferral.value = true
            val options  = HashMap<String,String>()
            options.put("adminId",adminId)
            options.put("user",user)
            networkScope.launch {
                val res = Network.apiService.getUserReferrals(page,options)
                withContext(Dispatchers.Main){
                    userReferral.addAll(res.referrals)
                    _isLoadingReferral.value = false
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
            _isLoadingReferral.value = false
        }
    }
    fun fetchUserDownloads(adminId: String,user: String,page: Int = 0){
        try{
            val options = HashMap<String,String>()
            options["adminId"] = adminId
            options["user"] = user
            _isLoadingDownloads.value = true
            networkScope.launch {
                val res = Network.apiService.getUserDownloads(page,options)
                withContext(Dispatchers.Main){
                    userDownloads.addAll(res.downloads)
                    _isLoadingDownloads.value = false
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
            _isLoadingDownloads.value = false
        }
    }
    override fun onCleared() {
        super.onCleared()

    }
}
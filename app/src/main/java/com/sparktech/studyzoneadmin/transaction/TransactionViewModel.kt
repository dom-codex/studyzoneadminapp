package com.sparktech.studyzoneadmin.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sparktech.studyzoneadmin.models.Transaction
import com.sparktech.studyzoneadmin.network.Network
import kotlinx.coroutines.*

class TransactionViewModel: ViewModel() {
    private val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)
    private val _isLoading = MutableLiveData(false)
    val isLoading:LiveData<Boolean>
    get() = _isLoading
    val transactions = mutableListOf<Transaction>()
    fun getTransactions(adminId:String,page:Int){
        try{
            _isLoading.value = true
            networkScope.launch {
                val res = Network.apiService.getTransactions(adminId,page)
                withContext(Dispatchers.Main){
                    transactions.addAll(res.transactions)
                    _isLoading.value = false
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
            _isLoading.value = false
        }
    }
}
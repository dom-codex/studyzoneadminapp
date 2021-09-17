package com.sparktech.studyzoneadmin.transaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sparktech.studyzoneadmin.helpers.Constants
import com.sparktech.studyzoneadmin.helpers.showToast
import com.sparktech.studyzoneadmin.models.Transaction
import com.sparktech.studyzoneadmin.network.Network
import kotlinx.coroutines.*

class TransactionViewModel(private val app: Application) : AndroidViewModel(app) {
    private val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    var filter = ""
    val transactions = HashMap<String, MutableList<Transaction>>()
    val indicators = HashMap<String, MutableLiveData<Boolean>>()
    val pages = HashMap<String, Int>()
    fun getTransactions(adminId: String) {
        indicators[filter]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.getTransactions(adminId, filter, pages[filter]!!)
                val index = transactions[filter]!!.indexOfFirst {
                    it.isLoading
                }
                if (index >= 0) transactions[filter]!!.removeAt(index)
                val itemsToAdd = listFilter(res.transactions)
                transactions[filter]!!.addAll(itemsToAdd)
                withContext(Dispatchers.Main) {
                    if ((pages[filter]!! + 1) * Constants.SERVER_LIMIT <= transactions[filter]!!.size) {
                        pages[filter] = pages[filter]!! + 1
                    }

                    if (itemsToAdd.isEmpty()) showToast(
                        app.applicationContext,
                        "you have reached the end of this list"
                    )

                    indicators[filter]!!.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators[filter]!!.value = false
                    showToast(app.applicationContext, "an error occurred")
                }
            }
        }

    }

    private suspend fun listFilter(newList: List<Transaction>): List<Transaction> {
        return withContext(Dispatchers.IO) {
            val itemsToAdd = mutableListOf<Transaction>()
            newList.forEach {
                val alreadyAdded = transactions[filter]!!.contains(it)
                if (!alreadyAdded) {
                    itemsToAdd.add(it)
                }
            }
            itemsToAdd
        }
    }

    /*fun filterTransactions(filter: String, callBack: (filtered: MutableList<Transaction>) -> Unit) {
        filteredTransactions.clear()
        if (filter != "ALL") {
            networkScope.launch {
                transactions.forEach {
                    if (it.paymentMethod == filter.toLowerCase(Locale.ROOT)) filteredTransactions.add(
                        it
                    )
                }
                withContext(Dispatchers.Main) {
                    callBack(filteredTransactions)
                }
            }
        } else {
            callBack(transactions)
        }

    }*/
}
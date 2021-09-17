package com.sparktech.studyzoneadmin.withdrawal_request

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sparktech.studyzoneadmin.helpers.Constants
import com.sparktech.studyzoneadmin.models.WithdrawalRequest
import com.sparktech.studyzoneadmin.network.Network
import com.sparktech.studyzoneadmin.request_models.UpdateWithdrawalStatusRequestBody
import kotlinx.coroutines.*

class WithdrawalRequestViewModel(val app: Application, val admin: String) : AndroidViewModel(app) {
    val job = Job()
    val networkScope = CoroutineScope(Dispatchers.IO + job)
    val requests = HashMap<String, MutableList<WithdrawalRequest>>()
    val indicators = HashMap<String, MutableLiveData<Boolean>>()
    val pages = HashMap<String, Int>()
    fun fetchRequest(query: HashMap<String, String>, cb: (err: Boolean, msg: String?) -> Unit) {
        indicators["loading_${query["status"]}"]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.getWithdrawalRequests(query)
                val index = requests[query["status"]]!!.indexOfFirst {
                    it.isLoading
                }
                if (index >= 0) requests[query["status"]]!!.removeAt(index)

                if (res.requests.isNotEmpty()) {
                    val itemsToAdd = listFilter(query["status"]!!, res.requests)
                    requests["${query["status"]}"]!!.addAll(itemsToAdd)
                    withContext(Dispatchers.Main) {
                        if ((pages["${query.get("status")}_page"]!! + 1) * Constants.SERVER_LIMIT <= requests[query["status"]]!!.size) {
                            pages["${query.get("status")}_page"] =
                                pages["${query.get("status")}_page"]!! + 1
                        }
                        cb(false, null)
                    }
                } else {
                    cb(false, "end of list")
                }
                withContext(Dispatchers.Main) {
                    indicators["loading_${query["status"]}"]!!.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                when (e) {
                    is retrofit2.HttpException -> {
                    }
                    is java.net.SocketTimeoutException -> {
                    }
                }
                withContext(Dispatchers.Main) {
                    indicators["loading_${query["status"]}"]!!.value = false
                    cb(true, null)
                }
            }
        }
    }

    fun updateRequestStatus(body: UpdateWithdrawalStatusRequestBody) {
        indicators["updating_PENDING"]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.updateWithdrawalRequest(body)
                val index = requests["PENDING"]!!.indexOfFirst {
                    it.hash == res.withdrawalId
                }
                //get the request object
                var request = requests["PENDING"]!![index]
                //remove the request from the former list
                requests["PENDING"]!!.removeAt(index)
                //update the request
                request = request.copy(status = res.status)
                //add the request to the new list
                requests[body.status]!!.add(request)
                //notify adapter of this changes
                withContext(Dispatchers.Main) {
                    indicators["updating_PENDING"]!!.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators["updating_PENDING"]!!.value = false
                }
            }
        }
    }

    private suspend fun listFilter(
        type: String,
        newList: List<WithdrawalRequest>
    ): List<WithdrawalRequest> {
        return withContext(Dispatchers.Main) {
            val itemsToAdd = mutableListOf<WithdrawalRequest>()
            newList.forEach {
                val alreadyAdded = requests[type]!!.contains(it)
                if (!alreadyAdded) {
                    itemsToAdd.add(it)
                }
            }
            itemsToAdd
        }
    }
}
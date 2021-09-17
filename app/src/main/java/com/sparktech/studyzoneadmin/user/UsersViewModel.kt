package com.sparktech.studyzoneadmin.user

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sparktech.studyzoneadmin.helpers.KeyComposer
import com.sparktech.studyzoneadmin.models.Download
import com.sparktech.studyzoneadmin.models.Referral
import com.sparktech.studyzoneadmin.models.Transaction
import com.sparktech.studyzoneadmin.models.User
import com.sparktech.studyzoneadmin.network.Network
import com.sparktech.studyzoneadmin.request_models.PostNotification
import com.sparktech.studyzoneadmin.request_models.ToggleUserStatus
import kotlinx.coroutines.*

class UsersViewModel : ViewModel() {
    private val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)
    private val _isLoading = MutableLiveData(false)
    private val _isLoadingTransactions = MutableLiveData(false)
    private val _isLoadingReferral = MutableLiveData(false)
    private val _isLoadingDownloads = MutableLiveData(false)
    private val _isPosting = MutableLiveData(false)

    //hashes
    val indicators = HashMap<String, MutableLiveData<Boolean>>()
    val pages = HashMap<String, Int>()
    val users = HashMap<String, MutableList<User>>()

    //hashes for user details
    val transactions = HashMap<String, MutableList<Transaction>>()

    // private val _isLoadingAllUsers = MutableLiveData(false)
    // private val _isLoadingActivatedUsers = MutableLiveData(false)
    // private val _isLoadingBlockedUsers = MutableLiveData(false)
    //private val _isLoadingNotActivatedUsers = MutableLiveData(false)
    //  val isLoadingAllUsers: LiveData<Boolean>
    //     get() = _isLoadingAllUsers
    // val isLoadingActivatedUsers: LiveData<Boolean>
    //  get() = _isLoadingActivatedUsers
    //val isLoadingBlockedUsers: LiveData<Boolean>
    //   get() = _isLoadingBlockedUsers
    //val isLoadingNotActivatedUsers: LiveData<Boolean>
    //    get() = _isLoadingNotActivatedUsers
    val isLoadingReferral: LiveData<Boolean>
        get() = _isLoadingReferral
    val isLoadingTransaction: LiveData<Boolean>
        get() = _isLoadingTransactions
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    val isLoadingDownloads: LiveData<Boolean>
        get() = _isLoadingDownloads
    val posting: LiveData<Boolean>
        get() = _isPosting

    //val users = mutableListOf<User>()
    val activatedUsers = mutableListOf<User>()
    val blockedUsers = mutableListOf<User>()
    val notActivatedUsers = mutableListOf<User>()

    //val usersTransaction = mutableListOf<Transaction>()
    val userReferral = HashMap<String, MutableList<Referral>>()
    val userDownloads = mutableListOf<Download>()


    fun fetchUsers(
        adminId: String,
        type: String,
        page: Int,
        cb: (err: Boolean, msg: String?) -> Unit
    ) {
        indicators["loading$type"]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.getUsers(adminId, type, page)
                if (pages[type]!! > 0) {
                    users[type]!!.removeAt(users[type]!!.size - 1)
                }
                if (res.users.isNotEmpty()) {
                    users[type]!!.addAll(res.users)
                    withContext(Dispatchers.Main) {
                        pages[type] = pages[type]!! + 1
                        cb(false, null)
                    }
                } else {
                    cb(false, "no more users to fetch")
                }
                withContext(Dispatchers.Main) {
                    indicators["loading$type"]!!.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    indicators["loading$type"]!!.value = false
                    cb(true, null)
                }
                e.printStackTrace()
                when (e) {
                    is retrofit2.HttpException -> {
                    }
                    is java.net.SocketTimeoutException -> {

                    }
                }
            }
        }
    }

    fun fetchUserTransactions(adminId: String, user: String, bundle: Bundle, page: Int = 0,cb:(err:Boolean,msg:String?)->Unit) {
        val transactionKey = KeyComposer.getUserDetailsTransactionsKey(bundle)
        val loadingKey = KeyComposer.getUserDetailsLoadingTransactions(bundle)
        val transactionPage = KeyComposer.getUserCurrentTransactionPage(bundle)
        try {
            indicators[loadingKey]!!.value = true
            val options = HashMap<String, String>()
            options["adminId"] = adminId
            options["user"] = user
            networkScope.launch {
                val res = Network.apiService.getUserTransactions(page, options)
                if (pages[transactionPage]!! > 0) {
                    transactions[transactionKey]!!.removeAt(
                        transactions[transactionKey]!!.size - 1
                    )

                }
                if(res.transactions.isNotEmpty()){

                    transactions[transactionKey]!!.addAll(res.transactions)
                    cb(false,null)
                }else{
                    cb(false,"end of list")
                }
                withContext(Dispatchers.Main) {
                    indicators[loadingKey]!!.value = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            indicators[loadingKey]!!.value = false
            cb(true,null)
        }
    }

    fun fetchUserReferral(adminId: String, user: String, bundle: Bundle, page: Int = 0,cb: (err: Boolean, msg: String?) -> Unit) {
        val referralKey = KeyComposer.getUserDetailsReferralKey(bundle)
        val referralLoadingKey = KeyComposer.getUserDetailsLoadingReferrals(bundle)
        val referralPage = KeyComposer.getUserCurrentReferralPage(bundle)
        try {
            _isLoadingReferral.value = true
            val options = HashMap<String, String>()
            options.put("adminId", adminId)
            options.put("user", user)
            networkScope.launch {
                val res = Network.apiService.getUserReferrals(page, options)
                if (pages[referralPage]!! > 0) {
                    userReferral[referralKey]!!.removeAt(userReferral[referralKey]!!.size - 1)
                }
                if(res.referrals.isNotEmpty()){
                    userReferral[referralKey]!!.addAll(res.referrals)
                    cb(false,null)
                }else{
                    cb(false,"end of list")
                }
                withContext(Dispatchers.Main) {

                    indicators[referralLoadingKey]!!.value = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            indicators[referralLoadingKey]!!.value = false
            cb(true,null)
        }
    }

    fun fetchUserDownloads(adminId: String, user: String, page: Int = 0) {
        try {
            val options = HashMap<String, String>()
            options["adminId"] = adminId
            options["user"] = user
            _isLoadingDownloads.value = true
            networkScope.launch {
                val res = Network.apiService.getUserDownloads(page, options)
                withContext(Dispatchers.Main) {
                    userDownloads.addAll(res.downloads)
                    _isLoadingDownloads.value = false
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _isLoadingDownloads.value = false
        }
    }

    fun postNotification(body: PostNotification) {
        _isPosting.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.postNotificationUser(body)
                if (res.code == 200) {
                }
                _isPosting.value = false
            } catch (e: retrofit2.HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _isPosting.value = false
                }
            }
        }
    }

    fun toggleUserStatus(body: ToggleUserStatus) {
        _isPosting.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.toggleUserStatus(body)
                if (res.code == 200) {

                }
                _isPosting.value = false
            } catch (e: retrofit2.HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _isPosting.value = false
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

    }
}
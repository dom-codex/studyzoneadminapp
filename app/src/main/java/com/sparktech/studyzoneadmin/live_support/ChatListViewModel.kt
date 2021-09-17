package com.sparktech.studyzoneadmin.live_support

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sparktech.studyzoneadmin.helpers.Constants
import com.sparktech.studyzoneadmin.models.ChatList
import com.sparktech.studyzoneadmin.network.Network
import kotlinx.coroutines.*

class ChatListViewModel(val app: Application, val admin: String) : AndroidViewModel(app) {
    private val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)
    private val _isLoading = MutableLiveData(false)
    private val _nchats = MutableLiveData(false)
    val nchats: LiveData<Boolean>
        get() = _nchats
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    val chatLists = mutableListOf<ChatList>()

    init {
        val query = HashMap<String, String>()
        query["adminId"] = admin
        query["page"] = "0"
        fetchChatList(query)
    }

    var currentChatListPage = 0
    fun fetchChatList(query: HashMap<String, String>) {
        _isLoading.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.getChatList(query)
                removeLoader()
                val itemsToAdd = listFilter(res.chatlist)
                chatLists.addAll(itemsToAdd)
                withContext(Dispatchers.Main)
                {
                    if ((currentChatListPage + 1) * Constants.SERVER_LIMIT <= chatLists.size) {
                        currentChatListPage += 1

                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }

    private fun removeLoader() {
        val index = chatLists.indexOfFirst {
            it.isLoading
        }
        if (index >= 0) {
            chatLists.removeAt(index)
        }
    }

    private suspend fun listFilter(newList: List<ChatList>): List<ChatList> {
        return withContext(Dispatchers.IO) {
            val itemsToAdd = mutableListOf<ChatList>()
            newList.forEach {
                val alreadyAdded = chatLists.contains(it)
                if (!alreadyAdded) {
                    itemsToAdd.add(it)
                }
            }
            itemsToAdd
        }
    }

    suspend fun findChatListUser(sender: String): Int {
        return withContext(Dispatchers.IO) {
            chatLists.indexOfFirst {
                it.user == sender
            }
        }
    }
}
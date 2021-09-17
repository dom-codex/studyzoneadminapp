package com.sparktech.studyzoneadmin.live_support

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sparktech.studyzoneadmin.helpers.Constants
import com.sparktech.studyzoneadmin.models.Chat
import com.sparktech.studyzoneadmin.network.Network
import com.sparktech.studyzoneadmin.request_models.SendMessageToUserBody
import kotlinx.coroutines.*

class ChatsViewModel(val app: Application, val group: String, val admin: String) :
    AndroidViewModel(app) {
    private val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        val query = HashMap<String, String>()
        query["page"] = "0"
        query["adminId"] = admin
        query["group"] = group
        fetchChats(query)
    }

    var currentChatPage = 0
    val chats = mutableListOf<Chat>()
    fun fetchChats(query: HashMap<String, String>) {
        _isLoading.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.loadChats(query)
                val itemsToAdd = listFilter(res.chats)
                chats.addAll(itemsToAdd)
                withContext(Dispatchers.Main) {
                    if ((currentChatPage + 1) * Constants.SERVER_LIMIT <= chats.size) {
                        currentChatPage += 1
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

    fun sendMessage(chat: SendMessageToUserBody, cb: (err: Boolean, chatId: String) -> Unit) {
        networkScope.launch {
            try {
                val res = Network.apiService.sendMessageToUser(chat)
                withContext(Dispatchers.Main) {
                    if (res.code == 200) {
                        cb(false, res.chatId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    cb(false, "")
                }
            }
        }
    }

    private suspend fun listFilter(newChats: List<Chat>): List<Chat> {
        return withContext(Dispatchers.IO) {
            val itemsToAdd = mutableListOf<Chat>()
            newChats.forEach {
                val alreadyAdded = chats.contains(it)
                if (!alreadyAdded) {
                    itemsToAdd.add(it)
                }
            }
            itemsToAdd
        }
    }
}
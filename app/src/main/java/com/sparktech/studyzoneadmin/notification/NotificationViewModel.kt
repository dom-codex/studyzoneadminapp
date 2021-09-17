package com.sparktech.studyzoneadmin.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sparktech.studyzoneadmin.helpers.Constants
import com.sparktech.studyzoneadmin.models.Announcement
import com.sparktech.studyzoneadmin.models.Notification
import com.sparktech.studyzoneadmin.network.Network
import com.sparktech.studyzoneadmin.request_models.DefaultRequestBody
import com.sparktech.studyzoneadmin.request_models.PostNotification
import kotlinx.coroutines.*

class NotificationViewModel(val app: Application, val admin: String) : AndroidViewModel(app) {
    val job = Job()
    val networkScope = CoroutineScope(Dispatchers.Main + job)
    private val _loading = MutableLiveData(false)
    private val _isDeleting = MutableLiveData<Boolean>(null)
    private val _sending = MutableLiveData(false)
    val sending :LiveData<Boolean>
    get() = _sending
    val loading: LiveData<Boolean>
        get() = _loading
    val isDeleting: LiveData<Boolean>
        get() = _isDeleting
    val notifications = mutableListOf<Notification>()
    private var notificationPage = 0

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        _loading.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.getNotifications(admin, notificationPage)
                val index = notifications.indexOfFirst {
                    it.isLoading
                }
                if(index>=0)notifications.removeAt(index)
                val itemsToAdd = listFilter(res.notifications)
                notifications.addAll(itemsToAdd)
                withContext(Dispatchers.Main) {
                    if ((notificationPage + 1) * Constants.SERVER_LIMIT <= notifications.size) {
                        notificationPage += 1
                    }

                }
                _loading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    _loading.value = false
                }
            }
        }
    }

    fun deleteNotification(index: Int, cb: (err: Boolean, msg: String?) -> Unit) {
        val notification = notifications[index]
        val body = DefaultRequestBody(notification.hash, admin)
        _isDeleting.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.deleteNotification(body)
                cb(false, "deleted")
                notifications.removeAt(index)
                _isDeleting.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _isDeleting.value = false
                    cb(true, "failed")
                }
            }
        }
    }
    //ANNOUNCEMENT SECTION
    val announcements = mutableListOf<Announcement>()
    private val _isLoadingAnnouncement = MutableLiveData(false)
    private val _deletingAnnouncement = MutableLiveData<Boolean>(null)
    val deletingAnnouncement:LiveData<Boolean>
    get() = _deletingAnnouncement
    val loadingAnnouncement:LiveData<Boolean>
    get() = _isLoadingAnnouncement
    private var announcementPage = 0

    fun fetchAnnouncement(){
        _isLoadingAnnouncement.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.getAnnouncement(admin, announcementPage)
                val index = announcements.indexOfFirst {
                    it.isLoading
                }
                if(index>=0)announcements.removeAt(index)
                val itemsToAdd =announcementFilter(res.announcements)
                announcements.addAll(itemsToAdd)
                withContext(Dispatchers.Main) {
                    if ((announcementPage + 1) * Constants.SERVER_LIMIT <= announcements.size) {
                        announcementPage += 1
                    }

                }
                _isLoadingAnnouncement.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    _isLoadingAnnouncement.value = false
                }
            }
        }
    }
    fun deleteAnnouncement(index:Int, cb:(err:Boolean, msg:String?)->Unit){
        val notification = announcements[index]
        val body = DefaultRequestBody(notification.hash, admin)
        _isDeleting.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.deleteAnnouncement(body)
                cb(false, "deleted")
                announcements.removeAt(index)
                _isDeleting.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _isDeleting.value = false
                    cb(true, "failed")
                }
            }
        }
    }
fun sendAnnouncement(subject:String,message:String){
    val body = PostNotification(admin,message,subject,"")
    _sending.value = true
    networkScope.launch {
        try{
            val res = Network.apiService.sendAnnouncement(body)
            announcements.add(res.announcement)
            withContext(Dispatchers.Main){
                _sending.value = false
            }
        }catch (e:Exception){
            e.printStackTrace()
            withContext(Dispatchers.Main){
                _sending.value = false
            }
        }
    }
}
    private suspend fun listFilter(newList: List<Notification>): MutableList<Notification> {
        return withContext(Dispatchers.IO) {
            val itemsToAdd = mutableListOf<Notification>()
            newList.forEach {
                val isAdded = notifications.contains(it)
                if (!isAdded) {
                    itemsToAdd.add(it)
                }
            }
            itemsToAdd
        }
    }
    private suspend fun announcementFilter(newList: List<Announcement>): MutableList<Announcement> {
        return withContext(Dispatchers.IO) {
            val itemsToAdd = mutableListOf<Announcement>()
            newList.forEach {
                val isAdded = announcements.contains(it)
                if (!isAdded) {
                    itemsToAdd.add(it)
                }
            }
            itemsToAdd
        }
    }
}
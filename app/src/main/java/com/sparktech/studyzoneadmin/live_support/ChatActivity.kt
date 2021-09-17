package com.sparktech.studyzoneadmin.live_support

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.ActivityChatBinding
import com.sparktech.studyzoneadmin.helpers.MessageType
import com.sparktech.studyzoneadmin.models.Chat
import com.sparktech.studyzoneadmin.request_models.SendMessageToUserBody
import com.sparktech.studyzoneadmin.socket_models.NewMessageBody
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.util.*
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {
    private val ADMIN = "ADMIN"
    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter
    private lateinit var msocket: Socket
    private lateinit var vm: ChatsViewModel
    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        init()
        setUpListeners()

    }

    private fun init() {
        val user = intent.getStringExtra("user")
        val sp = this.getSharedPreferences("AdminDetails",Context.MODE_PRIVATE)
        val admin = sp.getString("adminId","")
        val fac = ChatsViewModelFactory(application, "${ADMIN}_${user}",admin!!)
        vm = ViewModelProvider(this, fac).get(ChatsViewModel::class.java)
        adapter = ChatAdapter()
        binding.chatRcv.adapter = adapter
        msocket = IO.socket("http://10.0.2.2:4500")
        msocket.on(Socket.EVENT_CONNECT, onConnectListener)
        msocket.on("newMessage", onNewMessageListener)
        msocket.connect()
        setUpObservers()
    }
    private fun setUpObservers(){
      vm.isLoading.observe(this,{loading->
          loading?.let {
              if(!it){
                  adapter.submitList(vm.chats)
                  adapter.notifyDataSetChanged()
                  binding.chatRcv.scrollToPosition(vm.chats.size - 1)
              }else{
                  //show spinner
              }
          }
      })
    }
    private fun setUpListeners() {
        val sp = this.getSharedPreferences("AdminDetails",Context.MODE_PRIVATE)
        val admin = sp.getString("adminId","")
        binding.chatSwipe.setOnRefreshListener {
            val query = HashMap<String,String>()
            val user = intent.getStringExtra("user")

            query["page"] = vm.currentChatPage.toString()
            query["adminId"] = admin!!
            query["group"] = "${ADMIN}_$user"
            vm.fetchChats(query)
            binding.chatSwipe.isRefreshing = false
        }
        binding.sendMessageBtn.setOnClickListener {
            //get message input
            val message = binding.messageInput.text.toString()
            if (!message.minLength(1)) {
                Toast.makeText(this, "enter message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val user = intent.getStringExtra("user")
            println("user $user")
            val time = "12:00"
            val chat = Chat(
                UUID.randomUUID().toString(),
                message,
                time,
                "ADMIN",
                MessageType.SENDER,
                null,
                "${ADMIN}_${user}"
            )
            vm.chats.add(chat)
            adapter.notifyItemInserted(vm.chats.size - 1)
            binding.chatRcv.scrollToPosition(vm.chats.size - 1)
            binding.messageInput.text = null
            val body = SendMessageToUserBody(chat.message,chat.groupChat,"ADMIN",chat.time,admin!!,user!!)
            //sender message to server
            vm.sendMessage(body){err,chatId:String->
                if(!err){
                }
            }
        }
    }

    private val onConnectListener = Emitter.Listener {
        val user = intent.getStringExtra("user")
        val group = "${ADMIN}_${user}"
        //emit socket.join
        msocket.emit("join", group)
    }
    private val onNewMessageListener = Emitter.Listener {
        val jsonData = it[0].toString()
        val data = gson.fromJson(jsonData, NewMessageBody::class.java)
        val chat = Chat(
            data.chatId,
            data.message,
            data.time,
            data.sender,
            MessageType.RECEIVER,
            null,
            data.group
        )
        vm.chats.add(chat)
        runOnUiThread {
            adapter.notifyDataSetChanged()
            binding.chatRcv.scrollToPosition(vm.chats.size-1)
            //notify adapter
        }
    }
}
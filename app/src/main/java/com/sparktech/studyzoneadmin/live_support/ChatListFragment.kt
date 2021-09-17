package com.sparktech.studyzoneadmin.live_support

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.ChatListLayoutBinding
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.models.ChatList
import com.sparktech.studyzoneadmin.socket_models.UpdateChatList
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatListFragment : Fragment() {
    private val ADMIN_BASE = "ADMIN"
    private lateinit var binding: ChatListLayoutBinding
    private lateinit var adapter: ChatListAdapter
    private lateinit var vm: ChatListViewModel
    private lateinit var msocket: Socket
    private val gson = Gson()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_list_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val fac = ChatListViewModelFactory(requireActivity().application, admin!!)
        vm = ViewModelProvider(requireActivity(), fac).get(ChatListViewModel::class.java)
        adapter = ChatListAdapter(itemClickHandler)
        binding.chatListRcv.adapter = adapter
        binding.chatListRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.chatListRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchMoreChatLists
            )
        )
        msocket = IO.socket("http://10.0.2.2:4500")
        msocket.on(Socket.EVENT_CONNECT, onConnectListener)
        msocket.on("onUpdateChatlist", onUpDateChatList)
        msocket.on("joined",joinedListener)
        msocket.connect()
        setUpObservers()
    }
    private val joinedListener = Emitter.Listener {
        activity?.let{
            it.runOnUiThread {
                Toast.makeText(requireContext(),"joined group",Toast.LENGTH_LONG).show()
                println("joined")
            }
        }
    }
    //scroll lambdas
    private val getLoadingState: () -> Boolean = {
        vm.isLoading.value!!
    }
    private val getListSize: () -> Int = {
        vm.chatLists.size
    }
    private val setLoaderItem: () -> Unit = {
        val loaderItem = ChatList("", "", "", "", "", true)
        vm.chatLists.add(loaderItem)
        adapter.notifyItemInserted(vm.chatLists.size - 1)
    }
    private val fetchMoreChatLists = {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val adminId = sp.getString("adminId", "")
        val query = HashMap<String, String>()
        query["adminId"] = adminId!!
        query["page"] = vm.currentChatListPage.toString()
        vm.fetchChatList(query)
    }
    private val onConnectListener = Emitter.Listener {
        //join room
        //..implement on the server
         msocket.emit("joinUserGroup","${ADMIN_BASE}")
    }
    private val onUpDateChatList = Emitter.Listener {
        val jsonData = it[0].toString()
        val data = gson.fromJson(jsonData, UpdateChatList::class.java)
        updateRecyclerView(data)
    }

    private fun updateRecyclerView(data: UpdateChatList) {
        CoroutineScope(Dispatchers.Main).launch {
            val index = vm.findChatListUser(data.sender)
            if(index >=0) vm.chatLists.removeAt(index)
            val chatuser = ChatList(data.name, data.email, data.sender, data.message, "",false)
            vm.chatLists.add(0, chatuser)
            adapter.notifyDataSetChanged()
        }

    }

    private val itemClickHandler: (user: String) -> Unit = {
        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra("user", it)
        }
        requireContext().startActivity(intent)
    }

    private fun setUpObservers() {
        vm.isLoading.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.chatLoader.visibility = View.GONE
                    if(vm.chatLists.size > 0){
                        binding.noChatList.visibility = View.GONE
                        binding.chatListRcv.visibility = View.VISIBLE
                        adapter.submitList(vm.chatLists)
                        adapter.notifyDataSetChanged()
                        return@let
                    }
                    binding.noChatList.visibility = View.VISIBLE
                } else {
                    //show spinner
                    if(vm.chatLists.size == 0 ){
                        binding.chatListRcv.visibility = View.GONE
                        binding.noChatList.visibility = View.GONE
                        binding.chatLoader.visibility = View.VISIBLE
                    }
                }
            }
        })
        vm.nchats.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    if (vm.chatLists.size > 0) {
                        binding.liveChatValue.text = vm.chatLists.size.toString()
                    }
                } else {
                    //show spinner
                }
            }
        })
    }
}
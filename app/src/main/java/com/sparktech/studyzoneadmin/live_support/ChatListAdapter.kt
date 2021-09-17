package com.sparktech.studyzoneadmin.live_support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.databinding.ChatListRcvItemBinding
import com.sparktech.studyzoneadmin.databinding.LoadingRcvBinding
import com.sparktech.studyzoneadmin.models.ChatList
private const val ITEM_VIEW = 0
private const val  LOADER_ITEM = 1
class ChatListAdapter(val itemClickHandler:(user:String)->Unit):ListAdapter<ChatList,RecyclerView.ViewHolder>(diffUtil) {
    inner  class ViewHolder(val binding:ChatListRcvItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(chatList: ChatList){
            println("chatlist $chatList")
            binding.chatListEmail.text = chatList.email
            binding.chatListName.text = chatList.name
            binding.chatListMessage.text = chatList.lastMessage
            binding.root.setOnClickListener {
                itemClickHandler(chatList.user)
            }
        }
    }
    companion object{
        val diffUtil = object:DiffUtil.ItemCallback<ChatList>(){
            override fun areItemsTheSame(oldItem: ChatList, newItem: ChatList): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ChatList, newItem: ChatList): Boolean {
                return oldItem.user == newItem.user
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
         super.getItemViewType(position)
        val data = getItem(position)
        if(data.isLoading){
           return LOADER_ITEM
        }
        return ITEM_VIEW
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if(viewType == ITEM_VIEW) {
            return ViewHolder(ChatListRcvItemBinding.inflate(inflater, parent, false))
        }
        return LoaderViewHolder(LoadingRcvBinding.inflate(inflater,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatUser = getItem(position)
        if(holder is ViewHolder){
            holder.bind(chatUser)
        }

    }
}
class LoaderViewHolder(private val binding:LoadingRcvBinding):RecyclerView.ViewHolder(binding.root){}
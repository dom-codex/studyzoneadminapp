package com.sparktech.studyzoneadmin.live_support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.databinding.ReceiverMediaItemBinding
import com.sparktech.studyzoneadmin.databinding.ReceiverRcvItemBinding
import com.sparktech.studyzoneadmin.databinding.SenderMediaItemBinding
import com.sparktech.studyzoneadmin.databinding.SenderRcvItemBinding
import com.sparktech.studyzoneadmin.helpers.MessageType
import com.sparktech.studyzoneadmin.models.Chat

private const val SENDER = 1
private const val RECEIVER = 2
private const val SENDER_WITH_MEDIA = 3
private const val RECEIVER_WITH_MEDIA = 4

class ChatAdapter : ListAdapter<Chat, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Chat>() {
            override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.chatId == newItem.chatId
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chat = getItem(position)
        return when (chat.messageType) {
            MessageType.SENDER -> return SENDER
            MessageType.SENDER_WITH_MEDIA -> return SENDER_WITH_MEDIA
            MessageType.RECEIVER -> return RECEIVER
            MessageType.RECEIVER_WITH_MEDIA -> return RECEIVER_WITH_MEDIA
            else -> super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SENDER -> SenderViewHolder(SenderRcvItemBinding.inflate(inflater, parent, false))
            RECEIVER -> ReceiverViewHolder(ReceiverRcvItemBinding.inflate(inflater, parent, false))
            SENDER_WITH_MEDIA -> SenderWithMediaViewHolder(
                SenderMediaItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            RECEIVER_WITH_MEDIA -> ReceiverWithMediaViewHolder(
                ReceiverMediaItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> SenderViewHolder(SenderRcvItemBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = getItem(position)
        when (holder) {
            is SenderViewHolder -> holder.bind(chat)
            is ReceiverViewHolder -> holder.bind(chat)
            is SenderWithMediaViewHolder -> holder.bind(chat)
            is ReceiverWithMediaViewHolder -> holder.bind(chat)
        }
    }
}

class SenderViewHolder(val binding: SenderRcvItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(chat: Chat) {
        binding.apply {
            chatMsg.text = chat.message
            chatTime.text = chat.time
        }
    }
}

class ReceiverViewHolder(val binding: ReceiverRcvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(chat: Chat) {
        binding.apply {
            chatMsgReceiver.text = chat.message
            chatTimeReceiver.text = chat.time
        }
    }
}

class SenderWithMediaViewHolder(val binding: SenderMediaItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(chat: Chat) {}
}

class ReceiverWithMediaViewHolder(val binding: ReceiverMediaItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(chat: Chat) {}
}
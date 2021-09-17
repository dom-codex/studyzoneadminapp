package com.sparktech.studyzoneadmin.annnouncement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.databinding.LoadingRcvBinding
import com.sparktech.studyzoneadmin.databinding.NotificationRcvLayoutBinding
import com.sparktech.studyzoneadmin.models.Announcement
import com.sparktech.studyzoneadmin.models.Notification

private const val LOADER_ITEM = 0
private const val ITEM_VIEW = 1
class AnnouncementAdapter: ListAdapter<Announcement, RecyclerView.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding: NotificationRcvLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(announcement: Announcement){
            binding.apply {
                notificationBody.text = announcement.message
                notificationSubject.text =announcement.subject
                notificationDate.text = announcement.createdAt
            }
        }
    }
    companion object{
        val diffUtil = object: DiffUtil.ItemCallback<Announcement>(){
            override fun areItemsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
                return oldItem.hash == newItem.hash
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
        if(viewType == ITEM_VIEW){
            return ViewHolder(NotificationRcvLayoutBinding.inflate(inflater,parent,false))
        }
        return LoaderViewHolder(LoadingRcvBinding.inflate(inflater,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ViewHolder){
            val notification = getItem(position)
            holder.bind(notification)
        }
    }
}
class LoaderViewHolder(val binding: LoadingRcvBinding): RecyclerView.ViewHolder(binding.root)
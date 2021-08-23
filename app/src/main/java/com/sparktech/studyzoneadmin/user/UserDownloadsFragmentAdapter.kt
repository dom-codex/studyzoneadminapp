package com.sparktech.studyzoneadmin.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.databinding.TabItemPqBinding
import com.sparktech.studyzoneadmin.models.Download

class UserDownloadsFragmentAdapter:ListAdapter<Download,UserDownloadsFragmentAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding:TabItemPqBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(download: Download){
            binding.apply {
                pqTitle.text = "${download.startYear}/${download.endYear} ${download.title} pastquestion"
                pqLevel.text = download.level
                deptPq.text = download.dept
                semesterPq.text = download.semester
                pqSch.text = download.sch
            }
        }
    }
    companion object{
        val diffUtil =object:DiffUtil.ItemCallback<Download>(){
            override fun areItemsTheSame(oldItem: Download, newItem: Download): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Download, newItem: Download): Boolean {
                return oldItem.hash == newItem.hash
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(TabItemPqBinding.inflate(inflater,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val download = getItem(position)
        holder.bind(download)
    }
}
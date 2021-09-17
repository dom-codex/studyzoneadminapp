package com.sparktech.studyzoneadmin.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.LoadingRcvBinding
import com.sparktech.studyzoneadmin.databinding.SchoolRcvItemBinding
import com.sparktech.studyzoneadmin.models.School
import com.sparktech.studyzoneadmin.models.SchoolAdapterData
private const val LOADER_VIEW = 0
private const val ITEM_VIEW = 1
class SchoolAdapter:ListAdapter<SchoolAdapterData,RecyclerView.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding:SchoolRcvItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(school: School?){
            school?.let {
                binding.schName.text = it.name
                binding.schImg.setImageResource(R.drawable.ic_baseline_school_24)
                binding.createdAt.text = it.createdAt
                binding.schAbbr.text = it.nameAbbr
                binding.root.setOnClickListener { v->
                    val bundle = Bundle()
                    bundle.putString("schName",it.name)
                    bundle.putString("faculties",it.faculties)
                    bundle.putString("schHash",it.schHash)
                    v.findNavController().navigate(R.id.action_schoolFragment_to_schoolDetails,bundle)
                }
            }
        }
    }
    inner class LoadingViewHolder(val binding:LoadingRcvBinding):RecyclerView.ViewHolder(binding.root){}
    companion object{
        val diffUtil = object:DiffUtil.ItemCallback<SchoolAdapterData>(){
            override fun areItemsTheSame(oldItem: SchoolAdapterData, newItem: SchoolAdapterData): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: SchoolAdapterData, newItem: SchoolAdapterData): Boolean {
                return oldItem.school?.schHash == newItem.school?.schHash
            }

        }

        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if(viewType == ITEM_VIEW){
            return ViewHolder(SchoolRcvItemBinding.inflate(inflater,parent,false))
        }
        return LoadingViewHolder(LoadingRcvBinding.inflate(inflater,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //check instance of view holder
        val data = getItem(position)
        if(holder is ViewHolder){
            holder.bind(data.school)
        }
    }

    override fun getItemViewType(position: Int): Int {
         super.getItemViewType(position)
        val data = getItem(position)
        if(data.isLoading){
            return LOADER_VIEW
        }
        return ITEM_VIEW
    }
}

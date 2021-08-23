package com.sparktech.studyzoneadmin.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.SchoolDetailsItemBinding
import com.sparktech.studyzoneadmin.models.SchoolDetails

class SchoolDetailsAdapter(val sch:String,val schName:String):ListAdapter<SchoolDetails,SchoolDetailsAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding:SchoolDetailsItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(details: SchoolDetails){
            binding.sN.text = adapterPosition.toString()
            binding.facultyName.text = details.name
            binding.facultyCreatedAt.text = details.createdAt
        }
    }
    companion object{
        val diffUtil = object :DiffUtil.ItemCallback<SchoolDetails>(){
            override fun areItemsTheSame(oldItem: SchoolDetails, newItem: SchoolDetails): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: SchoolDetails,
                newItem: SchoolDetails
            ): Boolean {
                return oldItem.hash == newItem.hash
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(SchoolDetailsItemBinding.inflate(inflater,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val details = getItem(position)
        holder.bind(details)
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("fid",details.hash)
            bundle.putString("sch",sch)
            bundle.putString("schName",schName)
            bundle.putString("departments",details.departments)
            bundle.putString("fn",details.name)
            it.findNavController().navigate(R.id.action_schoolDetails_to_schoolDepartmentFragment,bundle)
        }
    }
}
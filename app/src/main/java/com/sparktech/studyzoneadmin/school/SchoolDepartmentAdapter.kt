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
import com.sparktech.studyzoneadmin.databinding.SchoolDetailsItemBinding
import com.sparktech.studyzoneadmin.models.Department
private const val ITEM_VIEW = 0
private const val LOADER_VIEW = 1
class SchoolDepartmentAdapter(val sch:SchInfo):ListAdapter<Department,RecyclerView.ViewHolder>(diffUtil) {
    inner  class ViewHolder(val binding:SchoolDetailsItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(department: Department){
            binding.facultyName.text = department.name
            binding.facultyCreatedAt.text = department.createdAt
        }
    }
    companion object{
        val diffUtil = object:DiffUtil.ItemCallback<Department>(){
            override fun areItemsTheSame(oldItem: Department, newItem: Department): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Department, newItem: Department): Boolean {
                return oldItem.hash == newItem.hash
            }
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if(viewType== ITEM_VIEW) {
            return ViewHolder(SchoolDetailsItemBinding.inflate(inflater, parent, false))
        }
        return LoaderViewHolder(LoadingRcvBinding.inflate(inflater,parent,false))
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder){
            val department = getItem(position)
            holder.bind(department)
            holder.itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("schName",sch.schName)
                bundle.putString("schHash",sch.sid)
                bundle.putString("facultyName",sch.faculty)
                bundle.putString("facultyHash",sch.fid)
                bundle.putString("dept",department.name)
                bundle.putString("did",department.hash)
                it.findNavController().navigate(R.id.action_schoolDepartmentFragment_to_schoolLevelFragment,bundle)
            }
        }

    }
}
class LoaderViewHolder(private val binding:LoadingRcvBinding):RecyclerView.ViewHolder(binding.root){}
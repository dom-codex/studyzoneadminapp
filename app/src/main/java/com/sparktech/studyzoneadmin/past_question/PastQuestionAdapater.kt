package com.sparktech.studyzoneadmin.past_question

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.databinding.LoadingRcvBinding
import com.sparktech.studyzoneadmin.databinding.SchoolPastQuestionRcvItemBinding
import com.sparktech.studyzoneadmin.models.PastQuestion

private const val LOADER_ITEM = 0
private const val ITEM_VIEW = 1
class PastQuestionAdapter(val handler:(pid:String)->Unit):ListAdapter<PastQuestion,RecyclerView.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding:SchoolPastQuestionRcvItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(pastQuestion: PastQuestion){
            binding.pqName.text = "${pastQuestion.startYear}/${pastQuestion.endYear} ${pastQuestion.title} past question"
            binding.pqSemester.text = "createdAt ${pastQuestion.createdAt}"
            binding.pqDeleteBtn.setOnClickListener {
                handler(pastQuestion.hash)
            }

        }
    }
    companion object{
        val diffUtil  = object:DiffUtil.ItemCallback<PastQuestion>(){
            override fun areItemsTheSame(oldItem: PastQuestion, newItem: PastQuestion): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: PastQuestion, newItem: PastQuestion): Boolean {
                return oldItem.hash == newItem.hash
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val data = getItem(position)
        if(data.isLoading){
            return LOADER_ITEM
        }
        return ITEM_VIEW
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if(viewType== LOADER_ITEM){
            return LoaderViewHolder(LoadingRcvBinding.inflate(inflater,parent,false))
        }
        return ViewHolder(SchoolPastQuestionRcvItemBinding.inflate(inflater,parent,false))
    }

    override fun onBindViewHolder(holder:RecyclerView.ViewHolder, position: Int) {
        val pastQuestion = getItem(position)
        if(holder is ViewHolder){
            holder.bind(pastQuestion)
        }

    }
}
class LoaderViewHolder(val binding:LoadingRcvBinding):RecyclerView.ViewHolder(binding.root){}
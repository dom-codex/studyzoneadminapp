package com.sparktech.studyzoneadmin.past_question

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.databinding.SchoolPastQuestionRcvItemBinding
import com.sparktech.studyzoneadmin.models.PastQuestion

class PastQuestionAdapter():ListAdapter<PastQuestion,PastQuestionAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding:SchoolPastQuestionRcvItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(pastQuestion: PastQuestion){
            binding.pqName.text = "${pastQuestion.startYear}/${pastQuestion.endYear} ${pastQuestion.title} past question"
            binding.pqSemester.text = "createdAt ${pastQuestion.createdAt}"

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(SchoolPastQuestionRcvItemBinding.inflate(inflater,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pastQuestion = getItem(position)
        holder.bind(pastQuestion)
    }
}
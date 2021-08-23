package com.sparktech.studyzoneadmin.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.databinding.TabItemPersonBinding
import com.sparktech.studyzoneadmin.models.Referral

class UserReferralAdapter:ListAdapter<Referral,UserReferralAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding:TabItemPersonBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(referral:Referral){
            binding.apply {
                personName.text = referral.name
                personEmail.text = referral.email
                personPhone.text = referral.phone
                personReferredDate.text = "referred on ${referral.createdAt}"
            }
        }
    }
    companion object{
        val diffUtil = object:DiffUtil.ItemCallback<Referral>(){
            override fun areItemsTheSame(oldItem: Referral, newItem: Referral): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Referral, newItem: Referral): Boolean {
                return oldItem.email == newItem.email
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater  = LayoutInflater.from(parent.context)
        return ViewHolder(TabItemPersonBinding.inflate(inflater,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val referral = getItem(position)
        holder.bind(referral)
    }
}
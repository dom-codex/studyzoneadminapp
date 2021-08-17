package com.sparktech.studyzoneadmin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.databinding.KeysRowRcvItemBinding
import com.sparktech.studyzoneadmin.models.LisenseKey

class KeysAdapter:ListAdapter<LisenseKey,KeysAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding:KeysRowRcvItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(lisenseKey: LisenseKey){
            binding.apply {
                keyVal.text = lisenseKey.key
                priceVal.text = lisenseKey.price.toString()
                vendorVal.text = lisenseKey.vendor
                isUsedVal.text = lisenseKey.isUsed.toString()
                usedVal.text = lisenseKey.usedBy
            }
        }
    }
    companion object{
        val diffUtil = object:DiffUtil.ItemCallback<LisenseKey>(){
            override fun areItemsTheSame(oldItem: LisenseKey, newItem: LisenseKey): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: LisenseKey, newItem: LisenseKey): Boolean {
                return false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(KeysRowRcvItemBinding.inflate(inflater,parent,false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = getItem(position)
        holder.bind(key)
    }
}
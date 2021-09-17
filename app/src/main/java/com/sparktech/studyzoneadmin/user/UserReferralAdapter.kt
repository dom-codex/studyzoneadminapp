package com.sparktech.studyzoneadmin.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.databinding.LoadingRcvBinding
import com.sparktech.studyzoneadmin.databinding.TabItemPersonBinding
import com.sparktech.studyzoneadmin.models.Referral

private const val ITEM_VIEW = 0
private const val LOADER_VIEW = 1

class UserReferralAdapter : ListAdapter<Referral, RecyclerView.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding: TabItemPersonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(referral: Referral) {
            binding.apply {
                personName.text = referral.name
                personEmail.text = referral.email
                personPhone.text = referral.phone
                personReferredDate.text = "referred on ${referral.createdAt}"
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Referral>() {
            override fun areItemsTheSame(oldItem: Referral, newItem: Referral): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Referral, newItem: Referral): Boolean {
                return oldItem.email == newItem.email
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        val data = getItem(position)
        if (data.isLoading) {
            return LOADER_VIEW
        }
        return ITEM_VIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == ITEM_VIEW) {
            return ViewHolder(TabItemPersonBinding.inflate(inflater, parent, false))
        }
        return LoaderViewHolder(LoadingRcvBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val referral = getItem(position)
        if (holder is ViewHolder) {
            holder.bind(referral)
        }
    }
}

class LoaderViewHolder(val binding: LoadingRcvBinding) : RecyclerView.ViewHolder(binding.root) {}
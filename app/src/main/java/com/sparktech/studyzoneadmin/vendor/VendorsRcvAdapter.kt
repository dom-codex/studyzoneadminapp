package com.sparktech.studyzoneadmin.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.LoadingRcvBinding
import com.sparktech.studyzoneadmin.databinding.VendorRcvItemLayoutBinding
import com.sparktech.studyzoneadmin.models.Vendor

private const val LOADER_VIEW = 0
private const val ITEM_VIEW = 1

class VendorsRcvAdapter : ListAdapter<Vendor, RecyclerView.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding: VendorRcvItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(vendor: Vendor) {
            binding.apply {
                vendorName.text = vendor.name
                vendorId.text = vendor.vendorId
                vendorSn.text = (adapterPosition + 1).toString()
                binding.root.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("vendorId",vendor.vendorId)
                    it.findNavController().navigate(R.id.action_vendorFragment_to_vendorStatisticsFragment,bundle)
                }
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Vendor>() {
            override fun areItemsTheSame(oldItem: Vendor, newItem: Vendor): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Vendor, newItem: Vendor): Boolean {
                return oldItem.vendorId == newItem.vendorId
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
            return ViewHolder(VendorRcvItemLayoutBinding.inflate(inflater, parent, false))
        }
        return LoaderViewHolder(LoadingRcvBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val data = getItem(position)
            holder.bind(data)
        }
    }
}

class LoaderViewHolder(private val binding: LoadingRcvBinding) :
    RecyclerView.ViewHolder(binding.root)
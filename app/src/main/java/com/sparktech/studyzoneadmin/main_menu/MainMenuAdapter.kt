package com.sparktech.studyzoneadmin.main_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.MainMenuRcvItemBinding

class MainMenuAdapter :
    ListAdapter<com.sparktech.studyzoneadmin.models.Categories, MainMenuAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding: MainMenuRcvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categories: com.sparktech.studyzoneadmin.models.Categories) {
            binding.sectionTitle.text = categories.name
            binding.sectionImg.setImageResource(categories.icon)
        }
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<com.sparktech.studyzoneadmin.models.Categories>() {
                override fun areItemsTheSame(
                    oldItem: com.sparktech.studyzoneadmin.models.Categories,
                    newItem: com.sparktech.studyzoneadmin.models.Categories
                ): Boolean {
                    return oldItem === newItem
                }

                override fun areContentsTheSame(
                    oldItem: com.sparktech.studyzoneadmin.models.Categories,
                    newItem: com.sparktech.studyzoneadmin.models.Categories
                ): Boolean {
                    return oldItem.id == newItem.id
                }

            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(MainMenuRcvItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categories = getItem(position)
        holder.bind(categories)
        holder.itemView.setOnClickListener {
            when (categories.name) {
                "Universities" -> {
                    val bundle = Bundle()
                    bundle.putString("schoolType", categories.name)
                    it.findNavController()
                        .navigate(R.id.action_mainMenuFragment_to_schoolFragment, bundle)
                }
                "Polytechnics" -> {
                    val bundle = Bundle()
                    bundle.putString("schoolType", categories.name)
                    it.findNavController()
                        .navigate(R.id.action_mainMenuFragment_to_schoolFragment, bundle)
                }
                "Notifications" -> {
                    it.findNavController()
                        .navigate(R.id.action_mainMenuFragment_to_notificationFragment)
                }
                "Announcements" -> it.findNavController()
                    .navigate(R.id.action_mainMenuFragment_to_annoucementFragment)
                "Transactions" -> {
                    it.findNavController()
                        .navigate(R.id.action_mainMenuFragment_to_transactionsFragment)
                }
                "LisenseKeys"->it.findNavController().navigate(R.id.action_mainMenuFragment_to_lisenseKeyFragment)
                "Vendors"->it.findNavController().navigate(R.id.action_mainMenuFragment_to_vendorFragment)
                "Students" -> {
                    it.findNavController().navigate(R.id.action_mainMenuFragment_to_userFragment)
                }
                "Withdrawal Requests" -> it.findNavController()
                    .navigate(R.id.withdrawalRequestFragment)
                "Support" -> it.findNavController()
                    .navigate(R.id.action_mainMenuFragment_to_chatListFragment)
                "Settings" -> it.findNavController()
                    .navigate(R.id.action_mainMenuFragment_to_settingsFragment)
            }
        }
    }
}
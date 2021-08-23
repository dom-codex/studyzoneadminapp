package com.sparktech.studyzoneadmin.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.UserExpandableGroupBinding
import com.sparktech.studyzoneadmin.models.User

class UsersAdapter : ListAdapter<User,UsersAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding: UserExpandableGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.nameUser.text = user.name
            binding.emailUser.text = user.email
            binding.phoneUser.text = user.phone
            binding.userDetailsInclude.nEarnings.text = user.totalEarned.toString()
            binding.userDetailsInclude.nReferrals.text = user.noOfReferral.toString()
            binding.userDetailsInclude.nTransactions.text = user.transactions.toString()
            binding.userDetailsInclude.detailsBtn.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("user",user.uid)
                bundle.putString("name",user.name)
                 bundle.putString("email",user.email)
                bundle.putString("phone",user.phone)
                 bundle.putString("earnings",user.totalEarned.toString())
                bundle.putBoolean("loggedIn",user.isLoggedIn)
                 bundle.putBoolean("activated",user.isActivated)
                it.findNavController().navigate(R.id.action_userFragment_to_userDetails,bundle)
            }
            binding.root.setOnClickListener {
                val visibility = binding.userDetailsInclude.root.visibility
                if(visibility == View.GONE){
                    binding.userDetailsInclude.root.visibility = View.VISIBLE
                }else{
                    binding.userDetailsInclude.root.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.uid == newItem.uid
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(UserExpandableGroupBinding.inflate(inflater,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }
}

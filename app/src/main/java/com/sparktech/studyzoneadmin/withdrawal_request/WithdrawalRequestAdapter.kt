package com.sparktech.studyzoneadmin.withdrawal_request

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.VideoPlayer
import com.sparktech.studyzoneadmin.databinding.LoadingRcvBinding
import com.sparktech.studyzoneadmin.databinding.WithdrawalRequestsRcvItemBinding
import com.sparktech.studyzoneadmin.models.WithdrawalRequest
private const val ITEM_VIEW = 0
private const val LOADER_ITEM = 1
class WithdrawalRequestAdapter(val updateHandler:(pid:String,status:String)->Unit,val context:Context,val type:String) :
    ListAdapter<WithdrawalRequest, RecyclerView.ViewHolder>(
        diffUtil
    ) {

    inner class ViewHolder(val binding: WithdrawalRequestsRcvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(request: WithdrawalRequest) {
            binding.apply {
                this.requesteeName.text = request.requesteeName
                this.requesteeEmail.text = request.requesteeEmail
                this.requestedDate.text = request.createdAt
                this.requestedAmount.text = request.amount.toString()
                binding.root.setOnClickListener {
                    if(type!="PENDING"){
                        return@setOnClickListener
                    }
                    if (withdrawalRequestController.root.visibility == View.GONE) {
                        withdrawalRequestController.root.visibility = View.VISIBLE
                    } else {
                        withdrawalRequestController.root.visibility = View.GONE
                    }
                }
                binding.withdrawalRequestController.testimonyLink.text = request.videoLink?:"N/A"
                binding.withdrawalRequestController.approveBtn.setOnClickListener {
                    updateHandler(request.hash,"APPROVED")
                }
                binding.withdrawalRequestController.declineBtn.setOnClickListener {
                    updateHandler(request.hash,"DECLINED")
                }
                binding.withdrawalRequestController.testimonyLink.setOnClickListener {
                    val link = binding.withdrawalRequestController.testimonyLink.text.toString()
                    if(link != "N/A"){
                       val intent = Intent(context,VideoPlayer::class.java).apply {
                           putExtra("uri",link)
                       }
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<WithdrawalRequest>() {
            override fun areItemsTheSame(
                oldItem: WithdrawalRequest,
                newItem: WithdrawalRequest
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: WithdrawalRequest,
                newItem: WithdrawalRequest
            ): Boolean {
                return oldItem.hash == newItem.hash
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
         super.getItemViewType(position)
        val data  =getItem(position)
        if(data.isLoading){
            return LOADER_ITEM
        }
        return ITEM_VIEW
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if(viewType == ITEM_VIEW){
        return ViewHolder(WithdrawalRequestsRcvItemBinding.inflate(inflater, parent, false))
        }
        return LoaderViewHolder(LoadingRcvBinding.inflate(inflater,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position<currentList.size){
            val request = getItem(position)
            if(holder is ViewHolder){
                holder.bind(request)
            }
        }


    }
}
class LoaderViewHolder(private val binding:LoadingRcvBinding):RecyclerView.ViewHolder(binding.root){}
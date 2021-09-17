package com.sparktech.studyzoneadmin.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.databinding.LoadingRcvBinding
import com.sparktech.studyzoneadmin.databinding.TransactionTableItemBinding
import com.sparktech.studyzoneadmin.models.Transaction
private const val LOADER_ITEM = 0
private const val ITEM_VIEW = 1
class TransactionsAdapter:ListAdapter<Transaction,RecyclerView.ViewHolder>(diffUtil) {
    inner class ViewHolder(val binding:TransactionTableItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(transaction: Transaction){
            binding.amountValue.text = transaction.amount.toString()
            binding.department.text = transaction.department?.name
            binding.faculty.text = transaction.faculty?.name
            binding.keyUsed.text = transaction.key
            binding.level.text = transaction.level?.level
            binding.method.text = transaction.paymentMethod
            binding.school.text = transaction.school?.name
            binding.semester.text = transaction.semester
            binding.transactionId.text = transaction.userTxId
            binding.transactionRef.text = transaction.transactionRef
            binding.transactionTitle.text = transaction.title
            binding.year.text = transaction.createdAt
            binding.userEmail.text = transaction.userEmail
        }
    }
    companion object{
        val diffUtil = object:DiffUtil.ItemCallback<Transaction>(){
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.userTxId ==newItem.userTxId
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        val data = getItem(position)
        if(data.isLoading){
            return LOADER_ITEM
        }
        return ITEM_VIEW
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if(viewType == ITEM_VIEW){
        return  ViewHolder(TransactionTableItemBinding.inflate(inflater,parent,false))
        }else{
            return LoaderViewHolder(LoadingRcvBinding.inflate(inflater,parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val transaction = getItem(position)
        if(holder is ViewHolder){
            holder.bind(transaction)
        }

    }
}
class LoaderViewHolder(private val binding:LoadingRcvBinding):RecyclerView.ViewHolder(binding.root){}
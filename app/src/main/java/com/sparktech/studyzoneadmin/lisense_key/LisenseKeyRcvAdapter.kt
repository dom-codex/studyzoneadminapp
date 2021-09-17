package com.sparktech.studyzoneadmin.lisense_key

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.KeysRowRcvItemBinding
import com.sparktech.studyzoneadmin.databinding.LoadingRcvBinding
import com.sparktech.studyzoneadmin.databinding.SetPricingDialogFormBinding
import com.sparktech.studyzoneadmin.helpers.showToast
import com.sparktech.studyzoneadmin.models.LisenseKey

private const val LOADER_ITEM = 0
private const val ITEM_VIEW = 1

class LisenseKeyRcvAdapter(
    private val deleteHandler: ((keyId: String) -> Unit)?,
    private val updateHandler: ((KeyId: String, price: String) -> Unit)?,
    private val type: String,
    private val fromVendorWindow:Boolean = false
) : ListAdapter<LisenseKey, RecyclerView.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: KeysRowRcvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(key: LisenseKey) {
            binding.apply {
                priceVal.text = key.price.toString()
                keyVal.text = key.key
                isUsedVal.text = key.isUsed.toString()
                usedVal.text = key.usedBy
                vendorVal.text = key.vendor
                if(fromVendorWindow){
                    keyEditBtn.visibility = View.GONE
                }
                keyEditBtn.setOnClickListener {
                    val popupMenu = PopupMenu(binding.root.context, it)
                    popupMenu.menuInflater.inflate(R.menu.key_edit_menu, popupMenu.menu)
                    val editItem = popupMenu.menu.findItem(R.id.key_edit_price_item)
                    if(type!="NOT_USED"){
                        editItem.isVisible = false
                    }
                    popupMenu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.key_edit_price_item -> {
                                //show dialog
                                val alertDialog = AlertDialog.Builder(binding.root.context)
                                val dialog = alertDialog.create()
                                val inflater = LayoutInflater.from(alertDialog.context)
                                val dialogBinding = SetPricingDialogFormBinding.inflate(
                                    inflater,
                                    dialog.listView,
                                    false
                                )
                                dialogBinding.setPriceBtn.setOnClickListener {
                                    val price = dialogBinding.pricingInput.text.toString()
                                    if (!price.isDigitsOnly() || price.toInt() <= 0) {
                                        showToast(binding.root.context, "enter a valid value")
                                        return@setOnClickListener
                                    }
                                    updateHandler?.invoke(key.keyId, price)
                                    dialog.dismiss()
                                }
                                dialog.setView(dialogBinding.root)
                                dialog.show()
                            }
                            R.id.key_delete_item -> {
                                //show confirmation dialog
                                val alertDialog = AlertDialog.Builder(binding.root.context)
                                alertDialog.setTitle("Confirm Action")
                                alertDialog.setMessage("Are you sure you want to delete this key?")
                                alertDialog.setNegativeButton("NO", null)
                                alertDialog.setPositiveButton("YES") { dialog, _ ->
                                    deleteHandler?.invoke(key.keyId)
                                    dialog.dismiss()
                                }
                                alertDialog.show()
                            }
                        }
                        true
                    }
                    popupMenu.show()
                }
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<LisenseKey>() {
            override fun areItemsTheSame(oldItem: LisenseKey, newItem: LisenseKey): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: LisenseKey, newItem: LisenseKey): Boolean {
                return oldItem.key == newItem.key
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        val data = getItem(position)
        if (data.isLoading) {
            return LOADER_ITEM
        }
        return ITEM_VIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == ITEM_VIEW) {
            return ViewHolder(KeysRowRcvItemBinding.inflate(inflater, parent, false))
        }
        return LoaderViewHolder(LoadingRcvBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val key = getItem(position)
        if(position<currentList.size){
            if (holder is ViewHolder) {
                holder.bind(key)
            }
        }
    }
}

class LoaderViewHolder(private val binding: LoadingRcvBinding) :
    RecyclerView.ViewHolder(binding.root)
package com.sparktech.studyzoneadmin.lisense_key

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.SchoolDetailsItemBinding
import com.sparktech.studyzoneadmin.models.Vendor
import java.util.*

class DropDownCustomAdapter(
    val c: Context,
    private val vendors: List<Vendor>,
    val selectHandler: (vendor:Vendor) -> Unit
) :
    ArrayAdapter<Vendor>(c,0) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //super.getView(position, convertView, parent)
        val inflater = LayoutInflater.from(parent.context)
        val binding = SchoolDetailsItemBinding.inflate(inflater, parent, false)

            binding.facultyName.text = getItem(position)?.name
            binding.facultyCreatedAtLabel.text = getItem(position)?.vendorId
            binding.root.setOnClickListener {
                selectHandler(getItem(position)!!)
            }


        return binding.root
    }

    override fun getFilter(): Filter {
        super.getFilter()
        return vendorFilter
    }
    private val vendorFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults? {
            val results = FilterResults()
            val vendorFilteredLists = mutableListOf<Vendor>()
            if (constraint == null || constraint.isEmpty()) {
                vendorFilteredLists.addAll(vendors)
            } else {
                val filterPattern =
                    constraint.toString().toLowerCase(Locale.ROOT)
                for (vendor in vendors) {
                    if (vendor.name.contains(filterPattern,true)) {
                        println("yes")
                        vendorFilteredLists.add(vendor)
                    }
                }
            }
            results.values = vendorFilteredLists
            results.count = vendorFilteredLists.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            this@DropDownCustomAdapter.clear()
            addAll(results.values as List<Vendor>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any): CharSequence? {
            val vendor = resultValue as Vendor
            return "${vendor.name}-${resultValue.vendorId}"
        }
    }
}
package com.sparktech.studyzoneadmin.vendor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.NewVendorDialogFormBinding
import com.sparktech.studyzoneadmin.databinding.VendorMainLayoutBinding
import com.sparktech.studyzoneadmin.helpers.showToast
import com.sparktech.studyzoneadmin.lisense_key.LisenseKeyViewModel
import com.sparktech.studyzoneadmin.lisense_key.LisenseKeyViewModelFactory
import com.sparktech.studyzoneadmin.main_menu.SwipeController
import com.sparktech.studyzoneadmin.request_models.CreateVendorRequestBody

class VendorFragment : Fragment() {
    private lateinit var binding: VendorMainLayoutBinding
    private lateinit var vm: LisenseKeyViewModel
    private lateinit var adapter: VendorsRcvAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.vendor_main_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val fac = LisenseKeyViewModelFactory(requireActivity().application, admin!!)
        vm = ViewModelProvider(requireActivity(), fac).get(LisenseKeyViewModel::class.java)
        if (vm.indicators["loadingVendors"] == null) {
            vm.indicators["loadingVendors"] = MutableLiveData(false)
            vm.fetchVendors()
        }
        if( vm.indicators["updatingVendorList"]==null){
            vm.indicators["updatingVendorList"] = MutableLiveData(false)
        }
        adapter = VendorsRcvAdapter()
        binding.vendorRcv.adapter = adapter
        val swipeController = object:SwipeController(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if(direction == ItemTouchHelper.LEFT){
                    val index = viewHolder.adapterPosition
                    AlertDialog.Builder(requireContext())
                        .setTitle("Confirmation")
                        .setMessage("Are you sure you want to delete this vendor?")
                        .setNegativeButton("NO"){d,_->
                         adapter.notifyDataSetChanged()
                         d.dismiss()
                        }
                        .setPositiveButton("YES"){d,_->
                          vm.deleteVendor(index)
                        }.show()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.vendorRcv)
        binding.newVendorBtn.setOnClickListener {
            if(vm.indicators["loadingVendors"]!!.value!!){
                showToast(requireContext(),"please wait...")
                return@setOnClickListener
            }
            showDialog()
        }
        setUpObservers()
    }
private fun showDialog(){
    val alertDialog = AlertDialog.Builder(requireContext())
    val dialog = alertDialog.create()
    val inflater = LayoutInflater.from(alertDialog.context)
    val dialogBinding = NewVendorDialogFormBinding.inflate(inflater,dialog.listView,false)
    dialogBinding.createVendorBtn.setOnClickListener {
        val name = dialogBinding.vendorNameInput.text.toString()
        val phone = dialogBinding.vendorPhoneInput.text.toString()
        if(name.isBlank()){
            showToast(requireContext(),"name cannot be blank")
            return@setOnClickListener
        }
        if(phone.isBlank()){
            showToast(requireContext(),"phone cannot be blank")
            return@setOnClickListener
        }
        vm.createVendor(name,phone)
        dialog.dismiss()
    }
    dialog.setView(dialogBinding.root)
    dialog.show()
}
    private fun setUpObservers() {
        vm.indicators["loadingVendors"]!!.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.vendorLoader.visibility = View.GONE
                    if (vm.vendors.isNotEmpty()) {
                        binding.vendorNoData.visibility = View.GONE
                        adapter.submitList(vm.vendors)
                    } else {
                        binding.vendorNoData.visibility = View.VISIBLE
                    }
                } else {
                    if (vm.vendors.isEmpty()) {
                        binding.vendorLoader.visibility = View.VISIBLE
                        binding.vendorNoData.visibility = View.GONE
                    }
                }
            }

        })
        vm.indicators["updatingVendorList"]!!.observe(viewLifecycleOwner,{loading->
            loading?.let {
                if(!it){
                   binding.vendorLoadingLayout.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                }else{
                   binding.vendorLoadingLayout.visibility = View.VISIBLE
                }
            }

        })
    }
}
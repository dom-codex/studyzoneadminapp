package com.sparktech.studyzoneadmin.lisense_key

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.GenerateKeysDialogFormBinding
import com.sparktech.studyzoneadmin.helpers.showToast
import com.sparktech.studyzoneadmin.models.Vendor
import com.sparktech.studyzoneadmin.request_models.GenerateKeysRequestBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GenKeyBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: GenerateKeysDialogFormBinding
    private lateinit var vm: LisenseKeyViewModel
    private lateinit var adapter: DropDownCustomAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.generate_keys_dialog_form, container, false)
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
        if(vm.vendors.isNotEmpty()){
            adapter =
                DropDownCustomAdapter(requireContext(), vm.vendors, selectionHandler)
            initViews()
        }
        setUpObservers()
    }

    private val selectionHandler: (vendor: Vendor) -> Unit = {
        binding.vendorPositionInput.text = it.vendorId
        binding.vendorInput.text =Editable.Factory().newEditable("${it.name}-${it.vendorId}")
        binding.vendorInput.dismissDropDown()
    }

    private fun initViews() {
        binding.vendorInput.setAdapter(adapter)
        binding.generateKeysBtn.setOnClickListener {
            val price = binding.keyPriceInput.text.toString()
            val noOfKeys = binding.noKeysInput.text.toString()
            val vendorKey = binding.vendorPositionInput.text.toString()
            if (price.isBlank() || !price.isDigitsOnly()) {
                showToast(requireContext(), "enter a valid price value")
                return@setOnClickListener
            }
            if (!noOfKeys.isDigitsOnly() || noOfKeys.toInt() <= 0) {
                showToast(requireContext(), "enter a valid no of keys value")
                return@setOnClickListener
            }
            if (vendorKey.isBlank()) {
                showToast(requireContext(), "please select a vendor")
                return@setOnClickListener
            }
            val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
            val admin = sp.getString("adminId", "")
            println(vendorKey)
            val body =
                GenerateKeysRequestBody(admin!!, noOfKeys.toInt(), vendorKey, price.toLong())
            vm.generateKeys(body)
            dismissAllowingStateLoss()
        }
    }

    private fun setUpObservers() {
        vm.indicators["loadingVendors"]!!.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    //hide loading body
                    binding.genKeyLoadingLayout.visibility = View.GONE
                    binding.genKeyMain.visibility = View.VISIBLE
                    if (vm.vendors.isNotEmpty()) {
                        adapter =
                            DropDownCustomAdapter(requireContext(), vm.vendors, selectionHandler)
                        initViews()
                    }
                } else {
                    //show loading body
                    binding.genKeyLoadingLayout.visibility = View.VISIBLE
                    binding.genKeyMain.visibility = View.GONE
                }
            }
        })
    }


    companion object {
        fun createInstance(): GenKeyBottomSheet {
            return GenKeyBottomSheet()
        }
    }
}
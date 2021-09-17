package com.sparktech.studyzoneadmin.vendor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.KeysMainLayoutBinding
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.lisense_key.LisenseKeyRcvAdapter
import com.sparktech.studyzoneadmin.lisense_key.LisenseKeyViewModel
import com.sparktech.studyzoneadmin.lisense_key.LisenseKeyViewModelFactory
import com.sparktech.studyzoneadmin.models.LisenseKey

class VendorKeysTabFragment : Fragment() {
    private lateinit var binding: KeysMainLayoutBinding
    private lateinit var vm: LisenseKeyViewModel
    private lateinit var adapter: LisenseKeyRcvAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.keys_main_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val fac = LisenseKeyViewModelFactory(requireActivity().application, admin!!)
        vm = ViewModelProvider(requireActivity(), fac).get(LisenseKeyViewModel::class.java)
        val bundle = requireArguments()
        val type = bundle.getString("type", "")
        val vendorId = bundle.getString("vendorId", "")
        if (vm.keys["${vendorId}${type}keys"] == null) {
            vm.keys["${vendorId}${type}keys"] = mutableListOf()
            vm.pages["${vendorId}${type}page"] = 0
            vm.indicators["loadingkey$type${vendorId}"] = MutableLiveData(false)
            vm.vendorKeys(type, vendorId)
        }
        adapter = LisenseKeyRcvAdapter(null, null, type,true)
        binding.lisenseKeysRcv.adapter = adapter
        binding.lisenseKeysRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.lisenseKeysRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchMoreKeys
            )
        )
        setUpObservers()
    }

    //scroll lambdas
    private val getLoadingState: () -> Boolean = {
        val bundle = requireArguments()
        val type = bundle.getString("type", "")
        val vendorId = bundle.getString("vendorId", "")
        vm.indicators["loading$type${vendorId}"]!!.value!!
    }
    private val getListSize: () -> Int = {
        val bundle = requireArguments()
        val type = bundle.getString("type", "")
        val vendorId = bundle.getString("vendorId", "")
        vm.keys["${vendorId}${type}keys"]!!.size
    }
    private val setLoaderItem: () -> Unit = {
        val bundle = requireArguments()
        val type = bundle.getString("type", "")
        val vendorId = bundle.getString("vendorId", "")
        val loaderItem =
            LisenseKey("", "", 0f, false, "", "", "", true)
        vm.keys["${vendorId}${type}keys"]!!.add(loaderItem)
        adapter.notifyItemInserted(vm.keys["${vendorId}${type}keys"]!!.size - 1)

    }
    private val fetchMoreKeys = {
        val bundle = requireArguments()
        val type = bundle.getString("type", "")
        val vendorId = bundle.getString("vendorId", "")
        vm.vendorKeys(type, vendorId)
    }

    private fun setUpObservers() {
        val bundle = requireArguments()
        val type = bundle.getString("type", "")
        val vendorId = bundle.getString("vendorId", "")
        vm.indicators["loadingkey$type${vendorId}"]!!.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.keysLoadingLayout.visibility = View.GONE
                    if (vm.keys["${vendorId}${type}keys"]!!.isNotEmpty()) {
                        adapter.submitList(vm.keys["${vendorId}${type}keys"])
                    } else {
                        binding.keyNoData.visibility = View.VISIBLE
                    }
                } else {
                    if (vm.keys["${vendorId}${type}keys"]!!.isEmpty()) {
                        binding.keysLoadingLayout.visibility = View.VISIBLE
                        binding.keyNoData.visibility = View.GONE
                    }
                }
            }

        })
    }
}
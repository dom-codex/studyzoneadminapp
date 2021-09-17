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
import com.google.android.material.tabs.TabLayoutMediator
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.VendorStatsLayoutBinding
import com.sparktech.studyzoneadmin.lisense_key.LisenseKeyViewModel
import com.sparktech.studyzoneadmin.lisense_key.LisenseKeyViewModelFactory
import com.sparktech.studyzoneadmin.models.VendorKeyStats

class VendorStatisticsFragment : Fragment() {
    private lateinit var binding: VendorStatsLayoutBinding
    private val title = listOf("USED","NOT USED")
    private lateinit var vm: LisenseKeyViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.vendor_stats_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        val vendorId = bundle.getString("vendorId", "")
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val fac = LisenseKeyViewModelFactory(requireActivity().application, admin!!)
        vm = ViewModelProvider(requireActivity(), fac).get(LisenseKeyViewModel::class.java)
        if (vm.indicators["loading$vendorId"] == null) {
            vm.indicators["loading$vendorId"] = MutableLiveData(false)
            vm.vendorKeyStats(vendorId)
        }
        setUpObservers()
        val pagerAdapter = VendorKeysFragmentAdapter(this)
        binding.vendorKeysPager.adapter = pagerAdapter
        TabLayoutMediator(binding.vendorKeysTab,binding.vendorKeysPager){tab,position->
            tab.text = title[position]
        }.attach()
    }

    private fun initViews(stats: VendorKeyStats) {
        binding.apply {
            totalGenValue.text = stats.noOfKeysGenerated
            totalCostVal.text = stats.totalcostOfKeys
            totalUsedVal.text = stats.noOfUsedKeys
            totalSoldCost.text = stats.totalcostOfUsedKeys
            totalUnusedVal.text = stats.noOfUnUsedKeys
        }

    }

    private fun setUpObservers() {
        val bundle = requireArguments()
        val vendorId = bundle.getString("vendorId", "")
        vm.indicators["loading$vendorId"]!!.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    if (vm.vendorStats != null) {
                        initViews(vm.vendorStats!!)
                        return@let
                    }
                    initViews(VendorKeyStats("0", "0", "0", "0", "0", "0"))
                } else {

                }
            }
        })
    }
}
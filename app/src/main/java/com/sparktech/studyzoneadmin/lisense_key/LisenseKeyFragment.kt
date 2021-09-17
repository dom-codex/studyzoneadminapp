package com.sparktech.studyzoneadmin.lisense_key

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
import com.sparktech.studyzoneadmin.databinding.LisenseKeyLayoutBinding
import com.sparktech.studyzoneadmin.response_models.GetKeyStatisticsResponseBody

class LisenseKeyFragment : Fragment() {
    private lateinit var binding: LisenseKeyLayoutBinding
    private lateinit var vm: LisenseKeyViewModel
    private val title = listOf("USED", "ALL", "NOT USED")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.lisense_key_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val fac = LisenseKeyViewModelFactory(requireActivity().application, admin!!)
        vm = ViewModelProvider(requireActivity(), fac).get(LisenseKeyViewModel::class.java)
        if(vm.indicators["generating"]==null){
            vm.indicators["generating"] = MutableLiveData(false)
            vm.indicators["stats"] = MutableLiveData(false)
            vm.fetchKeysStatistics()

        }
        val adapter = LisenseKeyFragmentAdapter(this)
        binding.keysPager.adapter = adapter
        TabLayoutMediator(binding.keysTab, binding.keysPager) { tab, pos ->
            tab.text = title[pos]
        }.attach()
        binding.keysPager.isUserInputEnabled = false
        binding.openGenKeyFab.setOnClickListener {
            parentFragmentManager.let {
                GenKeyBottomSheet.createInstance().apply {
                    this.show(it,"gen key bottom")
                }
            }
        }
        setUpObservers()
    }

    private fun initStats(data: GetKeyStatisticsResponseBody) {
        binding.apply {
            totalCostVal.text = data.costOfAllKeys.toString()
            totalGenValue.text = data.nTotalKeys.toString()
            totalSoldCost.text = data.costOfUsedKeys.toString()
            totalUsedVal.text = data.nUsedKeys.toString()
            totalUnusedVal.text = data.nNotUsedKeys.toString()
        }
    }

    private fun setUpObservers() {
        vm.keyStats.observe(viewLifecycleOwner, {
            if (it != null) {
                initStats(it)
            } else {
                initStats(GetKeyStatisticsResponseBody(404, "", 0, 0, 0, 0, 0, 0))
            }
        })
        vm.indicators["generating"]!!.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.mainKeyLoadingLayout.visibility = View.GONE
                } else {
                    binding.mainKeyLoadingLayout.visibility = View.VISIBLE
                }
            }

        })
    }
}
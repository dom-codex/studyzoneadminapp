package com.sparktech.studyzoneadmin.withdrawal_request

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.WithdrawalRequestLayoutBinding
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.models.WithdrawalRequest
import com.sparktech.studyzoneadmin.request_models.UpdateWithdrawalStatusRequestBody

class WithdrawalRequestFragment : Fragment() {
    private lateinit var binding: WithdrawalRequestLayoutBinding
    private lateinit var vm: WithdrawalRequestViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.withdrawal_request_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val fac = WithdrawalRequestViewModelFactory(requireActivity().application, admin!!)
        vm = ViewModelProvider(requireActivity(), fac).get(WithdrawalRequestViewModel::class.java)
        initViews()
        initSpinner()

    }
    private fun initViews() {
        binding.requestValue.text = "0"
        val pagerAdapter = WithdrawalTabAdapter(this)
        binding.requestPager.adapter = pagerAdapter
        binding.requestPager.isUserInputEnabled = false
        TabLayoutMediator(binding.requestTab,binding.requestPager){tab,pos->

        }
    }

    private fun initSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.request_types,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.requestSpinner.adapter = it
        }
        binding.requestSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    val item = parent?.getItemAtPosition(pos)
                    when (item) {
                        "PENDING" -> {
                      binding.requestPager.currentItem = 0
                        }
                        "APPROVED" -> {
                            binding.requestPager.currentItem = 1
                        }
                        "DECLINED" -> {
                            binding.requestPager.currentItem = 2

                        }
                    }

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Toast.makeText(requireContext(), "select a filter", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /*private fun setUpObservers() {
        vm.isLoading.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.requestLoader.visibility = View.GONE
                    when (binding.requestSpinner.selectedItem as String) {
                        "APPROVED" -> {
                            if (vm.approvedRequests.size > 0) {
                                binding.withdrawalRcv.visibility = View.VISIBLE
                                binding.requestNoData.visibility = View.GONE
                                adapter.submitList(vm.approvedRequests)
                                adapter.notifyDataSetChanged()
                                return@let
                            }
                            binding.requestNoData.visibility = View.VISIBLE
                        }
                        "DECLINED" -> {
                            if (vm.declinedRequests.size > 0) {
                                binding.withdrawalRcv.visibility = View.VISIBLE
                                binding.requestNoData.visibility = View.GONE
                                adapter.submitList(vm.declinedRequests)
                                adapter.notifyDataSetChanged()
                                return@let
                            }
                            binding.requestNoData.visibility = View.VISIBLE
                        }
                        "PENDING" -> {
                            if (vm.pendingRequests.size > 0) {
                                binding.withdrawalRcv.visibility = View.VISIBLE
                                binding.requestNoData.visibility = View.GONE
                                adapter.submitList(vm.pendingRequests)
                                adapter.notifyDataSetChanged()
                                return@let
                            }
                            binding.requestNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    when (binding.requestSpinner.selectedItem as String) {
                        "APPROVED" -> {
                            if (vm.approvedRequests.size == 0) {
                                //show spinner
                                binding.requestLoader.visibility = View.VISIBLE
                            }
                        }
                        "DECLINED" -> {
                            if (vm.declinedRequests.size == 0) {
                                //show spinner
                                binding.requestLoader.visibility = View.VISIBLE
                            }
                        }
                        "PENDING" -> {
                            if (vm.pendingRequests.size == 0) {
                                //show spinner
                                binding.requestLoader.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        })
    }*/
}
package com.sparktech.studyzoneadmin.transaction

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
import com.sparktech.studyzoneadmin.databinding.TransactionTabFragmentLayoutBinding
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.models.Transaction

class TransactionTabFragment:Fragment() {
    private lateinit var binding:TransactionTabFragmentLayoutBinding
    private lateinit var vm:TransactionViewModel
    private lateinit var adapter: TransactionsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.transaction_tab_fragment_layout,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        val filter = bundle.getString("filter","")
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId","")
        val fac = TransactionViewModelFactory(requireActivity().application)
        vm = ViewModelProvider(requireActivity(),fac).get(TransactionViewModel::class.java)
        vm.filter = filter
        if(vm.transactions[vm.filter]==null){
            vm.transactions[vm.filter] = mutableListOf()
            vm.indicators[vm.filter] = MutableLiveData(false)
            vm.pages[vm.filter] = 0
            vm.getTransactions(admin!!)
        }
        adapter = TransactionsAdapter()
        setUpObservers()
        initViews()

    }
    private fun initViews() {
        binding.txTableRcv.adapter = adapter
        binding.txTableRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.txTableRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchMoreTransaction
            )
        )
    }
    private val getLoadingState: () -> Boolean = {
        vm.indicators[vm.filter]!!.value!!
    }
    private val getListSize: () -> Int = {
        vm.transactions[vm.filter]!!.size
    }
    private val setLoaderItem: () -> Unit = {
        val loaderItem =
            Transaction(
                "", "", "", "",
                0, "", "", "",
                "", "", null,
                null, null, null, true
            )
            vm.transactions[vm.filter]!!.add(loaderItem)
            adapter.notifyItemInserted(vm.transactions[vm.filter]!!.size - 1)
    }
    private val fetchMoreTransaction = {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val adminId = sp.getString("adminId", "")
        vm.getTransactions(adminId!!)
    }
    private fun setUpObservers() {
        vm.indicators[vm.filter]!!.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.txLoader.visibility = View.GONE
                    if (vm.transactions[vm.filter]!!.size > 0) {
                        //hide  no data and progress bar
                            adapter.submitList(vm.transactions[vm.filter])
                        binding.txNoData.visibility = View.GONE
                        return@let
                    }
                    binding.txNoData.visibility = View.VISIBLE
                } else {
                    //show spinner
                    if (vm.transactions[vm.filter]!!.size == 0) {
                        binding.txLoader.visibility = View.VISIBLE
                        binding.txNoData.visibility = View.GONE
                    }
                }
            }
        })
    }
}
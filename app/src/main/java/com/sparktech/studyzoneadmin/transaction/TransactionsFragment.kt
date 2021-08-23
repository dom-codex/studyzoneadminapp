package com.sparktech.studyzoneadmin.transaction

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.TransactionLayoutBinding

class TransactionsFragment:Fragment() {
    private lateinit var binding:TransactionLayoutBinding
    private lateinit var vm:TransactionViewModel
    private lateinit var adapter: TransactionsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.transaction_layout,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = requireContext().getSharedPreferences("AdminDetails",Context.MODE_PRIVATE)
        val adminId = sp.getString("adminId","")
        vm = ViewModelProvider(requireActivity()).get(TransactionViewModel::class.java)
        vm.getTransactions(adminId!!,0)
        adapter = TransactionsAdapter()
        initViews()
        setUpObservers()
    }
    private fun initViews(){
        binding.txTableRcv.adapter = adapter
    }
    private fun setUpObservers(){
        vm.isLoading.observe(viewLifecycleOwner,{loading->
            loading?.let {
                if(!it){
                  adapter.submitList(vm.transactions)
                }else{
                    //show spinner
                }
            }
        })
    }
}

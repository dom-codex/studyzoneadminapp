package com.sparktech.studyzoneadmin.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.TransactionLayoutBinding
import com.sparktech.studyzoneadmin.transaction.TransactionsAdapter

class UserTransactionTabFragment: Fragment() {
    private lateinit var binding:TransactionLayoutBinding
    private lateinit var vm:UsersViewModel
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
        val bundle = requireArguments()
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val user = bundle.getString("user")
        val adminId = sp.getString("adminId","")
        vm = ViewModelProvider(requireActivity()).get(UsersViewModel::class.java)
        vm.fetchUserTransactions(adminId!!,user!!)
        adapter = TransactionsAdapter()
        setUpObservers()
    }
    private fun setUpObservers(){
        vm.isLoadingTransaction.observe(viewLifecycleOwner,{loading->
            loading?.let {
                if(!it){
                    initRcv()
                }else{
                    //show spinner
                }
            }
        })
    }
    private fun initRcv(){
        binding.txTableRcv.adapter = adapter
        adapter.submitList(vm.usersTransaction)
    }
}
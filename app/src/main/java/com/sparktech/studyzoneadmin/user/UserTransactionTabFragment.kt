package com.sparktech.studyzoneadmin.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.TransactionLayoutBinding
import com.sparktech.studyzoneadmin.databinding.TransactionTabFragmentLayoutBinding
import com.sparktech.studyzoneadmin.helpers.KeyComposer
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.models.Transaction
import com.sparktech.studyzoneadmin.transaction.TransactionsAdapter

class UserTransactionTabFragment : Fragment() {
    private lateinit var binding: TransactionTabFragmentLayoutBinding
    private lateinit var vm: UsersViewModel
    private lateinit var adapter: TransactionsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.transaction_tab_fragment_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val user = bundle.getString("user")
        val adminId = sp.getString("adminId", "")
        vm = ViewModelProvider(requireActivity()).get(UsersViewModel::class.java)
        if (vm.transactions[KeyComposer.getUserDetailsTransactionsKey(bundle)] == null) {
            //init hashes
            vm.transactions[KeyComposer.getUserDetailsTransactionsKey(bundle)] =
                mutableListOf()
            vm.indicators[KeyComposer.getUserDetailsLoadingTransactions(bundle)] =
                MutableLiveData(false)
            vm.pages[KeyComposer.getUserCurrentTransactionPage(bundle)] = 0
            vm.fetchUserTransactions(
                adminId!!,
                user!!, bundle,
                vm.pages[KeyComposer.getUserCurrentTransactionPage(requireArguments())]!!
            ) { err, msg ->
                requireActivity().runOnUiThread {
                    if (err) {
                        Toast.makeText(requireContext(), "an error occurred", Toast.LENGTH_SHORT)
                            .show()

                    } else {
                        msg?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            //  vm.fetchUserTransactions(adminId!!, user!!,bundle,)

        }
        adapter = TransactionsAdapter()
        binding.txTableRcv.adapter = adapter
      //  binding.chipGroup.visibility = View.GONE
        binding.txTableRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.txTableRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchTransaction
            )
        )
        setUpObservers()
    }

    //scroll lambdas
    private val getLoadingState: () -> Boolean = {
        vm.indicators[KeyComposer.getUserDetailsLoadingTransactions(requireArguments())]!!.value!!
    }
    private val getListSize: () -> Int = {
        vm.transactions[KeyComposer.getUserDetailsTransactionsKey(requireArguments())]!!.size
    }
    private val setLoaderItem: () -> Unit = {
        val bundle = requireArguments()
        val loaderItem = Transaction(
            "", "", "",
            "", 0, "", "", "", "", "", null, null, null,
            null, true
        )
        vm.transactions[KeyComposer.getUserDetailsTransactionsKey(bundle)]!!.add(loaderItem)
        adapter.notifyItemInserted(vm.transactions[KeyComposer.getUserDetailsTransactionsKey(bundle)]!!.size - 1)
    }
    private val fetchTransaction = {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val bundle = requireArguments()
        val adminId = sp.getString("adminId", "")
        val user = bundle.getString("user")
        vm.fetchUserTransactions(
            adminId!!,
            user!!, bundle,
            vm.pages[KeyComposer.getUserCurrentTransactionPage(bundle)]!!
        ) { err, msg ->
            requireActivity().runOnUiThread {
                if (err) {
                    Toast.makeText(requireContext(), "an error occurred", Toast.LENGTH_SHORT).show()

                } else {
                    msg?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun setUpObservers() {
        vm.indicators[KeyComposer.getUserDetailsLoadingTransactions(requireArguments())]!!.observe(
            viewLifecycleOwner,
            { loading ->
                loading?.let {
                    binding.txLoader.visibility = View.GONE
                    if (!it) {
                        initRcv()
                    } else {
                        //show spinner
                        if (vm.transactions[KeyComposer.getUserDetailsTransactionsKey(
                                requireArguments()
                            )]!!.isEmpty()
                        ) {
                            binding.txLoader.visibility = View.VISIBLE
                            binding.txNoData.visibility = View.GONE
                        }
                    }
                }
            })
    }

    private fun initRcv() {
        if (vm.transactions[KeyComposer.getUserDetailsTransactionsKey(requireArguments())]!!.isEmpty()) {
            binding.txNoData.visibility = View.VISIBLE
            return
        }
        binding.txNoData.visibility = View.GONE
        binding.txTableRcv.visibility = View.VISIBLE
        adapter.submitList(
            vm.transactions[KeyComposer.getUserDetailsTransactionsKey(
                requireArguments()
            )]
        )
    }
}
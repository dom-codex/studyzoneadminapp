package com.sparktech.studyzoneadmin.withdrawal_request

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
import com.sparktech.studyzoneadmin.databinding.WithdrawalRequestTabLayoutBinding
import com.sparktech.studyzoneadmin.helpers.KeyComposer
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.models.WithdrawalRequest
import com.sparktech.studyzoneadmin.request_models.UpdateWithdrawalStatusRequestBody

class WithdrawalTabFragment : Fragment() {
    private lateinit var binding: WithdrawalRequestTabLayoutBinding
    private lateinit var adapter: WithdrawalRequestAdapter
    private lateinit var vm: WithdrawalRequestViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.withdrawal_request_tab_layout,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val bundle = requireArguments()
        val type = bundle.getString("type", "")
        val fac = WithdrawalRequestViewModelFactory(requireActivity().application, admin!!)
        vm = ViewModelProvider(requireActivity(), fac).get(WithdrawalRequestViewModel::class.java)
        adapter = WithdrawalRequestAdapter(updateHandler, requireContext(),type!!)
        binding.withdrawalRcv.adapter = adapter
        binding.withdrawalRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.withdrawalRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchMoreRequest
            )
        )

        if (vm.requests[type] == null) {
            //init hashes here
            vm.requests[KeyComposer.getCurrentRequestListKey(bundle)] = mutableListOf()
            vm.indicators[KeyComposer.getCurrentRequestLoadingListKey(bundle)] =
                MutableLiveData(false)
            vm.indicators[KeyComposer.getCurrentRequestListUpdatingKey(bundle)] =
                MutableLiveData(false)
            vm.pages[KeyComposer.getCurrentRequestListTypePage(bundle)] = 0
            //fetch current list
            val query = HashMap<String, String>()
            query["adminId"] = admin
            query["page"] = "0"
            query["status"] = type
            vm.fetchRequest(query) { _, _ ->

            }
            vm.fetchRequest(query) { _, _ -> }
        }
        setUpObservers()
    }

    private val updateHandler: (wid: String, status: String) -> Unit = { hash, status ->
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val user = sp.getString("adminId", "")
        val body = UpdateWithdrawalStatusRequestBody(hash, status, user!!)
        vm.updateRequestStatus(body)

    }

    //scroll lambdas
    private val getLoadingState: () -> Boolean = {
        vm.indicators[KeyComposer.getCurrentRequestLoadingListKey(requireArguments())]!!.value!!
    }
    private val getListSize: () -> Int = {
        vm.requests[KeyComposer.getCurrentRequestListKey(requireArguments())]!!.size
    }
    private val setLoaderItem: () -> Unit = {
        val bundle = requireArguments()
        val loaderItem = WithdrawalRequest(
            "", "", 0,
            "", "", "", "", "", true
        )
        vm.requests[KeyComposer.getCurrentRequestListKey(bundle)]!!.add(loaderItem)
        adapter.notifyItemInserted(vm.requests[KeyComposer.getCurrentRequestListKey(bundle)]!!.size - 1)
    }
    private val fetchMoreRequest = {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val bundle = requireArguments()
        val admin = sp.getString("adminId", "")
        val query = HashMap<String, String>()
        val status = bundle.getString("type", "")!!
        query["adminId"] = admin!!
        query["status"] = status//bundle.getString("type","")!!
        query["page"] = vm.pages["${status}_page"]!!.toString()
        vm.fetchRequest(query) { err, msg ->
            requireActivity().runOnUiThread {
                if (err) {
                    Toast.makeText(requireContext(), "an error occured", Toast.LENGTH_SHORT).show()
                } else {
                    msg?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }

    }

    private fun setUpObservers() {
        val bundle = requireArguments()
        vm.indicators[KeyComposer.getCurrentRequestLoadingListKey(bundle)]!!.observe(
            viewLifecycleOwner,
            { loading ->
                loading?.let {
                    if (!it) {
                        binding.withdrawalLoader.visibility = View.GONE
                        if (vm.requests[KeyComposer.getCurrentRequestListKey(bundle)]!!.size > 0) {
                            binding.requestNoLoader.visibility = View.GONE
                            binding.withdrawalRcv.visibility = View.VISIBLE
                            adapter.submitList(vm.requests[KeyComposer.getCurrentRequestListKey(bundle)])
                        } else {
                            binding.requestNoLoader.visibility = View.VISIBLE
                        }
                    } else {
                        if (vm.requests[KeyComposer.getCurrentRequestListKey(bundle)]!!.size == 0) {
                            binding.withdrawalLoader.visibility = View.VISIBLE
                            binding.requestNoLoader.visibility = View.GONE
                        }
                    }
                }
            })
        vm.indicators[KeyComposer.getCurrentRequestListUpdatingKey(bundle)]!!.observe(viewLifecycleOwner,{loading->
            loading?.let {
                if(!it){
                  binding.updatingRequestLayout.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                }else{
                    binding.updatingRequestLayout.visibility = View.VISIBLE
                }
            }

        })
    }
}
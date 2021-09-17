package com.sparktech.studyzoneadmin.lisense_key

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.KeysMainLayoutBinding
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.main_menu.SwipeController
import com.sparktech.studyzoneadmin.models.LisenseKey
import com.sparktech.studyzoneadmin.request_models.UpdateKeyRequestBody

class LisenseKeyTabFragment : Fragment() {
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
        val bundle = requireArguments()
        val type = bundle.getString("type", "")
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val fac = LisenseKeyViewModelFactory(requireActivity().application, admin!!)
        vm = ViewModelProvider(requireActivity(), fac).get(LisenseKeyViewModel::class.java)
        if(vm.keys[type]==null){
            //init hashes
            vm.keys[type] = mutableListOf()
            vm.indicators["loading$type"]  = MutableLiveData(false)
            vm.pages["${type}pages"] = 0
            vm.indicators["updating"] = MutableLiveData(false)
            vm.indicators["generating"] = MutableLiveData(false)
            vm.fetchKeys(type)
        }
        adapter = LisenseKeyRcvAdapter(deleteHandler, updateHandler, type)
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
        vm.indicators["loading$type"]!!.value!!
    }
    private val getListSize: () -> Int = {
        val bundle = requireArguments()
        val type = bundle.getString("type", "")
        vm.keys[type]!!.size
    }
    private val setLoaderItem: () -> Unit = {
        val bundle = requireArguments()
        val type = bundle.getString("type", "")
        val loaderItem =
            LisenseKey("", "", 0f, false, "", "", "", true)
        vm.keys[type]!!.add(loaderItem)
        adapter.notifyItemInserted(vm.keys[type]!!.size - 1)

    }
    private val fetchMoreKeys = {
        val bundle = requireArguments()
        val type = bundle.getString("type", "")
        vm.fetchKeys(type!!)
    }
    private val deleteHandler: (keyId: String) -> Unit = {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val body = UpdateKeyRequestBody(admin!!, it, 0)
        vm.deleteKey(body)
    }
    private val updateHandler: (keyId: String, price: String) -> Unit = { keyId, price ->
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val body = UpdateKeyRequestBody(admin!!, keyId, price.toLong())
        vm.updateKey(body)

    }

    private fun setUpObservers() {
        val bundle = requireArguments()
        val type = bundle.getString("type", "")
        vm.indicators["loading$type"]!!.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.keysLoadingLayout.visibility = View.GONE
                    if (vm.keys[type]!!.isNotEmpty()) {
                        binding.keyNoData.visibility = View.GONE
                        adapter.submitList(vm.keys[type])
                        adapter.notifyDataSetChanged()
                        return@let
                    }
                    binding.keyNoData.visibility = View.VISIBLE
                } else {
                    //show spinner
                    binding.keysLoadingLayout.visibility = View.VISIBLE
                }
            }
        })
        vm.indicators["updating"]!!.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    //hide loading layout
                    binding.keysLoadingLayout.visibility = View.GONE

                    adapter.submitList(vm.keys[type])
                    adapter.notifyDataSetChanged()
                } else {
                    //show loading layout
                    binding.keysLoadingLayout.visibility = View.VISIBLE
                }
            }

        })
        vm.indicators["generating"]!!.observe(viewLifecycleOwner){loading->
            loading?.let{
                if(!it){
                    if(type == "ALL" ||type == "NOT_USED"){
                      adapter.notifyDataSetChanged()
                    }
                }
            }

        }
    }
}
package com.sparktech.studyzoneadmin.user

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.UserDetailsTabLayoutBinding

class UserDownloadsTabFragment: Fragment() {
    private lateinit var binding:UserDetailsTabLayoutBinding
    private lateinit var vm:UsersViewModel
    private lateinit var adapter: UserDownloadsFragmentAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.user_details_tab_layout,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(UsersViewModel::class.java)
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val bundle = requireArguments()
        val adminId = sp.getString("adminId","")
        val user = bundle.getString("user")
        vm.fetchUserDownloads(adminId!!,user!!)
        adapter  = UserDownloadsFragmentAdapter()
        setUpObservers()
    }
    private fun setUpObservers(){
        vm.isLoadingDownloads.observe(viewLifecycleOwner,{loading->
            loading?.let {
                if(!it){
                   initRcv()
                    Log.i("DOWNLOADS",vm.userDownloads.toString())
                }else{
                //show spinner
                }
            }
        })
    }
    private fun initRcv(){
        binding.userDetailsRcv.adapter = adapter
        adapter.submitList(vm.userDownloads)
    }
}
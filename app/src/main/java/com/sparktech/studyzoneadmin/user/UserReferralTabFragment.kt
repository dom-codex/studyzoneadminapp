package com.sparktech.studyzoneadmin.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.UserDetailsTabLayoutBinding

class UserReferralTabFragment:Fragment() {
    private lateinit var binding:UserDetailsTabLayoutBinding
    private lateinit var vm:UsersViewModel
    private lateinit var adapter:UserReferralAdapter
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
        adapter = UserReferralAdapter()
        val sp = requireContext().getSharedPreferences("AdminDetails",Context.MODE_PRIVATE)
        val bundle = requireArguments()
        val adminId = sp.getString("adminId","")
        val user = bundle.getString("user")
        vm.fetchUserReferral(adminId!!,user!!)
        setUpObservers()
    }
    private  fun setUpObservers(){
        vm.isLoadingReferral.observe(viewLifecycleOwner,{loading->
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
        binding.userDetailsRcv.adapter = adapter
        adapter.submitList(vm.userReferral)
    }
}
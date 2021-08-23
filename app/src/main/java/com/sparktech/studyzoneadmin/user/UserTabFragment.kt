package com.sparktech.studyzoneadmin.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.UsersFragmentLayoutBinding
import com.sparktech.studyzoneadmin.main_menu.MainMenuViewModel

class UserTabFragment:Fragment() {
  private lateinit var binding:UsersFragmentLayoutBinding
  private lateinit var vm:UsersViewModel
  private lateinit var adapter: UsersAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.users_fragment_layout,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(UsersViewModel::class.java)
        adapter = UsersAdapter()
        setObservers()
    }
    private fun setObservers(){
        vm.isLoading.observe(viewLifecycleOwner,{loading->
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
     binding.userRcv.adapter = adapter
     adapter.submitList(vm.users)
    }
}
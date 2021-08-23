package com.sparktech.studyzoneadmin.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.StudentsLayoutBinding

class UserFragment:Fragment() {
    private lateinit var binding:StudentsLayoutBinding
    private lateinit var vm:UsersViewModel
    private val tabtitle = listOf("ACTIVATED","ALL","NOT ACTIVATED","BLOCKED")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater,R.layout.students_layout,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(UsersViewModel::class.java)
        val sp = requireContext().getSharedPreferences("AdminDetails",Context.MODE_PRIVATE)
        val adminId = sp.getString("adminId","")
        vm.fetchSchool(adminId!!)
        val pagerAdapter = UsersTabFragmentAdapter(this)
        binding.userPager.adapter = pagerAdapter
        TabLayoutMediator(binding.userTab,binding.userPager){tab,pos->
          val title = tabtitle.get(pos)
            tab.text = title
        }.attach()
    }
}
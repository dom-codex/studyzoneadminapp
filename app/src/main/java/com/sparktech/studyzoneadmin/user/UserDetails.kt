package com.sparktech.studyzoneadmin.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.UserDetailsLayoutBinding

class UserDetails: Fragment() {
    private lateinit var binding:UserDetailsLayoutBinding
    private val tabTitle = listOf("Referrals","Transaction")
    private lateinit var vm:UsersViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.user_details_layout,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pagerAdapter = UserDetailsTabFragmentAdapter(this,requireArguments())
        binding.studentPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout2,binding.studentPager){tab,position->
            val title = tabTitle[position]
            tab.text = title
        }.attach()
        //retrieve name , email , phone , earnings,login status , activation status
        val bundle = requireArguments()
        val name = bundle.getString("name")
        val email = bundle.getString("email")
        val phone = bundle.getString("phone")
        val earnings = bundle.getString("earnings")
        val isLoggedIn = bundle.getBoolean("loggedIn")
        val isActivated = bundle.getBoolean("activated")
        val user = bundle.getString("user","")
        val isBlocked = bundle.getBoolean("isBlocked",false)
        binding.apply {
            userName.text = name
            phoneNo.text = phone
            emailValue.text = email
            moneyValue.text = earnings
            loggedInTxt.text = if(isLoggedIn)"LoggedIn" else "not loggedIn"
            activatedText.text = if(isActivated)"Activated" else "not Activated"
            binding.userMoreFab.setOnClickListener {
              parentFragmentManager.let {
                  UserBottomSheet.getBottomSheet(user,isBlocked).apply {
                      show(it,"userBottom")
                  }
              }
            }

        }
    }
}
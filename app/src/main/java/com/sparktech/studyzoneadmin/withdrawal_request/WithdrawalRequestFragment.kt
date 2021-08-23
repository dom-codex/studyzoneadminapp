package com.sparktech.studyzoneadmin.withdrawal_request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.WithdrawalRequestLayoutBinding

class WithdrawalRequestFragment: Fragment() {
private lateinit var binding:WithdrawalRequestLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.withdrawal_request_layout,container,false)

        return binding.root
    }
}
package com.sparktech.studyzoneadmin.withdrawal_request

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class WithdrawalTabAdapter(f: Fragment) : FragmentStateAdapter(f) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return WithdrawalTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type", "PENDING")
                }
            }
            1 -> return WithdrawalTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type", "APPROVED")
                }
            }
            2 -> return WithdrawalTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type", "DECLINED")
                }
            }
            else-> return WithdrawalTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type", "PENDING")
                }
            }
        }
    }
}
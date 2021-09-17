package com.sparktech.studyzoneadmin.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class UserDetailsTabFragmentAdapter(fragment: Fragment, val bundle: Bundle) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
         return when (position) {

            1 -> UserTransactionTabFragment().apply {
                arguments = bundle
            }
            0-> UserReferralTabFragment().apply {
                arguments = bundle
            }
            else -> UserReferralTabFragment().apply {
                arguments = bundle
            }
        }
    }
}
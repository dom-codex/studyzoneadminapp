package com.sparktech.studyzoneadmin.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class UserDetailsTabFragmentAdapter(fragment: Fragment, val bundle: Bundle) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
         return when (position) {
            0 -> UserDownloadsTabFragment().apply {
                arguments = bundle
            }
            1 -> UserReferralTabFragment().apply {
                arguments = bundle
            }
            2 -> UserTransactionTabFragment().apply {
                arguments = bundle
            }
            else -> UserDownloadsTabFragment().apply {
                arguments = bundle
            }
        }
    }
}
package com.sparktech.studyzoneadmin.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class UsersTabFragmentAdapter(val fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserAllTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type", "ACTIVATED")
                }
            }
            1 -> UserAllTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type", "ALL")
                }
            }
            2 -> UserAllTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type", "NOT_ACTIVATED")
                }
            }
            3 -> UserAllTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type", "BLOCKED")
                }
            }
            else -> UserAllTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type", "ACTIVATED")
                }
            }
        }
    }
}
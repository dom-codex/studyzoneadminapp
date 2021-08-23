package com.sparktech.studyzoneadmin.user

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class UsersTabFragmentAdapter(val fragment:Fragment):FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return UserTabFragment()
    }
}
package com.sparktech.studyzoneadmin.vendor

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class VendorKeysFragmentAdapter(val f: Fragment) : FragmentStateAdapter(f) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return VendorKeysTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type", "USED")
                    putAll(f.arguments)
                }
            }
        }
        return VendorKeysTabFragment().apply {
            arguments = Bundle().apply {
                putString("type", "NOT_USED")
                putAll(f.arguments)
            }
        }
    }
}
package com.sparktech.studyzoneadmin.lisense_key

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class LisenseKeyFragmentAdapter(f:Fragment):FragmentStateAdapter(f) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->  LisenseKeyTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type","USED")
                }
            }
            1->  LisenseKeyTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type","ALL")
                }
            }
            2->  LisenseKeyTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type","NOT_USED")
                }
            }
            else ->   LisenseKeyTabFragment().apply {
                arguments = Bundle().apply {
                    putString("type","USED")
                }
            }
        }

    }
}
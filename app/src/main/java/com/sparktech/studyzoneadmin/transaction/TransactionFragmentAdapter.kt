package com.sparktech.studyzoneadmin.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TransactionFragmentAdapter(private val f:Fragment):FragmentStateAdapter(f) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->TransactionTabFragment().apply {
                arguments = Bundle().apply {
                    putString("filter","ALL")
                }
            }
            1->TransactionTabFragment().apply {
                arguments = Bundle().apply {
                    putString("filter","CARD")
                }
            }
            2->TransactionTabFragment().apply {
                arguments = Bundle().apply {
                    putString("filter","KEY")
                }
            }
            3->TransactionTabFragment().apply {
                arguments = Bundle().apply {
                    putString("filter","FREETRIAL")
                }
            }
            else->TransactionTabFragment().apply {
                arguments = Bundle().apply {
                    putString("filter","CARD")
                }
            }
        }
    }
}
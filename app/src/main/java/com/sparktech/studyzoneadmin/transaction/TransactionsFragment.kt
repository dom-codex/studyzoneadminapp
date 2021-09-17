package com.sparktech.studyzoneadmin.transaction

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.TransactionLayoutBinding

class TransactionsFragment : Fragment() {
    private lateinit var binding: TransactionLayoutBinding
    val data = listOf("ALL", "CARD", "KEY", "FREETRIAL")

    private lateinit var vm: TransactionViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.transaction_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val adminId = sp.getString("adminId", "")
        val fac = TransactionViewModelFactory(requireActivity().application)
        vm = ViewModelProvider(requireActivity(), fac).get(TransactionViewModel::class.java)
        // initChips()
        initTabs()
    }

    private fun initTabs() {
        parentFragmentManager.let { fm ->
            val ft = fm.beginTransaction()
            ft.setReorderingAllowed(true)
            ft.replace(
                R.id.frag_host,
                TransactionTabFragment::class.java,
                Bundle().apply {
                    putString("filter", "ALL")
                })
            ft.commitAllowingStateLoss()
        }
        binding.txTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        parentFragmentManager.let { fm ->
                            val ft = fm.beginTransaction()
                            ft.setReorderingAllowed(true)
                            ft.replace(
                                R.id.frag_host,
                                TransactionTabFragment::class.java,
                                Bundle().apply {
                                    putString("filter", "ALL")
                                })
                            ft.commitAllowingStateLoss()
                        }
                    }
                    1 -> {
                        parentFragmentManager.let { fm ->
                            val ft = fm.beginTransaction()
                            ft.setReorderingAllowed(true)
                            ft.replace(
                                R.id.frag_host,
                                TransactionTabFragment::class.java,
                                Bundle().apply {
                                    putString("filter", "CARD")
                                })
                            ft.commitAllowingStateLoss()
                        }
                    }
                    2 -> {
                        parentFragmentManager.let { fm ->
                            val ft = fm.beginTransaction()
                            ft.setReorderingAllowed(true)
                            ft.replace(
                                R.id.frag_host,
                                TransactionTabFragment::class.java,
                                Bundle().apply {
                                    putString("filter", "KEY")
                                })
                            ft.commitAllowingStateLoss()
                        }
                    }
                    3 -> {
                        parentFragmentManager.let { fm ->
                            val ft = fm.beginTransaction()
                            ft.setReorderingAllowed(true)
                            ft.replace(
                                R.id.frag_host,
                                TransactionTabFragment::class.java,
                                Bundle().apply {
                                    putString("filter", "FREE")
                                })
                            ft.commitAllowingStateLoss()
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        // val pagerAdapter = TransactionFragmentAdapter(this)
        // binding.txPager.adapter = pagerAdapter

        // binding.txPager.isUserInputEnabled = false
        /*  TabLayoutMediator(binding.txTab,binding.txPager){tab,position->
              tab.text = data[position]
          }.attach()*/

    }

    private fun initChips() {
        val data = resources.getStringArray(R.array.chip_items)
        for (text in data) {
            val chip =
                layoutInflater.inflate(R.layout.chip_layout_item, binding.chipCont, false) as Chip
            chip.text = text
            chip.setOnClickListener {
                when (text) {
                    "CARD" -> binding.txPager.currentItem = 3
                }
            }
            binding.chipGroup.addView(chip)
            val chi = binding.chipGroup[0] as Chip
            chi.isChecked = true
        }
    }
}
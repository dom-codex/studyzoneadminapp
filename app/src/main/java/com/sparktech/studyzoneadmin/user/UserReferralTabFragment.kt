package com.sparktech.studyzoneadmin.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.UserDetailsTabLayoutBinding
import com.sparktech.studyzoneadmin.helpers.KeyComposer
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.models.Referral

class UserReferralTabFragment : Fragment() {
    private lateinit var binding: UserDetailsTabLayoutBinding
    private lateinit var vm: UsersViewModel
    private lateinit var adapter: UserReferralAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.user_details_tab_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(UsersViewModel::class.java)
        adapter = UserReferralAdapter()
        binding.userDetailsRcv.adapter = adapter
        binding.userDetailsRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.userDetailsRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchReferrals
            )
        )
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val bundle = requireArguments()
        val adminId = sp.getString("adminId", "")
        val user = bundle.getString("user")
        if (vm.pages[KeyComposer.getUserDetailsReferralKey(bundle)] == null) {
            //init hashes
            vm.userReferral[KeyComposer.getUserDetailsReferralKey(bundle)] = mutableListOf()

            vm.indicators[KeyComposer.getUserDetailsLoadingReferrals(bundle)] =
                MutableLiveData(false)
            vm.pages[KeyComposer.getUserCurrentReferralPage(bundle)] = 0
            vm.fetchUserReferral(
                adminId!!,
                user!!,
                bundle,
                vm.pages[KeyComposer.getUserCurrentReferralPage(requireArguments())]!!
            ) { err, msg ->
                requireActivity().runOnUiThread {
                    if (err) {
                        Toast.makeText(requireContext(), "an error occurred", Toast.LENGTH_SHORT)
                            .show()

                    } else {
                        msg?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        setUpObservers()
    }

    //scroll lambdas
    private val getLoadingState: () -> Boolean = {
        vm.indicators[KeyComposer.getUserDetailsLoadingReferrals(requireArguments())]!!.value!!
    }
    private val getListSize: () -> Int = {
        vm.userReferral[KeyComposer.getUserDetailsReferralKey(requireArguments())]!!.size
    }
    private val setLoaderItem: () -> Unit = {
        val bundle = requireArguments()
        val loaderItem = Referral("", "", "", "", true)
        vm.userReferral[KeyComposer.getUserDetailsReferralKey(bundle)]!!.add(loaderItem)
        adapter.notifyItemInserted(vm.users[KeyComposer.getUserDetailsReferralKey(bundle)]!!.size - 1)
    }
    private val fetchReferrals = {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val bundle = requireArguments()
        val adminId = sp.getString("adminId", "")
        val user = bundle.getString("user")
        vm.fetchUserReferral(
            adminId!!,
            user!!,
            bundle,
            vm.pages[KeyComposer.getUserCurrentReferralPage(bundle)]!!
        ) { err, msg ->
            if (err) {
                Toast.makeText(requireContext(), "an error occurred", Toast.LENGTH_SHORT).show()
                return@fetchUserReferral
            }
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setUpObservers() {
        val bundle = requireArguments()
        vm.indicators[KeyComposer.getUserDetailsLoadingReferrals(bundle)]!!.observe(
            viewLifecycleOwner,
            { loading ->
                loading?.let {
                    binding.userReferralLoader.visibility = View.GONE
                    if (!it) {
                        binding.userReferralNoData.visibility = View.GONE
                        initRcv()
                    } else {
                        //show spinner
                        if (vm.userReferral[KeyComposer.getUserDetailsReferralKey(bundle)]!!.size == 0) {
                            binding.userReferralLoader.visibility = View.VISIBLE
                            binding.userReferralNoData.visibility = View.GONE
                        }
                    }
                }
            })
    }

    private fun initRcv() {
        if (vm.userReferral[KeyComposer.getUserDetailsReferralKey(requireArguments())]!!.size > 0) {
            binding.userDetailsRcv.visibility = View.VISIBLE
            adapter.submitList(
                vm.userReferral[KeyComposer.getUserDetailsReferralKey(
                    requireArguments()
                )]!!
            )
            return
        }
        binding.userReferralNoData.visibility = View.VISIBLE
    }
}
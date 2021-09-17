package com.sparktech.studyzoneadmin.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.UsersFragmentLayoutBinding
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.models.User

/*class UserBlockedFragment : Fragment() {

    private lateinit var binding: UsersFragmentLayoutBinding
    private lateinit var vm: UsersViewModel
    private lateinit var adapter: UsersAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.users_fragment_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(UsersViewModel::class.java)
        adapter = UsersAdapter()
        binding.userRcv.adapter = adapter
        binding.userRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.userRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchUsers
            )
        )
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val adminId = sp.getString("adminId", "")
        if (vm.currentBlockedUsersPage == 0) {
            vm.fetchUsers(adminId!!, "BLOCKED", vm.currentBlockedUsersPage)
        }
        setObservers()
    }

    //scroll lambdas
    private val getLoadingState: () -> Boolean = {
        vm.isLoadingBlockedUsers.value!!
    }
    private val getListSize: () -> Int = {
        vm.blockedUsers.size
    }
    private val setLoaderItem: () -> Unit = {
        val loaderItem = User(
            "", "", "", "", "", "",
            false, false, 0, 0, 0,
            false, isLoading = true
        )
        vm.blockedUsers.add(loaderItem)
        adapter.notifyItemInserted(vm.blockedUsers.size - 1)
    }
    private val fetchUsers = {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val adminId = sp.getString("adminId", "")
        vm.fetchUsers(adminId!!, "BLOCKED", vm.currentBlockedUsersPage)
    }

    private fun setObservers() {
        vm.isLoadingBlockedUsers.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.userLoader.visibility = View.GONE
                    if (vm.blockedUsers.size > 0) {
                        binding.noUserData.visibility = View.GONE
                        binding.userRcv.visibility = View.VISIBLE
                        adapter.submitList(vm.blockedUsers)
                        adapter.notifyDataSetChanged()
                        return@let
                    }
                    binding.noUserData.visibility = View.VISIBLE
                } else {
                    if (vm.blockedUsers.size == 0) {
                        binding.userLoader.visibility = View.VISIBLE
                        binding.userRcv.visibility = View.GONE
                        binding.noUserData.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun initRcv() {
        adapter.submitList(vm.users)
    }
}*/
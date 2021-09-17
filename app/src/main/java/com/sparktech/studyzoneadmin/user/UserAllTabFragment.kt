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
import com.sparktech.studyzoneadmin.databinding.UsersFragmentLayoutBinding
import com.sparktech.studyzoneadmin.helpers.BundleExtractor
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.models.User

class UserAllTabFragment : Fragment() {

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
        val bundle = requireArguments()
        val usersCategory = bundle.getString("type")
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
        if (vm.pages[usersCategory] == null) {
            //init hashes
            vm.pages[usersCategory!!] = 0
            vm.users[usersCategory] = mutableListOf()
            vm.indicators["loading${usersCategory}"] = MutableLiveData(false)
            vm.fetchUsers(adminId!!, usersCategory, vm.pages[usersCategory]!!) { err, msg ->
                if (!err) {
                    msg?.let {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    }
                    return@fetchUsers
                }
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "an error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
        setObservers()
    }

    //scroll lambdas
    private val getLoadingState: () -> Boolean = {
        val usersCategory = BundleExtractor.getUsersType(requireArguments())
        vm.indicators["loading$usersCategory"]!!.value!!
    }
    private val getListSize: () -> Int = {
        val usersCategory = BundleExtractor.getUsersType(requireArguments())
        vm.users[usersCategory]!!.size
    }
    private val setLoaderItem: () -> Unit = {
        val usersCategory = BundleExtractor.getUsersType(requireArguments())
        val loaderItem = User(
            "", "", "", "", "", "",
            false, false, 0, 0, 0,
            false, isLoading = true
        )
        vm.users[usersCategory]!!.add(loaderItem)
        adapter.notifyItemInserted(vm.users[usersCategory]!!.size-1 )
    }
    private val fetchUsers = {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val adminId = sp.getString("adminId", "")
        val usersCategory = BundleExtractor.getUsersType(requireArguments())
        vm.fetchUsers(adminId!!, usersCategory, vm.pages[usersCategory]!!) { err, msg ->
            if (!err) {
                msg?.let {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
                return@fetchUsers
            }
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "an error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setObservers() {
        val usersCategory = BundleExtractor.getUsersType(requireArguments())
        vm.indicators["loading$usersCategory"]!!.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.userLoader.visibility = View.GONE
                    if (vm.users[usersCategory]!!.size > 0) {
                        binding.noUserData.visibility = View.GONE
                        binding.userRcv.visibility = View.VISIBLE
                        adapter.submitList(vm.users[usersCategory])
                        adapter.notifyDataSetChanged()
                        return@let
                    }
                    binding.noUserData.visibility = View.VISIBLE
                } else {
                    if (vm.users[usersCategory]!!.size == 0) {
                        binding.userLoader.visibility = View.VISIBLE
                        binding.userRcv.visibility = View.GONE
                        binding.noUserData.visibility = View.GONE
                    }
                }
            }
        })
    }
}
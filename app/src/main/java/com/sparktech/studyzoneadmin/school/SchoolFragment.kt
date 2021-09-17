package com.sparktech.studyzoneadmin.school

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.Auth
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.SchoolLayoutBinding
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.helpers.getSchoolType
import com.sparktech.studyzoneadmin.main_menu.MainMenuViewModel
import com.sparktech.studyzoneadmin.main_menu.SwipeController
import com.sparktech.studyzoneadmin.models.School
import com.sparktech.studyzoneadmin.models.SchoolAdapterData
import com.sparktech.studyzoneadmin.network.Network
import com.sparktech.studyzoneadmin.request_models.SchoolToDelete
import kotlinx.coroutines.*

class SchoolFragment : Fragment() {
    private lateinit var binding: SchoolLayoutBinding
    private lateinit var vm: MainMenuViewModel
    private lateinit var adapter: SchoolAdapter
    private val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)
    private var schoolIndex = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.school_layout, container, false)
        return binding.root
    }

    private fun initViews(schType: String? = "Universities") {
        val swipeController = object : SwipeController() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        //get adapter position
                        val postion = viewHolder.adapterPosition
                        schoolIndex = postion
                        var school: School? = null
                        if (schType == "Universities") {
                            val adapterdata = vm.uni.get(postion)
                            school = adapterdata.school
                        } else {
                            val adapterdata = vm.poly.get(postion)
                            school = adapterdata.school
                        }
                        //show comfirmation Dialog
                        AlertDialog.Builder(requireContext())
                            .setTitle("Comfirm")
                            .setMessage("Are you sure you want to delete ${school?.name}(${school?.nameAbbr})")
                            .setNegativeButton("NO", dialogDontDeleteHandler)
                            .setPositiveButton("YES", DialogDeleteHandler(school!!))
                            .show()
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeController)
        touchHelper.attachToRecyclerView(binding.schoolRcv)
        binding.schoolRcv.adapter = adapter
        binding.schType.text = schType
        binding.schSearchBtn.setOnClickListener {
            this.parentFragmentManager.let {
                BottomFragment.newInstance(
                    schType
                ).apply {

                    show(it, "bottomsheet")
                }
            }
        }
    }

    private fun setUpPolyObservers() {
        vm.loadingPoly.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.schLoader.visibility = View.GONE
                    if (vm.poly.size > 0) {
                        binding.noSchoolData.visibility = View.GONE
                        binding.schoolRcv.visibility = View.VISIBLE
                        binding.schoolValue.text = vm.poly.size.toString()
                        adapter.submitList(vm.poly)
                        adapter.notifyDataSetChanged()
                        return@let
                    }
                    binding.noSchoolData.visibility = View.VISIBLE
                } else {
                    if (vm.poly.size == 0) {
                        binding.schLoader.visibility = View.VISIBLE
                        binding.noSchoolData.visibility = View.GONE
                    }
                }
            }
        })
        vm.isNewSchoolAdded.observe(viewLifecycleOwner, { isNewSchool ->
            isNewSchool?.let {
                if (it) {
                    binding.loadingLayout.visibility = View.GONE
                    binding.noSchoolData.visibility = View.GONE
                    binding.schLoader.visibility = View.GONE
                    binding.schoolRcv.visibility = View.VISIBLE
                    val school = vm.newSchool
                    vm.poly.add(SchoolAdapterData(school))
                  //  adapter.notifyItemInserted(vm.poly.size-1)
                    adapter.notifyDataSetChanged()
                    // adapter.submitList(vm.adapterData)
                    vm.notifyNewSchoolAdded()
                    binding.schoolValue.text = vm.poly.size.toString()
                }
            }

        })
        vm.creatingPoly.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.loadingLayout.visibility = View.GONE
                    adapter.notifyItemInserted(vm.poly.size-1)
                } else {
                    binding.loadingLayout.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setUpObservers() {
        vm.isFetchingSchool.observe(viewLifecycleOwner, { fetching ->
            fetching?.let {
                if (!it) {
                    binding.schLoader.visibility = View.GONE
                    if (vm.uni.size > 0) {
                        binding.noSchoolData.visibility = View.GONE
                        binding.schoolRcv.visibility = View.VISIBLE
                        binding.schoolValue.text = vm.uni.size.toString()
                        adapter.submitList(vm.uni)
                        adapter.notifyDataSetChanged()
                        return@let
                    }
                    binding.noSchoolData.visibility = View.VISIBLE
                } else {
                    if (vm.uni.size == 0) {
                        binding.schLoader.visibility = View.VISIBLE
                        binding.noSchoolData.visibility = View.VISIBLE
                    }
                }
            }
        })
        //observe new scool
        vm.isNewSchoolAdded.observe(viewLifecycleOwner, { isNewSchool ->
            isNewSchool?.let {
                if (it) {
                    binding.loadingLayout.visibility = View.GONE
                    binding.noSchoolData.visibility = View.GONE
                    binding.schLoader.visibility = View.GONE
                    binding.schoolRcv.visibility = View.VISIBLE
                    val school = vm.newSchool
                    vm.uni.add(SchoolAdapterData(school))
                    adapter.notifyDataSetChanged()

                    // adapter.submitList(vm.adapterData)
                    vm.notifyNewSchoolAdded()
                    binding.schoolValue.text = vm.uni.size.toString()
                }
            }

        })
        vm.creatingUni.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.loadingLayout.visibility = View.GONE
                } else {
                    binding.loadingLayout.visibility = View.VISIBLE
                }
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init view model
        vm = ViewModelProvider(requireActivity()).get(MainMenuViewModel::class.java)
        adapter = SchoolAdapter()
        adapter.submitList(mutableListOf())
        binding.schoolRcv.adapter = adapter
        binding.schoolRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.schoolRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchMoreSchool
            )
        )
        //get arguments
        val bundle = requireArguments()
        val schType = bundle.getString("schoolType")
        when (schType) {
            "Universities" -> {
                setUpObservers()
                if (vm.currentUniversityPage == 0) {
                    vm.fetchSchools(vm.currentUniversityPage, "university")
                } else {
                    adapter.submitList(vm.poly)
                }
            }
            "Polytechnics" -> {
                setUpPolyObservers()
                if (vm.currentPolyPage == 0) {
                    vm.fetchSchools(vm.currentPolyPage, "polytechnic")
                } else {
                    adapter.submitList(vm.poly)
                }
            }
        }
        initViews(schType)

        //get list of schools from viewModel
    }

    //loader lambadas
    private val getLoadingState: () -> Boolean = {
        if (getSchoolType(requireArguments()) == "Universities") {
            vm.isFetchingSchool.value!!
        } else {
            vm.loadingPoly.value!!
        }
    }
    private val getListSize: () -> Int = {
        if (getSchoolType(requireArguments()) == "Universities") {
            vm.uni.size
        } else {
            vm.poly.size
        }
    }
    private val setLoaderItem: () -> Unit = {
        val loaderItem = SchoolAdapterData(null, true)
        if (getSchoolType(requireArguments()) == "Universities") {
            vm.uni.add(loaderItem)
            adapter.notifyItemInserted(vm.uni.size - 1)
        } else {
            vm.poly.add(loaderItem)
            adapter.notifyItemInserted(vm.poly.size - 1)
        }
    }
    private val fetchMoreSchool = {
        if (getSchoolType(requireArguments()) == "Universities") {
            vm.fetchSchools(vm.currentUniversityPage, "university")
        } else {
            vm.fetchSchools(vm.currentPolyPage, "polytechnic")

        }
    }

    inner class DialogDeleteHandler(val school: School) : DialogInterface.OnClickListener {
        override fun onClick(p0: DialogInterface?, p1: Int) {
            try {
                val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
                val email = sp.getString("email", null)
                val adminId = sp.getString("adminId", null)
                if (email == null || adminId == null) {
                    return kickUserOut()
                }
                networkScope.launch {
                    school?.let {
                        val res =
                            Network.apiService.deleteSchool(SchoolToDelete(email, adminId, school))
                        if (res.code == 200) {
                            withContext(Dispatchers.Main) {
                                if (school.type == "university") {
                                    vm.uni.removeAt(schoolIndex)
                                    binding.schoolValue.text = vm.uni.size.toString()
                                } else {
                                    vm.poly.removeAt(schoolIndex)
                                    binding.schoolValue.text = vm.poly.size.toString()
                                }
                                adapter.notifyItemRemoved(schoolIndex)
                            }
                        } else if (res.code == 404) {
                            //clear shared preferences and send to auth screen
                            kickUserOut()
                        } else {
                            //reset data if an error occurs
                            adapter.notifyDataSetChanged()
                            Toast.makeText(
                                requireContext(),
                                "an error occurred, try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private val dialogDontDeleteHandler = DialogInterface.OnClickListener { p0, p1 -> adapter.notifyDataSetChanged() }

    private fun kickUserOut() {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        sp.edit().apply {
            putString("email", null)
            putString("adminId", null)
            putBoolean("loggedIn", false)
            apply()
        }
        requireContext().startActivity(Intent(requireContext(), Auth::class.java))
        requireActivity().finishAfterTransition()
    }
}
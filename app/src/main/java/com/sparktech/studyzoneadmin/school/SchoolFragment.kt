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
import com.sparktech.studyzoneadmin.main_menu.MainMenuViewModel
import com.sparktech.studyzoneadmin.main_menu.SwipeController
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
                        val adapterdata = vm.adapterData.get(postion)
                        val school = adapterdata.school
                        //show comfirmation Dialog
                        AlertDialog.Builder(requireContext())
                            .setTitle("Comfirm")
                            .setMessage("Are you sure you want to delete ${school?.name}(${school?.nameAbbr})")
                            .setNegativeButton("NO",dialogDontDeleteHandler)
                            .setPositiveButton("YES",dialogDeleteHandler)
                            .show()
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeController)
        touchHelper.attachToRecyclerView(binding.schoolRcv)
        adapter = SchoolAdapter()
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

    private fun setUpObservers() {
        vm.isFetchingSchool.observe(viewLifecycleOwner, { fetching ->
            fetching?.let {
                if (!it) {
                    //get fetched school
                    val schools = vm.adapterData
                    //create list for adapter data
                    val adapterData = mutableListOf<SchoolAdapterData>()
                    //if data is not empty initialize adapter
                    if (schools.size > 0) {
                        binding.schoolValue.text = schools.size.toString()
                        adapter.submitList(schools)
                    }
                } else {
                    //show loader
                }
            }
        })
        //observe new scool
        vm.isNewSchoolAdded.observe(viewLifecycleOwner, { isNewSchool ->
            isNewSchool?.let {
                if (it) {
                    val school = vm.newSchool
                    vm.adapterData.add(SchoolAdapterData(school))
                    adapter.submitList(vm.adapterData)
                    adapter.notifyItemInserted(vm.adapterData.size - 1)
                    vm.notifyNewSchoolAdded()
                    binding.schoolValue.text = vm.adapterData.size.toString()
                }
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init view model
        vm = ViewModelProvider(requireActivity()).get(MainMenuViewModel::class.java)
        //get arguments
        val bundle = requireArguments()
        val schType = bundle.getString("schoolType")
        when(schType){
            "Universities"->vm.fetchSchools(0,"university")
            "Polytechnics"->vm.fetchSchools(0,"polytechnic")
        }
        initViews(schType)
        setUpObservers()
        //get list of schools from viewModel
    }

    private val dialogDeleteHandler = object : DialogInterface.OnClickListener {
        override fun onClick(p0: DialogInterface?, p1: Int) {
            try{
                val sp = requireContext().getSharedPreferences("AdminDetails",Context.MODE_PRIVATE)
                val email = sp.getString("email",null)
                val adminId = sp.getString("adminId",null)
                if(email == null ||adminId == null){
                  return kickUserOut()
                }
            val adapterdata = vm.adapterData.get(schoolIndex)
            val school = adapterdata.school
            networkScope.launch {
                school?.let {
                    val res = Network.apiService.deleteSchool(SchoolToDelete(email, adminId, school))
                    if(res.code==200){
                        withContext(Dispatchers.Main){
                            vm.adapterData.removeAt(schoolIndex)
                            adapter.notifyItemRemoved(schoolIndex)
                            binding.schoolValue.text = vm.adapterData.size.toString()
                        }
                    }else if(res.code==404){
                        //clear shared preferences and send to auth screen
                      kickUserOut()
                    }else{
                        adapter.notifyDataSetChanged()
                        Toast.makeText(requireContext(),"an error occurred, try again",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }catch (e:Exception) {
            e.printStackTrace()
            }

            }
    }
    private val dialogDontDeleteHandler = object:DialogInterface.OnClickListener{
        override fun onClick(p0: DialogInterface?, p1: Int) {
            adapter.notifyDataSetChanged()
        }
    }
    private fun kickUserOut(){
        val sp = requireContext().getSharedPreferences("AdminDetails",Context.MODE_PRIVATE)
        sp.edit().apply{
            putString("email",null)
            putString("adminId",null)
            putBoolean("loggedIn",false)
            apply()
        }
        requireContext().startActivity(Intent(requireContext(), Auth::class.java))
        requireActivity().finishAfterTransition()
    }
}
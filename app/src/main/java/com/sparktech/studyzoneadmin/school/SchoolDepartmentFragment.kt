package com.sparktech.studyzoneadmin.school

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.SchoolDetailsBinding

class SchoolDepartmentFragment: Fragment() {
    private lateinit var binding:SchoolDetailsBinding
    private lateinit var vm:SchoolDetailsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.school_details,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        vm = ViewModelProvider(requireActivity()).get(SchoolDetailsViewModel::class.java)
        val sp = requireContext().getSharedPreferences("AdminDetails",Context.MODE_PRIVATE)
        val schHash = bundle.getString("sch")
        val facultyHash = bundle.getString("fid")
        val adminId = sp.getString("adminId","")
        vm.fetchDepartment(schHash!!,facultyHash!!,adminId!!,0)
        initViews(bundle)
        setUpObservers(bundle)
    }
    private fun initViews(bundle:Bundle){
        val schName = bundle.getString("schName")
        val deparments = bundle.getString("departments")
        val facultyName = bundle.getString("fn")
        binding.schoolDetailsName.text = schName
        binding.schoolDetailsNumber.text = "$deparments departments in $facultyName"
    }
    private fun setUpObservers(bundle:Bundle){
        vm.isLoadingDepartments.observe(viewLifecycleOwner,{loading->
           loading?.let {
               if(!it){
                   initRcv(bundle)
               }else{
                   //show spinner
               }
           }
        })
    }
    private fun initRcv(bundle: Bundle){
        val schHash = bundle.getString("sch")
        val facultyHash = bundle.getString("fid")
        val schName = bundle.getString("schName")
        val facultyName = bundle.getString("fn")
        val adapter = SchoolDepartmentAdapter(SchInfo(schName!!,schHash!!,facultyName!!,facultyHash!!))
        binding.schoolDetailsRcv.adapter = adapter
        adapter.submitList(vm.departments)
    }
}
data class SchInfo(val schName:String,val sid:String,val faculty:String,val fid:String)
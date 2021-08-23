package com.sparktech.studyzoneadmin.school

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.SchoolDetailsBinding
import com.sparktech.studyzoneadmin.main_menu.MainMenuViewModel

class SchoolDetails: Fragment() {
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
        vm = ViewModelProvider(requireActivity()).get(SchoolDetailsViewModel::class.java)
        val bundle = requireArguments()
        val schName = bundle.getString("schName")
        val faculties = bundle.getString("facultites")
        val sch = bundle.getString("schHash")
        getFaculties(sch!!)
        setUpObserver(sch,schName!!)
        binding.schoolDetailsName.text = schName
        binding.schoolDetailsNumber.text = faculties
    }
    private fun getFaculties(sch:String,page:Int=0){
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val adminId = sp.getString("adminId",null)
        vm.fetchFaculty(sch,adminId!!,page)
    }
    private fun setUpObserver(sch:String,name:String){
        vm.isLoadingFaculty.observe(viewLifecycleOwner,{loading->
            loading?.let{
                if(!it){
                   initRecyclerView(sch,name)
                }else{
                    //show spinner
                }
            }
        })
    }
    private fun initRecyclerView(sch:String,name:String){
        val adapter = SchoolDetailsAdapter(sch,name)
        binding.schoolDetailsRcv.adapter = adapter
        adapter.submitList(vm.faculties)
    }
}
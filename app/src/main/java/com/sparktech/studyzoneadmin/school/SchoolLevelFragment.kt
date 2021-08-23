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
import com.sparktech.studyzoneadmin.databinding.SchoolLevelsLayoutBinding

class SchoolLevelFragment: Fragment() {
    private lateinit var binding:SchoolLevelsLayoutBinding
    private lateinit var vm:SchoolDetailsViewModel
    private val semesters = listOf("first","second")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.school_levels_layout,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        val sp = requireContext().getSharedPreferences("AdminDetails",Context.MODE_PRIVATE)
        vm = ViewModelProvider(requireActivity()).get(SchoolDetailsViewModel::class.java)
        val sch = bundle.getString("schHash")
        val fid = bundle.getString("facultyHash")
        val did = bundle.getString("did")
        val adminId = sp.getString("adminId","")
        vm.fetchLevel(sch!!,fid!!,did!!,adminId!!)
        initViews(bundle)
        setObservers()
    }
    private fun initViews(bundle:Bundle){
        val schName = bundle.getString("schName","")
        //bundle.putString("schHash",sch.sid)
        val facName = bundle.getString("facultyName","")
        //bundle.putString("facultyHash",sch.fid)
        val deptName =bundle.getString("dept","")
       // bundle.putString("did",department.hash)
        binding.schoolLevelDetailsName.text = schName
        binding.schoolLevelDetailsNumber.text = "Levels in $deptName in $facName"
        binding.schoolLevelDetailsTitle.text="$facName faculty"

    }
    private fun setObservers(){
        vm.isLoadingLeve.observe(viewLifecycleOwner,{loading->
            loading?.let {
                if(!it){
                    val levels = vm.adapterData.keys.toList()
                    initList(levels,vm.adapterData)
                }else{
                    //show spinner
                }
            }
        })
    }
    private fun initList(levels:List<String>,data:HashMap<String,List<String>>){
        val adapter = SchoolLevelAdapter(levels,data,vm.levels,requireArguments())
        binding.schoolLevelDetailsExpandable.setAdapter(adapter)
    }
}
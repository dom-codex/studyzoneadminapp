package com.sparktech.studyzoneadmin.past_question

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.SchoolPastQuestionLayoutBinding

class PastQuestionFragment: Fragment(){
    private lateinit var binding:SchoolPastQuestionLayoutBinding
    private lateinit var vm:PastQuestionViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.school_past_question_layout,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(PastQuestionViewModel::class.java)
        val bundle = requireArguments()
        val sp = requireContext().getSharedPreferences("AdminDetails",Context.MODE_PRIVATE)
        val schName = bundle.getString("schName","")
        val schHash = bundle.getString("schHash","")
        val facName = bundle.getString("facultyName","")
        val facultyHash = bundle.getString("facultyHash","")
        val deptName =bundle.getString("dept","")
        val semester = bundle.getString("semester","")
        val level = bundle.getString("level","")
        val did = bundle.getString("did","")
        val lid = bundle.getString("lid","")
        val adminId = sp.getString("adminId","")
        val queryOptions = HashMap<String,String>()
        queryOptions.put("sch",schHash)
        queryOptions.put("fid",facultyHash)
        queryOptions.put("did",did)
        queryOptions.put("lid",lid)
        queryOptions.put("adminId",adminId!!)
        queryOptions.put("semester",semester)
        vm.getPastQuestion(queryOptions,0)
        initViews(schName,deptName,semester,level)
        setUpObservers()
    }
    private fun initViews(schName:String,deptName:String,sem:String,level:String){
        binding.nameOfSch.text = schName
        binding.nameOfDepartment.text = deptName
        binding.nameOfLevel.text = level
        binding.nameOfSemester.text = sem
    }
    private fun setUpObservers(){
        vm.isloading.observe(viewLifecycleOwner,{loading->
            loading?.let {
                if(!it){
                    val adapter = PastQuestionAdapter()
                    binding.pqRcv.adapter = adapter
                    adapter.submitList(vm.pastquestion)
                }else{
                    //show spinner
                }
            }
        })
    }
}
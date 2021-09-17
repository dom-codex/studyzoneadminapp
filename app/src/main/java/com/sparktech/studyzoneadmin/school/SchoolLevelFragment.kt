package com.sparktech.studyzoneadmin.school

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.AddNewSchoolItemBinding
import com.sparktech.studyzoneadmin.databinding.SchoolLevelsLayoutBinding
import com.sparktech.studyzoneadmin.helpers.BundleExtractor
import com.sparktech.studyzoneadmin.request_models.CreateLevelRequestBody
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength

class SchoolLevelFragment : Fragment() {
    private lateinit var binding: SchoolLevelsLayoutBinding
    private lateinit var vm: SchoolDetailsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.school_levels_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        vm = ViewModelProvider(requireActivity()).get(SchoolDetailsViewModel::class.java)
        val sch = bundle.getString("schHash")
        val fid = bundle.getString("facultyHash")
        val did = bundle.getString("did")
        val adminId = sp.getString("adminId", "")
        if (!vm.fetchedLevel) {
            vm.allLevels[did!!] = mutableListOf()
            vm.indicators["loadingLevel${did}"] = MutableLiveData(false)
            vm.indicators["creatingLevel${did}"] = MutableLiveData(false)
            vm.fetchLevel(sch!!, fid!!, did, adminId!!)
        }
        initViews(bundle)
        setObservers()
    }

    private fun initViews(bundle: Bundle) {
        val schName = bundle.getString("schName", "")
        //bundle.putString("schHash",sch.sid)
        val facName = bundle.getString("facultyName", "")
        //bundle.putString("facultyHash",sch.fid)
        val deptName = bundle.getString("dept", "")
        // bundle.putString("did",department.hash)
        binding.schoolLevelDetailsName.text = schName
        binding.schoolLevelDetailsNumber.text = "Levels in $deptName in $facName"
        binding.schoolLevelDetailsTitle.text = "$facName faculty"
        binding.addLevelBtn.setOnClickListener {
            if (vm.indicators["creatingLevel${BundleExtractor.getDepartmentHash(requireArguments())}"]!!.value!!) {
                return@setOnClickListener
            }
            showCreateDialog()
        }

    }

    private fun setObservers() {
        vm.indicators["creatingLevel${BundleExtractor.getDepartmentHash(requireArguments())}"]!!.observe(
            viewLifecycleOwner,
            { loading ->
                loading?.let {
                    if (!it) {
                        binding.newLevelLoadingLayout.visibility = View.GONE
                        if (vm.getCode() == 200) {
                            Toast.makeText(requireContext(), "CREATED", Toast.LENGTH_LONG).show()
                            val data =
                                vm.getAdapterData(BundleExtractor.getDepartmentHash(requireArguments()))
                            val levels = data.keys.toList()
                            initList(levels, data)
                        }
                    } else {
                        //show spinner
                        binding.newLevelLoadingLayout.visibility = View.VISIBLE
                    }
                }

            })
        vm.indicators["loadingLevel${BundleExtractor.getDepartmentHash(requireArguments())}"]!!.observe(
            viewLifecycleOwner,
            { loading ->
                loading?.let {
                    if (!it) {
                        val data =
                            vm.getAdapterData(BundleExtractor.getDepartmentHash(requireArguments()))
                        val levels = data.keys.toList()
                        initList(levels, data)
                    } else {
                        //show spinner
                    }
                }
            })
    }

    private fun initList(levels: List<String>, data: HashMap<String, List<String>>) {
        val adapter = SchoolLevelAdapter(
            levels,
            data,
            vm.allLevels[BundleExtractor.getDepartmentHash(requireArguments())]!!,
            requireArguments()
        )
        binding.schoolLevelDetailsExpandable.setAdapter(adapter)
    }

    private fun showCreateDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        val dialog = alertDialog.create()
        val layoutInflater = LayoutInflater.from(dialog.context)
        val dialogBinding = AddNewSchoolItemBinding.inflate(layoutInflater, dialog.listView, false)
        dialogBinding.createSchItemBtn.setOnClickListener {
            val title = dialogBinding.titleInput.text.toString()
            if (!title.minLength(3)) {
                Toast.makeText(
                    requireContext(),
                    "Name too show, should be at least 3 characters long",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            //proceed with creation
            val bundle = requireArguments()
            val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
            vm = ViewModelProvider(requireActivity()).get(SchoolDetailsViewModel::class.java)
            val sch = bundle.getString("schHash")
            val fid = bundle.getString("facultyHash")
            val did = bundle.getString("did")
            val adminId = sp.getString("adminId", "")
            val rb = CreateLevelRequestBody(adminId!!, sch!!, fid!!, did!!, title)
            vm.createLevel(rb){
                dialog.dismiss()
                if(it){
                    Toast.makeText(requireContext(),"an error occurreed try again",Toast.LENGTH_SHORT).show()
                    return@createLevel
                }
            }
        }
        dialog.setView(dialogBinding.root)
        dialog.show()
    }
}
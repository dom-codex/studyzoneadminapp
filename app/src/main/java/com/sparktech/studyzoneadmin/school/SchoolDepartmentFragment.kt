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
import com.sparktech.studyzoneadmin.databinding.SchoolDetailsBinding
import com.sparktech.studyzoneadmin.helpers.BundleExtractor
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.models.Department
import com.sparktech.studyzoneadmin.request_models.CreateDepartmentRequestBody
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength

class SchoolDepartmentFragment : Fragment() {
    private lateinit var binding: SchoolDetailsBinding
    private lateinit var vm: SchoolDetailsViewModel
    private lateinit var adapter: SchoolDepartmentAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.school_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        vm = ViewModelProvider(requireActivity()).get(SchoolDetailsViewModel::class.java)
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val schHash = bundle.getString("sch")
        val facultyHash = bundle.getString("fid")
        val adminId = sp.getString("adminId", "")
        val schName = bundle.getString("schName")
        val facultyName = bundle.getString("fn")
        if (vm.pages["currentDepartmentPage$facultyHash"] == null) {
            //init hashes on first start
            vm.allDepartments[facultyHash!!] = mutableListOf()
            vm.indicators["isLoadingDepartment$facultyHash"] = MutableLiveData(false)
            vm.indicators["creatingDepartment${facultyHash}"] = MutableLiveData(false)
            vm.pages["currentDepartmentPage${facultyHash}"] = 0
            //fetch department associated with the chosen faculty
            vm.fetchDepartment(
                schHash!!,
                facultyHash,
                adminId!!,
                vm.pages["currentDepartmentPage$facultyHash"]!!
            )
        }
        adapter =
            SchoolDepartmentAdapter(SchInfo(schName!!, schHash!!, facultyName!!, facultyHash!!))
        binding.schoolDetailsRcv.adapter = adapter
        binding.schoolDetailsRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.schoolDetailsRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchMoreDepartment
            )
        )
        initViews(bundle)
        setUpObservers()
    }

    //scroll lambdas
    private val getLoadingState: () -> Boolean = {
        vm.indicators["isLoadingDepartment${BundleExtractor.getFacultyHash(requireArguments())}"]!!.value!!
    }
    private val getListSize: () -> Int = {
        vm.allDepartments[BundleExtractor.getFacultyHash(requireArguments())]!!.size
    }
    private val setLoaderItem: () -> Unit = {
        val loaderItem = Department("", "", "", true)
        vm.allDepartments[BundleExtractor.getFacultyHash(requireArguments())]!!.add(loaderItem)
        adapter.notifyItemInserted(vm.allDepartments[BundleExtractor.getFacultyHash(requireArguments())]!!.size - 1)
    }
    private val fetchMoreDepartment = {
        val bundle = requireArguments()
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val schHash = bundle.getString("sch")
        val facultyHash = bundle.getString("fid")
        val adminId = sp.getString("adminId", "")
        vm.fetchDepartment(
            schHash!!,
            facultyHash!!,
            adminId!!,
            vm.pages["currentDepartmentPage${facultyHash}"]!!
        )
    }

    private fun initViews(bundle: Bundle) {
        val schName = bundle.getString("schName")
        val deparments = bundle.getString("departments")
        val facultyName = bundle.getString("fn")
        binding.schoolDetailsName.text = schName
        binding.schoolDetailsNumber.text = "$deparments departments in $facultyName"
        binding.addFacultyBtn.setOnClickListener {
            if (vm.indicators["creatingDepartment${BundleExtractor.getFacultyHash(requireArguments())}"]?.value!!) {
                return@setOnClickListener
            }
            showCreateDialog()
        }
    }

    private fun setUpObservers() {
        vm.indicators["creatingDepartment${BundleExtractor.getFacultyHash(requireArguments())}"]!!.observe(
            viewLifecycleOwner,
            { loading ->
                loading?.let {
                    if (!it) {
                        binding.schDetailsLoaderLayout.visibility = View.GONE
                        if (vm.getCode() == 200) {
                            //adapter.submitList(vm.departments)
                            adapter.notifyDataSetChanged()
                            binding.schoolDetailsNumber.text =
                                vm.allDepartments[BundleExtractor.getFacultyHash(requireArguments())]!!.size.toString()
                        }
                    } else {
                        //show spinner
                        binding.schDetailsLoaderLayout.visibility = View.VISIBLE
                    }
                }
            })
        vm.indicators["isLoadingDepartment${BundleExtractor.getFacultyHash(requireArguments())}"]!!.observe(
            viewLifecycleOwner,
            { loading ->
                loading?.let {
                    if (!it) {
                        binding.loadingFaculty.visibility = View.GONE
                        if (vm.allDepartments[BundleExtractor.getFacultyHash(requireArguments())]!!.size > 0) {
                            binding.noData.visibility = View.GONE
                            binding.schoolDetailsRcv.visibility = View.VISIBLE
                            initRcv()
                            return@let
                        }
                        binding.noData.visibility = View.VISIBLE
                    } else {
                        //show spinner
                        if (vm.allDepartments[BundleExtractor.getFacultyHash(requireArguments())]!!.size == 0) {
                            binding.noData.visibility = View.GONE
                            binding.loadingFaculty.visibility = View.VISIBLE
                        }
                    }
                }
            })
    }

    private fun initRcv() {
        adapter.submitList(vm.allDepartments[BundleExtractor.getFacultyHash(requireArguments())])
        adapter.notifyDataSetChanged()
    }

    private fun showCreateDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        val dialog = alertDialog.create()
        val layoutInflater = LayoutInflater.from(alertDialog.context)
        val dialogBinding = AddNewSchoolItemBinding.inflate(layoutInflater, dialog.listView, false)
        dialogBinding.addNewLabel.text = "Create Department"
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
            val sch = bundle.getString("sch")
            val fid = bundle.getString("fid")
            val adminId = sp.getString("adminId", "")
            val rb = CreateDepartmentRequestBody(title, sch!!, fid!!, adminId!!)
            vm.createDepartment(rb) { err, pos ->
                if (err) {
                    Toast.makeText(
                        requireContext(),
                        "an error occured,try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                requireActivity().runOnUiThread {
                    binding.schoolDetailsRcv.visibility = View.VISIBLE
                    binding.loadingFaculty.visibility = View.GONE
                    binding.noData.visibility = View.GONE
                    adapter.notifyItemInserted(pos)
                    dialog.dismiss()
                }

            }
        }
        dialog.setView(dialogBinding.root)
        dialog.show()
    }
}

data class SchInfo(val schName: String, val sid: String, val faculty: String, val fid: String)
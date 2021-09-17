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
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.helpers.getSchHash
import com.sparktech.studyzoneadmin.helpers.getSchName
import com.sparktech.studyzoneadmin.models.SchoolDetails
import com.sparktech.studyzoneadmin.request_models.CreateFacultyRequestBody
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength

class SchoolDetails : Fragment() {
    private lateinit var binding: SchoolDetailsBinding
    private lateinit var vm: SchoolDetailsViewModel
    private lateinit var adapter: SchoolDetailsAdapter
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
        vm = ViewModelProvider(requireActivity()).get(SchoolDetailsViewModel::class.java)
        val bundle = requireArguments()
        val schName = getSchName(bundle)
        val faculties = bundle.getString("facultites")
        val sch = bundle.getString("schHash")
        //init all hashes with relevant data
        if (vm.allFaculties[sch!!] == null) {
            vm.allFaculties[sch!!] = mutableListOf()
            vm.indicators["isLoadingFaculty${sch}"] = MutableLiveData(false)
            vm.indicators["isCreatingFaculty${sch}"] = MutableLiveData(false)
            vm.pages[sch] = 0
        }
        adapter = SchoolDetailsAdapter(sch!!, schName)
        binding.schoolDetailsRcv.adapter = adapter
        binding.schoolDetailsRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.schoolDetailsRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchMoreFaculties
            )
        )
        if (vm.pages[sch] == 0) {
            getFaculties(sch!!)
            binding.loadingFaculty.visibility = View.VISIBLE
        }
        setUpObserver()
        binding.schoolDetailsName.text = schName
        binding.schoolDetailsNumber.text = faculties
        binding.addFacultyBtn.setOnClickListener {
            if (vm.indicators["isCreatingFaculty${sch}"]!!.value!!) {
                Toast.makeText(requireContext(), "still creating a department", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            showCreateDialog()
        }
    }

    private fun getFaculties(sch: String) {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val adminId = sp.getString("adminId", null)
        vm.fetchFaculty(sch, adminId!!, vm.pages[sch]!!)
    }

    //loader lambadas
    private val getLoadingState: () -> Boolean = {
        vm.indicators["isLoadingFaculty${getSchHash(requireArguments())}"]!!.value!!
    }
    private val getListSize: () -> Int = {
        vm.allFaculties[getSchHash(requireArguments())]?.size ?: 0
    }
    private val setLoaderItem: () -> Unit = {
        val loaderItem = SchoolDetails("", "", "", "", true)
        val faculties = vm.allFaculties[getSchHash(requireArguments())] ?: mutableListOf()
        faculties.add(loaderItem)
        adapter.notifyItemInserted(faculties.size - 1)
    }
    private val fetchMoreFaculties = {
        val bundle = requireArguments()
        val sch = bundle.getString("schHash")
        getFaculties(sch!!)
    }

    private fun setUpObserver() {
        vm.indicators["isCreatingFaculty${getSchHash(requireArguments())}"]!!.observe(
            viewLifecycleOwner,
            { loading ->
                loading?.let {
                    if (!it) {
                        binding.schDetailsLoaderLayout.visibility = View.GONE
                        //adapter.submitList(vm.allFaculties[getSchHash(requireArguments())])
                       // adapter.notifyItemInserted(vm.allFaculties[getSchHash(requireArguments())]!!.size - 1)
                        adapter.notifyDataSetChanged()
                    } else {
                        //show spinner
                        binding.schDetailsLoaderLayout.visibility = View.VISIBLE
                    }
                }
            })
        vm.indicators["isLoadingFaculty${getSchHash(requireArguments())}"]!!.observe(
            viewLifecycleOwner,
            { loading ->
                loading?.let {
                    if (!it) {
                        binding.loadingFaculty.visibility = View.GONE

                        if (vm.allFaculties[getSchHash(requireArguments())]!!.size > 0) {
                            binding.noData.visibility = View.GONE
                            initRecyclerView()
                        } else {
                            //show not found screen
                            binding.noData.visibility = View.VISIBLE
                        }

                    } else {
                        //show spinner
                        if (vm.allFaculties[getSchHash(requireArguments())]!!.size == 0) {
                            binding.loadingFaculty.visibility = View.VISIBLE
                            binding.noData.visibility = View.GONE

                        }
                    }
                }
            })
    }

    private fun initRecyclerView() {
        binding.schoolDetailsRcv.visibility = View.VISIBLE
        adapter.submitList(vm.allFaculties[getSchHash(requireArguments())])
        adapter.notifyDataSetChanged()
    }

    private fun showCreateDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        val dialog = alertDialog.create()
        val layoutInflater = LayoutInflater.from(alertDialog.context)
        val dialogBinding = AddNewSchoolItemBinding.inflate(layoutInflater, dialog.listView, false)
        dialogBinding.addNewLabel.text = "Create Faculty"
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
            val adminId = sp.getString("adminId", "")
            val rb = CreateFacultyRequestBody(sch!!, adminId!!, title)
            vm.createFaculty(rb) {
                requireActivity().runOnUiThread {
                    binding.schoolDetailsRcv.visibility = View.VISIBLE
                    binding.loadingFaculty.visibility = View.GONE
                    binding.noData.visibility = View.GONE
                    adapter.notifyItemInserted(it)
                    dialog.dismiss()
                }
            }
        }
        dialog.setView(dialogBinding.root)
        dialog.show()
    }
}
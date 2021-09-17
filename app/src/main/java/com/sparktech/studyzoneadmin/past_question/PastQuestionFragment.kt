package com.sparktech.studyzoneadmin.past_question

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.NewPastQuestionLayoutBinding
import com.sparktech.studyzoneadmin.databinding.PricingLayoutBinding
import com.sparktech.studyzoneadmin.databinding.SchoolPastQuestionLayoutBinding
import com.sparktech.studyzoneadmin.helpers.*
import com.sparktech.studyzoneadmin.models.PastQuestion
import com.sparktech.studyzoneadmin.request_models.DefaultRequestBody
import com.sparktech.studyzoneadmin.request_models.PastQuestionPricingRequestBody
import com.sparktech.studyzoneadmin.request_models.PastQuestionUpload
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength

class PastQuestionFragment : Fragment() {
    private lateinit var binding: SchoolPastQuestionLayoutBinding
    private lateinit var vm: PastQuestionViewModel
    private lateinit var adapter: PastQuestionAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.school_past_question_layout,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(PastQuestionViewModel::class.java)
        adapter = PastQuestionAdapter(rcvDeleteBtnHandler)
        binding.pqRcv.adapter = adapter
        binding.pqRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.pqRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchMorePastQuestion
            )
        )
        val bundle = requireArguments()
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val schName = bundle.getString("schName", "")
        val schHash = bundle.getString("schHash", "")
        val facName = bundle.getString("facultyName", "")
        val facultyHash = bundle.getString("facultyHash", "")
        val deptName = bundle.getString("dept", "")
        val semester = bundle.getString("semester", "")
        val level = bundle.getString("level", "")
        val did = bundle.getString("did", "")
        val lid = bundle.getString("lid", "")
        val adminId = sp.getString("adminId", "")
        val queryOptions = HashMap<String, String>()
        queryOptions.put("sch", schHash)
        queryOptions.put("fid", facultyHash)
        queryOptions.put("did", did)
        queryOptions.put("lid", lid)
        queryOptions.put("adminId", adminId!!)
        queryOptions.put("semester", semester)
        val pageKey = KeyComposer.getLevelPastQuestionPageKey(queryOptions)
        val listKey = KeyComposer.getLevelPastQuestionListKey(queryOptions)
        val uploadingKey = KeyComposer.getLevelUploadingPastQuestionKey((queryOptions))
        val deletingKey = KeyComposer.getLevelDeletingPastQuestionKey(queryOptions)
        val loadingKey = KeyComposer.getLevelPastQuestionLoadingKey(queryOptions)
        if (vm.pages[pageKey] == null) {
            vm.inputs["title"] = ""
            vm.inputs["start"] = ""
            vm.inputs["end"] = ""
            //init hashes
            vm.indicators[uploadingKey] = MutableLiveData(false)
            vm.indicators[loadingKey] = MutableLiveData(false)
            vm.indicators[deletingKey] = MutableLiveData(false)
            vm.allPastQuestions[listKey] = mutableListOf()
            vm.pages[pageKey] = 0
            vm.indicators["settingPrice${lid}${semester}"]  = MutableLiveData(false)
            vm.pricing["${lid}${semester}"] = MutableLiveData(0)
            vm.getPastQuestion(queryOptions, vm.pages[pageKey]!!)
        }
        //create map for the keys and pass to setUpObservers
        val observerOptions = HashMap<String, String>()
        observerOptions["loading"] = loadingKey
        observerOptions["uploading"] = uploadingKey
        observerOptions["deleting"] = deletingKey
        observerOptions["list"] = listKey
        observerOptions["page"] = pageKey
        initViews(schName, deptName, semester, level)
        setUpObservers(observerOptions)
    }

    //lambdas
    //scroll lambdas
    private val getLoadingState: () -> Boolean = {
        val option = BundleExtractor.getLevelAndSemester(requireArguments())
        val loadingKey = KeyComposer.getLevelPastQuestionLoadingKey(option)
        vm.indicators[loadingKey]!!.value!!
    }
    private val getListSize: () -> Int = {
        val option = BundleExtractor.getLevelAndSemester(requireArguments())
        val listKey = KeyComposer.getLevelPastQuestionListKey(option)
        vm.allPastQuestions[listKey]!!.size
    }
    private val setLoaderItem: () -> Unit = {
        val option = BundleExtractor.getLevelAndSemester(requireArguments())
        val listKey = KeyComposer.getLevelPastQuestionListKey(option)
        val loaderItem = PastQuestion("", "", "", "", "", true)
        vm.allPastQuestions[listKey]!!.add(loaderItem)
        adapter.notifyItemInserted(vm.allPastQuestions[listKey]!!.size - 1)
    }
    private val fetchMorePastQuestion = {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val bundle = requireArguments()
        val schHash = bundle.getString("schHash", "")
        val facultyHash = bundle.getString("facultyHash", "")
        val semester = bundle.getString("semester", "")
        val did = bundle.getString("did", "")
        val lid = bundle.getString("lid", "")
        val adminId = sp.getString("adminId", "")
        val queryOptions = HashMap<String, String>()
        queryOptions.put("sch", schHash)
        queryOptions.put("fid", facultyHash)
        queryOptions.put("did", did)
        queryOptions.put("lid", lid)
        queryOptions.put("adminId", adminId!!)
        queryOptions.put("semester", semester)
        val pageKey = KeyComposer.getLevelPastQuestionPageKey(queryOptions)
        vm.getPastQuestion(queryOptions, vm.pages[pageKey]!!)
    }
    private val rcvDeleteBtnHandler: (hash: String) -> Unit = {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val defaultRequestBody = DefaultRequestBody(it, admin!!)
        val option = BundleExtractor.getLevelAndSemester(requireArguments())
        vm.deletePastQuestion(defaultRequestBody, option) {
            adapter.notifyItemRemoved(it)
        }
    }
    private val openActivityForResult =
        registerForActivityResult(PastQuestionUploadContract()) { result ->
            result?.let {
                //get file info
                val fileInfo = MediaUtils.getPastQuestionInfo(requireContext(), it)
                initDialog(true, fileInfo, it)
                //init views on file selected
            }
        }

    private fun initViewOnfileSelected(
        fileInfo: HashMap<String, String>?,
        uri: Uri,
        dialogBinding: NewPastQuestionLayoutBinding, cb: () -> Unit
    ) {
        dialogBinding.pqNameToUpload.text = fileInfo?.get("name")
        dialogBinding.cancelToUploadBtn.setOnClickListener {
            //hiide view
            dialogBinding.pqNameToUpload.text = null
            hideDialogViewsOnFileCleared(dialogBinding)
        }
        dialogBinding.startUploadBtn.setOnClickListener {
            val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
            val user = sp.getString("adminId", "")
            val options = BundleExtractor.extractRequestDataFromBundle(requireArguments())
            val startYear = vm.inputs["start"]//dialogBinding.startYear.text.toString()
            val endYear = vm.inputs["end"]//dialogBinding.endYear.text.toString()
            val title = vm.inputs["title"]
            val body = PastQuestionUpload(
                fileInfo?.get("name")!!,
                startYear!!,
                endYear!!,
                options["sch"]!!,
                options["fid"]!!,
                options["did"]!!,
                options["lid"]!!,
                options["semester"]!!,
                user!!
            )
            cb()
            vm.beginPastQuestionUpload(body, uri) { pos, err ->
                if (err) {
                    Toast.makeText(requireContext(), "Upload failed", Toast.LENGTH_SHORT).show()
                    return@beginPastQuestionUpload
                }
                adapter.notifyItemInserted(pos)
            }
        }
    }

    private fun hideDialogViewsOnFileCleared(dialogBinding: NewPastQuestionLayoutBinding) {
        dialogBinding.pqNameToUpload.visibility = View.GONE
        dialogBinding.startUploadBtn.visibility = View.GONE
        dialogBinding.cancelToUploadBtn.visibility = View.GONE
        dialogBinding.selectPqFromStorage.visibility = View.VISIBLE
        dialogBinding.courseTitle.visibility = View.VISIBLE
        dialogBinding.startYear.visibility = View.VISIBLE
        dialogBinding.endYear.visibility = View.VISIBLE
    }

    private fun showDialogViewsOnFileSelected(dialogBinding: NewPastQuestionLayoutBinding) {
        dialogBinding.pqNameToUpload.visibility = View.VISIBLE
        dialogBinding.startUploadBtn.visibility = View.VISIBLE
        dialogBinding.cancelToUploadBtn.visibility = View.VISIBLE
        dialogBinding.courseTitle.visibility = View.GONE
        dialogBinding.startYear.visibility = View.GONE
        dialogBinding.endYear.visibility = View.GONE
        dialogBinding.selectPqFromStorage.visibility = View.GONE

    }

    private fun initViews(schName: String, deptName: String, sem: String, level: String) {
        binding.nameOfSch.text = schName
        binding.nameOfDepartment.text = deptName
        binding.nameOfLevel.text = level
        binding.nameOfSemester.text = sem
        binding.addPqBtn.setOnClickListener {
            val options = BundleExtractor.getLevelAndSemester(requireArguments())
            val uploadingKey = KeyComposer.getLevelUploadingPastQuestionKey(options)
            if (vm.indicators[uploadingKey]!!.value!!) {
                Toast.makeText(
                    requireContext(),
                    "please wait, upload still on-going",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            initDialog(false)
        }
        binding.pricingBtn.setOnClickListener {
            val sp = requireContext().getSharedPreferences("AdminDetails",Context.MODE_PRIVATE)
            val admin = sp.getString("adminId","")
            val alertDialog = AlertDialog.Builder(requireContext())
            val dialog = alertDialog.create()
            val inflater = LayoutInflater.from(alertDialog.context)
            val dialogBinding = PricingLayoutBinding.inflate(inflater, dialog.listView, false)
            dialogBinding.setPricingBtn.setOnClickListener {
                val price = dialogBinding.pricingInput.text.toString()
                if (!price.isDigitsOnly()) {
                    showToast(requireContext(), "enter a number value")
                    return@setOnClickListener
                }
                val options = BundleExtractor.extractRequestDataFromBundle(requireArguments())
                val sid = options["sch"]!!
                val fid = options["fid"]!!
                val did = options["did"]!!
                val lid = options["lid"]!!
                val sem = options["semester"]!!
                val body = PastQuestionPricingRequestBody(admin!!,sid, fid, did, lid, sem,price.toLong())
                vm.setPriceOfPastQuestion(body)
                dialog.dismiss()
            }
            dialog.setView(dialogBinding.root)
            dialog.show()
        }
    }

    private fun initDialog(
        fileSelected: Boolean,
        fileInfo: HashMap<String, String>? = null,
        uri: Uri? = null
    ) {

        //init custom dialog
        val alertDialog = AlertDialog.Builder(requireContext())
        val dialog = alertDialog.create()
        //create binding for new layout
        val inflater = LayoutInflater.from(alertDialog.context)
        val dialogBinding = NewPastQuestionLayoutBinding.inflate(inflater, dialog.listView, false)
        if (fileSelected) {
            initViewOnfileSelected(fileInfo, uri!!, dialogBinding) {
                dialog.dismiss()
            }
            showDialogViewsOnFileSelected(dialogBinding)
            dialog.setView(dialogBinding.root)
            dialog.show()
            return
        }
        //set up listeners
        dialogBinding.courseTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
                vm.inputs["title"] = input.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        dialogBinding.startYear.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
                vm.inputs["start"] = input.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        dialogBinding.endYear.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
                vm.inputs["end"] = input.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        dialogBinding.selectPqFromStorage.setOnClickListener {
            if (vm.inputs["title"]!!.minLength(1) &&
                vm.inputs["start"]!!.minLength(1) &&
                vm.inputs["end"]!!.minLength(1)
            ) {
                openActivityForResult.launch(Unit)
                dialog.dismiss()
                return@setOnClickListener
            }
            Toast.makeText(requireContext(), "enter a valid title or year", Toast.LENGTH_SHORT)
                .show()
            dialog.dismiss()

        }
        dialog.setView(dialogBinding.root)
        dialog.show()
    }

    private fun setUpObservers(options: HashMap<String, String>) {
        val bundle = requireArguments()
        val semester = bundle.getString("semester", "")
        val level = bundle.getString("lid","")
        vm.pricing["${level}${semester}"]!!.observe(viewLifecycleOwner,{loading->
            loading?.let{
               binding.pqPricing.text = it.toString()
            }
        })
        vm.indicators["settingPrice${level}${semester}"]!!.observe(viewLifecycleOwner,{loading->
            loading?.let{
                if(!it){
                    binding.pqLoadingLayout.visibility = View.GONE
                }else{
                    binding.pqLoadingLayout.visibility = View.VISIBLE
                }
            }
        })
        vm.indicators[options["uploading"]]!!.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.pqLoadingLayout.visibility = View.GONE
                } else {
                    binding.pqLoadingLayout.visibility = View.VISIBLE
                }
            }
        })
        vm.indicators[options["loading"]]!!.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.pqLoader.visibility = View.GONE
                    if (vm.allPastQuestions[options["list"]]!!.size > 0) {
                        binding.noPqData.visibility = View.GONE
                        binding.pqRcv.visibility = View.VISIBLE
                        adapter.submitList(vm.allPastQuestions[options["list"]]!!)
                        adapter.notifyDataSetChanged()
                        return@let
                    }
                    binding.noPqData.visibility = View.VISIBLE
                } else {
                    //show spinner
                    if (vm.allPastQuestions[options["list"]]!!.size == 0) {
                        binding.pqLoader.visibility = View.VISIBLE
                        binding.pqRcv.visibility = View.GONE
                        binding.noPqData.visibility = View.GONE
                    }
                }
            }
        })
    }
}
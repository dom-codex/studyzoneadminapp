package com.sparktech.studyzoneadmin.school

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.SchoolBottomFragLayoutBinding
import com.sparktech.studyzoneadmin.main_menu.MainMenuViewModel
import com.sparktech.studyzoneadmin.models.School
import com.sparktech.studyzoneadmin.network.Network
import com.sparktech.studyzoneadmin.request_models.NewSchool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BottomFragment:BottomSheetDialogFragment() {
    private lateinit var binding: SchoolBottomFragLayoutBinding
    private lateinit var vm:MainMenuViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.school_bottom_frag_layout,container,false)
        setup()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(MainMenuViewModel::class.java)
        val bundle = requireArguments()
        val type = bundle.getString("type")
        when(type){
            "Universities"->binding.createLabel.text = "Create University"
            "Polytechnics"-> binding.createLabel.text = "Create Polytechnic"
        }
        setupViews()
    }
    private fun setup(){
        val be = BottomSheetBehavior.from(binding.mainCordChild)
        be.isFitToContents = true
        be.isDraggable = false
        val dm = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(dm)
        be.peekHeight = (dm.heightPixels)
    }
    private fun setupViews(){
        var nameValid = false
        var abbrValid = false
        binding.closeBtn.setOnClickListener {
            dismissAllowingStateLoss()
        }
        binding.createBtn.setOnClickListener {
            val bundle = requireArguments()
            val type = bundle.getString("type")
            var toCreate:String
            if(type=="Universities"){
               toCreate =  "university"
            }else{
                toCreate = "polytechnic"
            }
            //retrieve admin email and hash
            val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
            val email = sp.getString("email",null)
            val hash = sp.getString("adminId",null)
            //retrieve input,validate, and make network calls
            val name = binding.schNameInput.text.toString()
            val abbr = binding.schAbInput.text.toString()
            if(email!=null && hash!=null){
                createSchool(name,abbr,email,hash,toCreate)
            }else{
                //kick user out
            }

        }
        binding.schAbInput.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val valid = text.toString().isNotEmpty()
                abbrValid = valid
                binding.createBtn.isEnabled = nameValid && abbrValid
            }

            override fun afterTextChanged(text: Editable?) {}
        })
        binding.schNameInput.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //validate length
                val valid = text.toString().isNotEmpty()
                nameValid = valid
                binding.createBtn.isEnabled = nameValid && abbrValid
            }

            override fun afterTextChanged(text: Editable?) {}
        })
    }
    private fun createSchool(name:String,abbr:String,email:String,admin:String,type:String){
        Toast.makeText(requireContext(),type,Toast.LENGTH_SHORT).show()
        val newSchool = NewSchool(email,name,abbr,admin,type)
        CoroutineScope(Dispatchers.IO).launch{
            try{
            val res = Network.apiService.createSchool(newSchool)
                Log.i("RESPONSE",res.toString())
                withContext(Dispatchers.Main){
                    val data = res.data
                    val school = School(data.name,data.nameAbbr,data.icon,data.type,data.sid,data.createdAt,0.toString())
                    vm.notifyNewSchool(school)
                    dismissAllowingStateLoss()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    companion object{
            fun newInstance(schType:String?): BottomFragment {
                val bundle = Bundle()
                bundle.putString("type",schType)
                return BottomFragment().apply {
                    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetTheme)
                    this.arguments = bundle
                }
        }
}
}
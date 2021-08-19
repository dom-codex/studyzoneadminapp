package com.sparktech.studyzoneadmin.main_menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sparktech.studyzoneadmin.models.School
import com.sparktech.studyzoneadmin.models.SchoolAdapterData
import com.sparktech.studyzoneadmin.models.SliderData
import com.sparktech.studyzoneadmin.network.Network
import kotlinx.coroutines.*

class MainMenuViewModel : ViewModel() {
    private val job = Job()
    private val newtworkScope = CoroutineScope(Dispatchers.IO + job)
    //loading indicators
    private val _isLoading = MutableLiveData<Boolean>(false)
    private val _isFetchingSchool = MutableLiveData(false)
     val isLoading:LiveData<Boolean>
            get()= _isLoading
    val isFetchingSchool:LiveData<Boolean>
    get() = _isFetchingSchool
    //lists with response data
    val sliderData = MutableLiveData<SliderData>()
    val schools = MutableLiveData<MutableList<School>>()
    val adapterData = mutableListOf<SchoolAdapterData>()
    init {
        fetchSliderDetails()
    }
    fun fetchSliderDetails(){
        _isLoading.value = true
        newtworkScope.launch {
            try{
            val data = Network.apiService.getSliderData()
                withContext(Dispatchers.Main){
                sliderData.value  = data
                _isLoading.value = false
                }
                Log.i("RESPONSE",data.toString())
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    fun fetchSchools(page:Int = 0,type:String="university"){
        _isFetchingSchool.value = true
        try{
            newtworkScope.launch {
                val schoolRes = Network.apiService.fetchSchools(page,type)
                if(schoolRes.schools.isNotEmpty()){
                    schoolRes.schools.forEach {
                        adapterData.add(SchoolAdapterData(it))
                    }
                }
                Log.i("RES",schoolRes.toString())
                withContext(Dispatchers.Main){
                    schools.value = schoolRes.schools.toMutableList()
                    _isFetchingSchool.value = false
                }
            }
        }catch(e:Exception){
            _isFetchingSchool.value = false
            e.printStackTrace()
        }
    }
    override fun onCleared() {
        super.onCleared()
        job.complete()
    }
    //notification functions
    //for new school
    var newSchool:School? = null
    private val _isNewSchoolCreated = MutableLiveData(false)
    val isNewSchoolAdded :LiveData<Boolean>
    get() = _isNewSchoolCreated
    fun notifyNewSchool(school: School){
        newSchool = school
        _isNewSchoolCreated.value = true
    }
    fun notifyNewSchoolAdded(){
        _isNewSchoolCreated.value = false
        newSchool = null
    }

}
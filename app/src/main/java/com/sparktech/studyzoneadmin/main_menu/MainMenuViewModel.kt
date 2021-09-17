package com.sparktech.studyzoneadmin.main_menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sparktech.studyzoneadmin.helpers.Constants
import com.sparktech.studyzoneadmin.models.School
import com.sparktech.studyzoneadmin.models.SchoolAdapterData
import com.sparktech.studyzoneadmin.models.SliderData
import com.sparktech.studyzoneadmin.network.Network
import kotlinx.coroutines.*

class MainMenuViewModel : ViewModel() {
    private val job = Job()
    private val newtworkScope = CoroutineScope(Dispatchers.IO + job)


    //loading indicators
    private val _creatingUni = MutableLiveData(false)
    val creatingUni: LiveData<Boolean>
        get() = _creatingUni
    private val _creatingPoly = MutableLiveData(false)
    val creatingPoly: LiveData<Boolean>
        get() = _creatingPoly
    private val _loadingPoly = MutableLiveData(false)
    val loadingPoly: LiveData<Boolean>
        get() = _loadingPoly
    private val _isLoading = MutableLiveData<Boolean>(false)
    private val _isFetchingSchool = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    val isFetchingSchool: LiveData<Boolean>
        get() = _isFetchingSchool

    fun setCreatingSchool(creating: Boolean, type: String) {
        if (type == "university") {
            _creatingUni.value = creating
        } else {
            _creatingPoly.value = creating
        }
    }

    //lists with response data
    val sliderData = MutableLiveData<SliderData>()
    val schools = MutableLiveData<MutableList<School>>()
    val adapterData = mutableListOf<SchoolAdapterData>()
    val poly = mutableListOf<SchoolAdapterData>()
    val uni = mutableListOf<SchoolAdapterData>()

    init {
        fetchSliderDetails()
    }

    fun fetchSliderDetails() {
        _isLoading.value = true
        newtworkScope.launch {
            try {
                val data = Network.apiService.getSliderData()
                withContext(Dispatchers.Main) {
                    sliderData.value = data
                    _isLoading.value = false
                }
                Log.i("RESPONSE", data.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _isLoading.value = true
                }
            }
        }
    }

    var currentUniversityPage = 0
    var currentPolyPage = 0
    fun fetchSchools(page: Int, type: String = "university") {
        if (type == "university") {
            _isFetchingSchool.value = true
        } else {
            _loadingPoly.value = true
        }
        try {
            newtworkScope.launch {
                val schoolRes = Network.apiService.fetchSchools(page, type)
                if (type == "university") {
                    val index = uni.indexOfFirst { it.isLoading }
                    if (index >= 0) uni.removeAt(index)
                } else {
                    val index = uni.indexOfFirst { it.isLoading }
                    if (index >= 0) uni.removeAt(index)
                }
                if (schoolRes.schools.isNotEmpty()) {
                    val itemsToAdd = listFilter(type, schoolRes.schools)
                    if (type == "university") {
                        uni.addAll(itemsToAdd)
                        if ((currentUniversityPage + 1) * Constants.SERVER_LIMIT <= uni.size) {
                            currentUniversityPage += 1
                        }
                    } else {
                        poly.addAll(itemsToAdd)
                        if ((currentPolyPage + 1) * Constants.SERVER_LIMIT <= uni.size) {
                            currentPolyPage += 1
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    schools.value = schoolRes.schools.toMutableList()
                    if (type == "university") {
                        _isFetchingSchool.value = false
                    } else {
                        _loadingPoly.value = false
                    }
                }
            }
        } catch (e: Exception) {
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
    var newSchool: School? = null
    private val _isNewSchoolCreated = MutableLiveData(false)
    val isNewSchoolAdded: LiveData<Boolean>
        get() = _isNewSchoolCreated

    fun notifyNewSchool(school: School) {
        newSchool = school
        _isNewSchoolCreated.value = true
    }

    fun notifyNewSchoolAdded() {
        _isNewSchoolCreated.value = false
        newSchool = null
    }

    private suspend fun listFilter(type: String, list: List<School>): List<SchoolAdapterData> {
        return withContext(Dispatchers.IO) {
            val itemsToAdd = mutableListOf<SchoolAdapterData>()
            if (type == "university") {
                list.forEach {
                    val data = SchoolAdapterData(it)
                    val alreadyAdded = uni.contains(data)
                    if (!alreadyAdded) {
                        itemsToAdd.add(data)
                    }
                }
            } else {
                list.forEach {
                    val data = SchoolAdapterData(it)
                    val alreadyAdded = poly.contains(data)
                    if (!alreadyAdded) {
                        itemsToAdd.add(data)
                    }
                }
            }
            itemsToAdd
        }
    }
}
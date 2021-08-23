package com.sparktech.studyzoneadmin.school

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sparktech.studyzoneadmin.models.Department
import com.sparktech.studyzoneadmin.models.Level
import com.sparktech.studyzoneadmin.models.SchoolDetails
import com.sparktech.studyzoneadmin.network.Network
import kotlinx.coroutines.*
import java.lang.Exception

class SchoolDetailsViewModel: ViewModel() {
    val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO+ job)
    private val _isLoadingFaculties = MutableLiveData(false)
    val isLoadingFaculty :LiveData<Boolean>
    get() = _isLoadingFaculties
    private val _isLoadingDepartments = MutableLiveData(false)
    val isLoadingDepartments :LiveData<Boolean>
    get() = _isLoadingDepartments
    private val _isLoadingLevels = MutableLiveData(false)
    val isLoadingLeve :LiveData<Boolean>
    get() = _isLoadingLevels
    //data
    val faculties= mutableListOf<SchoolDetails>()
    val departments = mutableListOf<Department>()
    val levels = mutableListOf<Level>()
    val adapterData:HashMap<String,List<String>>
    get() {
        val semesters = listOf("first","second")
        val data = HashMap<String,List<String>>()
        levels.forEach {
            data.put(it.level,semesters)
        }
        return data
    }
    fun fetchFaculty(sch:String,adminId:String,page:Int){
        try{
            _isLoadingFaculties.value = true
            networkScope.launch {
                val res = Network.apiService.getFaculties(sch,adminId, page)
                withContext(Dispatchers.Main){
                    faculties.addAll(res.faculties)
                    _isLoadingFaculties.value = false
                }
            }
        }catch (e:Exception){
            _isLoadingFaculties.value = false
            e.printStackTrace()
        }
    }
    fun fetchDepartment(sch:String,fac:String,adminId:String,page:Int){
        try{
            _isLoadingDepartments.value = true
            networkScope.launch {
                val res = Network.apiService.getDepartment(sch,adminId,fac,0)
                withContext(Dispatchers.Main){
                    departments.addAll(res.departments)
                    _isLoadingDepartments.value = false
                }
            }
        }catch (e:Exception){
            _isLoadingFaculties.value = false
            e.printStackTrace()
        }
    }
    fun fetchLevel(sch:String,fac:String,did:String,adminId:String){
        try{
            _isLoadingLevels.value = true
            val queryOptions = HashMap<String,String>()
            queryOptions.put("sch",sch)
            queryOptions.put("fid",fac)
            queryOptions.put("did",did)
            queryOptions.put("adminId",adminId)
            networkScope.launch {
            val res = Network.apiService.getDepartmentLevels(queryOptions)
                withContext(Dispatchers.Main){levels.addAll(res.levels)
                _isLoadingLevels.value = false}
            }
        }catch (e:Exception){
            e.printStackTrace()
            _isLoadingLevels.value = false
        }
    }
    override fun onCleared() {
        super.onCleared()
        job.complete()
    }
}
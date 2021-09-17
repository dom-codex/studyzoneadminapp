package com.sparktech.studyzoneadmin.school

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sparktech.studyzoneadmin.models.Department
import com.sparktech.studyzoneadmin.models.Level
import com.sparktech.studyzoneadmin.models.SchoolDetails
import com.sparktech.studyzoneadmin.network.Network
import com.sparktech.studyzoneadmin.request_models.CreateDepartmentRequestBody
import com.sparktech.studyzoneadmin.request_models.CreateFacultyRequestBody
import com.sparktech.studyzoneadmin.request_models.CreateLevelRequestBody
import kotlinx.coroutines.*

class SchoolDetailsViewModel : ViewModel() {
    val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)
    //private val _isLoadingFaculties = MutableLiveData(false)

    //val isLoadingFaculty: LiveData<Boolean>
    //  get() = _isLoadingFaculties
    private val _isLoadingDepartments = MutableLiveData(false)

    //val isLoadingDepartments: LiveData<Boolean>
    //   get() = _isLoadingDepartments
    // private val _isLoadingLevels = MutableLiveData(false)
    //  val isLoadingLeve: LiveData<Boolean>
    //    get() = _isLoadingLevels
    private var _code = -1

    //loaders
    //private val _isCreatingLevel = MutableLiveData(false)

    //private val _isCreatingDepartment = MutableLiveData(false)
    private val _isCreatingFaculty = MutableLiveData(false)
    //  val isCreatingLevel: LiveData<Boolean>
    //    get() = _isCreatingLevel
    //val isCreatingDepartment: LiveData<Boolean>
    //   get() = _isCreatingDepartment
    //val isCreatingFaculty: LiveData<Boolean>
    //  get() = _isCreatingFaculty

    //data
    //hash to hold school and associated faculties
    val allFaculties = HashMap<String, MutableList<SchoolDetails>>()
    val allDepartments = HashMap<String, MutableList<Department>>()
    val allLevels = HashMap<String, MutableList<Level>>()

    //hash to hold faculty loader
    val indicators = HashMap<String, MutableLiveData<Boolean>>()
    val pages = HashMap<String, Int>()

    // val faculties = mutableListOf<SchoolDetails>()
    //val departments = mutableListOf<Department>()
    //val levels = mutableListOf<Level>()
    /* private val adapterData: HashMap<String, List<String>>
         get() {
             val semesters = listOf("first", "second")
             val data = HashMap<String, List<String>>()
             allLevels[""].forEach {
                 data.put(it.level, semesters)
             }
             return data
         }*/
    fun getAdapterData(key: String): HashMap<String, List<String>> {
        val semesters = listOf("first", "second")
        val data = HashMap<String, List<String>>()
        allLevels[key]!!.forEach {
            data.put(it.level, semesters)
        }
        return data
    }

    //faculty page control variable
    //var currentFacultyPage = 0
    fun fetchFaculty(sch: String, adminId: String, page: Int) {
        val serverLimit = 1
        try {
            indicators["isLoadingFaculty${sch}"]!!.value = true
            networkScope.launch {
                val res = Network.apiService.getFaculties(sch, adminId, page)
                withContext(Dispatchers.Main) {
                    //remove loading item
                    if (pages[sch]!! > 0) {
                        allFaculties[sch]?.removeAt(allFaculties[sch]!!.size - 1)
                    }
                    if (res.faculties.isNotEmpty()) {
                        facultyListFilter(sch, res.faculties)
                        val schoolFaculties = allFaculties[sch] ?: mutableListOf()
                        schoolFaculties.addAll(serverLimit * pages[sch]!!, res.faculties)
                        // schoolFaculties.addAll(res.faculties)
                        allFaculties[sch] = schoolFaculties
                        pages[sch] = pages[sch]!! + 1
                    }
                    indicators["isLoadingFaculty${sch}"]!!.value = false
                }
            }
        } catch (e: Exception) {
            //_isLoadingFaculties.value = false
            indicators["isLoadingFaculty${sch}"]!!.value = false
            e.printStackTrace()
        }
    }

    //deparmtent page controller
    // var currentDepartmentPage = 0
    //use the faculty as key for departments hash
    fun fetchDepartment(sch: String, fac: String, adminId: String, page: Int) {
        val severLimit = 1
        try {
            indicators["isLoadingDepartment$fac"]?.value = true
            // _isLoadingDepartments.value = true
            networkScope.launch {
                val res = Network.apiService.getDepartment(sch, adminId, fac, page)
                withContext(Dispatchers.Main) {
                    if (pages["currentDepartmentPage${fac}"]!! > 0) {
                        allDepartments[fac]!!.removeAt(allDepartments[fac]!!.size - 1)
                    }

                    if (res.departments.isNotEmpty()) {
                        departmentListFilter(fac, res.departments)
                        allDepartments[fac]!!.addAll(
                            severLimit * pages["currentDepartmentPage$fac"]!!,
                            res.departments
                        )
                        //   allDepartments[fac]!!.addAll(res.departments)
                        pages["currentDepartmentPage$fac"] =
                            pages["currentDepartmentPage$fac"]!! + 1
                    }
                    // _isLoadingDepartments.value = false
                    indicators["isLoadingDepartment$fac"]?.value = false
                }
            }
        } catch (e: Exception) {
            // _isLoadingFaculties.value = false
            indicators["isLoadingDepartment$fac"]?.value = false
            e.printStackTrace()
        }
    }

    var fetchedLevel = false
    fun fetchLevel(sch: String, fac: String, did: String, adminId: String) {
        try {
            indicators["loadingLevel${did}"]!!.value = true
            val queryOptions = HashMap<String, String>()
            queryOptions.put("sch", sch)
            queryOptions.put("fid", fac)
            queryOptions.put("did", did)
            queryOptions.put("adminId", adminId)
            networkScope.launch {
                val res = Network.apiService.getDepartmentLevels(queryOptions)
                withContext(Dispatchers.Main) {
                    //levels.addAll(res.levels)
                    allLevels[did]!!.addAll(res.levels)
                    fetchedLevel = true
                    indicators["loadingLevel${did}"]!!.value = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            indicators["loadingLevel${did}"]!!.value = false
        }
    }

    fun createLevel(rb: CreateLevelRequestBody,cb:(err:Boolean)->Unit) {
        indicators["creatingLevel${rb.did}"]?.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.createLevel((rb))
                val newLevel = res.data
                withContext(Dispatchers.Main) {
                    allLevels[rb.did]!!.add(newLevel)
                    _code = res.code
                    indicators["creatingLevel${rb.did}"]?.value = false
                    cb(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators["creatingLevel${rb.did}"]?.value = false
                    cb(true)
                }
            }
        }
    }

    fun createDepartment(rb: CreateDepartmentRequestBody, cb: (err: Boolean, index: Int) -> Unit) {
        // _isCreatingDepartment.value = true
        indicators["creatingDepartment${rb.fid}"]?.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.createDepartment(rb)
                val newDepartment = res.data
                allDepartments[rb.fid]!!.add(newDepartment)
                cb(
                    false,
                    allDepartments[rb.fid]!!.size
                )
                withContext(Dispatchers.Main) {
                    indicators["creatingDepartment${rb.fid}"]?.value = false

                }
            } catch (e: retrofit2.HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators["creatingDepartment${rb.fid}"]?.value = false
                    cb(true, -1)

                }
            }
        }
    }

    fun createFaculty(rb: CreateFacultyRequestBody, cb: (index: Int) -> Unit) {
        indicators["creatingFaculty${rb.sid}"]?.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.createFaculty(rb)
                val newFaculty = res.data
                println(newFaculty)
                val faculty = allFaculties[rb.sid]
                faculty?.add(newFaculty)
                cb(faculty!!.size)
                _code = res.code
                indicators["creatingFaculty${rb.sid}"]?.value = false
            } catch (e: retrofit2.HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators["creatingFaculty${rb.sid}"]?.value = false
                }
            }
        }
    }

    fun resetCode() {
        _code = -1
    }

    fun getCode(): Int {
        return _code
    }

    private suspend fun facultyListFilter(sch: String, list: List<SchoolDetails>) {
        withContext(Dispatchers.IO) {
            list.forEach {
                val index = allFaculties[sch]!!.indexOfFirst { data ->
                    data.hash == it.hash
                }
                println(index)
                if (index >= 0) {
                    allFaculties[sch]!!.removeAt(index)
                }
            }

        }
    }

    private suspend fun departmentListFilter(key: String, list: List<Department>) {
        withContext(Dispatchers.IO) {
            list.forEach {
                val index = allDepartments[key]!!.indexOfFirst { data ->
                    data.hash == it.hash
                }
                if (index >= 0) {
                    allDepartments[key]!!.removeAt(index)
                }
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        job.complete()
    }
}
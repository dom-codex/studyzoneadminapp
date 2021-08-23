package com.sparktech.studyzoneadmin.past_question

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sparktech.studyzoneadmin.models.PastQuestion
import com.sparktech.studyzoneadmin.network.Network
import kotlinx.coroutines.*
import java.lang.Exception

class PastQuestionViewModel: ViewModel() {
    private val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)
    val pastquestion = mutableListOf<PastQuestion>()
    private val _isLoading = MutableLiveData(false)
    val isloading:LiveData<Boolean>
    get() = _isLoading
    fun getPastQuestion(option:HashMap<String,String>,page:Int){
        try{
            _isLoading.value = true
            networkScope.launch {
                val res = Network.apiService.getPastQuestions(option,page)
                withContext(Dispatchers.Main){pastquestion.addAll(res.pastquestions)
                _isLoading.value = false}
            }
        }catch (e:Exception){
            _isLoading.value = false
            e.printStackTrace()
        }
    }
    override fun onCleared() {
        super.onCleared()
        job.complete()
    }

}
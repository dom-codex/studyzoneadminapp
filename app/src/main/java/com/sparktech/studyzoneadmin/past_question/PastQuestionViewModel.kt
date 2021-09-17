package com.sparktech.studyzoneadmin.past_question

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sparktech.studyzoneadmin.helpers.FileUtils
import com.sparktech.studyzoneadmin.helpers.KeyComposer
import com.sparktech.studyzoneadmin.models.PastQuestion
import com.sparktech.studyzoneadmin.network.Network
import com.sparktech.studyzoneadmin.request_models.DefaultRequestBody
import com.sparktech.studyzoneadmin.request_models.PastQuestionPricingRequestBody
import com.sparktech.studyzoneadmin.request_models.PastQuestionUpload
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class PastQuestionViewModel(val app: Application) : AndroidViewModel(app) {
    private val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)

    val inputs = HashMap<String,String>()
    val allPastQuestions = HashMap<String, MutableList<PastQuestion>>()
    val indicators = HashMap<String, MutableLiveData<Boolean>>()
    val pages = HashMap<String, Int>()
    val pricing = HashMap<String,MutableLiveData<Int>>()
    fun getPastQuestion(option: HashMap<String, String>, page: Int) {
        val serverLimit = 1
        val listKey = KeyComposer.getLevelPastQuestionListKey(option)
        val loadingIndicatorKey = KeyComposer.getLevelPastQuestionLoadingKey(option)
        val pageKey = KeyComposer.getLevelPastQuestionPageKey(option)
        indicators[loadingIndicatorKey]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.getPastQuestions(option, page)
                withContext(Dispatchers.Main) {
                    if (pages[pageKey]!! > 0) {
                        allPastQuestions[listKey]!!.removeAt(allPastQuestions[listKey]!!.size - 1)
                    }
                    if (res.pastquestions.isNotEmpty()) {
                        val offset = pages[pageKey]!! * serverLimit
                        //remove any duplicate from incoming list
                        filterList(res.pastquestions, option)
                        allPastQuestions[listKey]!!.addAll(offset, res.pastquestions)
                        pages[pageKey] = pages[pageKey]!! + 1
                    }
                    indicators[loadingIndicatorKey]!!.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    indicators[loadingIndicatorKey]!!.value = false
                }
                e.printStackTrace()
            }
        }

    }
fun setPriceOfPastQuestion(b:PastQuestionPricingRequestBody){
    indicators["settingPrice${b.level}${b.semester}"]!!.value = true
    networkScope.launch {
        try{
            val res = Network.apiService.setPastQuestionPrice(b)
            withContext(Dispatchers.Main){
                pricing["${b.level}${b.semester}"]!!.value = b.pricing.toInt()
                indicators["settingPrice${b.level}${b.semester}"]!!.value = false
            }
        }catch (e:Exception){
            e.printStackTrace()
            withContext(Dispatchers.Main){
                indicators["settingPrice${b.level}${b.semester}"]!!.value = false
            }
        }
    }
}
    fun deletePastQuestion(
        defaultRequestBody: DefaultRequestBody,
        option: HashMap<String, String>,
        cb: (index: Int) -> Unit
    ) {
        val indicatorKey = KeyComposer.getLevelDeletingPastQuestionKey(option)
        val listKey = KeyComposer.getLevelPastQuestionListKey(option)
        indicators[indicatorKey]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.deletePastQuestion(defaultRequestBody)
                withContext(Dispatchers.Main) {
                    //_code.value = res.code
                    val index = allPastQuestions[listKey]!!.indexOfFirst {
                        it.hash == defaultRequestBody.id
                    }
                    allPastQuestions[listKey]!!.removeAt(index)
                    cb(index)
                    indicators[indicatorKey]!!.value = false
                    Toast.makeText(app.applicationContext, "deleted", Toast.LENGTH_SHORT).show()
                }
            } catch (e: retrofit2.HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators[indicatorKey]!!.value = false
                    Toast.makeText(app.applicationContext, "an error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun beginPastQuestionUpload(
        body: PastQuestionUpload,
        uri: Uri,
        cb: (index: Int, err: Boolean) -> Unit
    ) {
        val path = FileUtils.getPath(app.applicationContext, uri)
        val pqPart = preparePastQuestionForUpload(path!!, uri)
        val pqMeta = prepareExtraInput(body)
        val option = HashMap<String, String>()
        option["lid"] = body.lid
        option["semester"] = body.semester
        val uploadingIndicatorKey = KeyComposer.getLevelUploadingPastQuestionKey(option)
        val listKey = KeyComposer.getLevelPastQuestionListKey(option)
        indicators[uploadingIndicatorKey]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.uploadPastQuestion(pqPart, pqMeta)
                val newPastQuestion = PastQuestion(
                    res.data.title,
                    res.data.start,
                    res.data.end,
                    res.data.pid,
                    res.data.createdAt
                )
                withContext(Dispatchers.Main) {
                    allPastQuestions[listKey]!!.add(newPastQuestion)
                    cb(allPastQuestions[listKey]!!.size, false)
                    //_code.value = res.code
                    indicators[uploadingIndicatorKey]!!.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    indicators[uploadingIndicatorKey]!!.value = false
                }

                when (e) {
                    is retrofit2.HttpException -> {
                        withContext(Dispatchers.Main) {
                            cb(-1, true)
                        }
                    }
                    is java.net.SocketTimeoutException -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                app.applicationContext,
                                "an error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                e.printStackTrace()

            }
        }
    }

    fun prepareExtraInput(data: PastQuestionUpload): MultipartBody.Part {
        val gson = Gson()
        val requestData = gson.toJson(data)
        return MultipartBody.Part.createFormData("data", requestData)
    }

    fun preparePastQuestionForUpload(path: String, uri: Uri): MultipartBody.Part {
        val file = File(path)
        val requestBody = RequestBody.create(MediaType.parse(getMimeType(uri)!!), file)
        return MultipartBody.Part.createFormData("pq", file.name, requestBody)
    }

    fun getMimeType(uri: Uri): String? {
        return app.applicationContext.contentResolver.getType(uri)
    }

    private fun filterList(incoming: List<PastQuestion>, option: HashMap<String, String>) {
        val listKey = KeyComposer.getLevelPastQuestionListKey(option)
        incoming.forEach {
            val index = allPastQuestions[listKey]!!.indexOfFirst { pq ->
                pq.hash == it.hash
            }
            if (index >= 0) {
                allPastQuestions[listKey]!!.removeAt(index)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.complete()
    }
}
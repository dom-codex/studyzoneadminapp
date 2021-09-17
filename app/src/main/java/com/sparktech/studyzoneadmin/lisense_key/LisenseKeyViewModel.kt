package com.sparktech.studyzoneadmin.lisense_key

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sparktech.studyzoneadmin.helpers.Constants
import com.sparktech.studyzoneadmin.helpers.showToast
import com.sparktech.studyzoneadmin.models.LisenseKey
import com.sparktech.studyzoneadmin.models.Vendor
import com.sparktech.studyzoneadmin.models.VendorKeyStats
import com.sparktech.studyzoneadmin.network.Network
import com.sparktech.studyzoneadmin.request_models.CreateVendorRequestBody
import com.sparktech.studyzoneadmin.request_models.DefaultRequestBody
import com.sparktech.studyzoneadmin.request_models.GenerateKeysRequestBody
import com.sparktech.studyzoneadmin.request_models.UpdateKeyRequestBody
import com.sparktech.studyzoneadmin.response_models.DefaultNetworkResponse
import com.sparktech.studyzoneadmin.response_models.GetKeyStatisticsResponseBody
import com.sparktech.studyzoneadmin.response_models.GetLisenseKeyResponseBody
import kotlinx.coroutines.*

class LisenseKeyViewModel(private val app: Application, private val admin: String) :
    AndroidViewModel(app) {
    private val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)
    val indicators = HashMap<String, MutableLiveData<Boolean>>()
    val pages = HashMap<String, Int>()
    val keys = HashMap<String, MutableList<LisenseKey>>()
    private val _keyStats = MutableLiveData<GetKeyStatisticsResponseBody>()
    val keyStats: LiveData<GetKeyStatisticsResponseBody>
        get() = _keyStats

    init {
        _keyStats.value = GetKeyStatisticsResponseBody(
            0, "", 0, 0,
            0, 0, 0, 0
        )
    }

    val vendors = mutableListOf<Vendor>()
    fun fetchKeys(type: String) {
        indicators["loading${type}"]!!.value = true
        val query = HashMap<String, String>()
        query["adminId"] = admin
        query["type"] = type
        query["page"] = pages["${type}pages"].toString()
        networkScope.launch {
            try {
                val res = Network.apiService.getLisenseKeys(query)
                val index = keys[type]!!.indexOfFirst {
                    it.isLoading
                }
                println(index)
                if (index >= 0) keys[type]!!.removeAt(index)
                val itemsToAdd = listFilter(res.keys, type)
                keys[type]!!.addAll(itemsToAdd)
                if ((pages["${type}pages"]!! + 1) * Constants.SERVER_LIMIT <= keys[type]!!.size) {
                    pages["${type}pages"] = pages["${type}pages"]!! + 1
                }
                withContext(Dispatchers.Main) {
                    indicators["loading${type}"]!!.value = false

                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators["loading${type}"]!!.value = false
                }
                when (e) {
                    is retrofit2.HttpException -> {
                        val gson = Gson()
                        val adapter = gson.getAdapter(GetLisenseKeyResponseBody::class.java)
                        val err = e.response()?.errorBody()
                        val body = adapter.fromJson(err?.string())
                        withContext(Dispatchers.Main) {
                            showToast(app.applicationContext, body.message)
                        }
                    }
                    is java.net.SocketException -> {
                        withContext(Dispatchers.Main) {
                            showToast(app.applicationContext, "connection timeout")
                        }
                    }
                    else -> {
                        withContext(Dispatchers.Main) {
                            showToast(app.applicationContext, "an error occurred")
                        }
                    }
                }
            }
        }
    }

    fun fetchKeysStatistics() {
        indicators["stats"]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.getKeysStatistics(admin)
                withContext(Dispatchers.Main) {
                    _keyStats.value = GetKeyStatisticsResponseBody(
                        res.code,
                        res.message,
                        res.nTotalKeys,
                        res.nUsedKeys,
                        res.nNotUsedKeys,
                        res.costOfAllKeys,
                        res.costOfUsedKeys,
                        res.costOfUnUsedKeys
                    )
                }

                withContext(Dispatchers.Main) {
                    indicators["stats"]!!.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators["stats"]!!.value = false
                }
                when (e) {
                    is retrofit2.HttpException -> {
                        val gson = Gson()
                        val adapter = gson.getAdapter(GetKeyStatisticsResponseBody::class.java)
                        val err = e.response()?.errorBody()
                        val body = adapter.fromJson(err?.string())
                        withContext(Dispatchers.Main) {
                            showToast(app.applicationContext, body.message)
                        }
                    }
                    is java.net.SocketException -> {
                        withContext(Dispatchers.Main) {
                            showToast(app.applicationContext, "connection timeout")
                        }
                    }
                    else -> {
                        withContext(Dispatchers.Main) {
                            showToast(app.applicationContext, "an error occured")
                        }
                    }
                }
            }
        }
    }

    fun updateKey(body: UpdateKeyRequestBody) {
        indicators["updating"]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.updateKeyWorth(body)
                var key = keys["NOT_USED"]!!.find {
                    it.keyId == body.keyId
                }
                key?.price = body.price.toFloat()
                withContext(Dispatchers.Main) {
                    showToast(app.applicationContext, "updated")
                    indicators["updating"]!!.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                when (e) {
                    is retrofit2.HttpException -> {
                        val gson = Gson()
                    }
                    is java.net.SocketException -> {
                        withContext(Dispatchers.Main) {
                            showToast(app.applicationContext, "connection time out")
                        }
                    }
                    else -> {
                        withContext(Dispatchers.Main) {
                            showToast(app.applicationContext, "an error occurred")
                        }
                    }
                }
            }
        }
    }

    fun deleteKey(body: UpdateKeyRequestBody) {
        indicators["updating"]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.deleteKey(body)
                val key = keys["ALL"]!!.find {
                    it.keyId == body.keyId
                }
                val key2 = keys["USED"]!!.find {
                    it.keyId == body.keyId
                }
                val key3 = keys["NOT_USED"]!!.find {
                    it.keyId == body.keyId
                }
                if (key != null) {
                    keys["ALL"]!!.removeAll {
                        it.keyId == body.keyId
                    }
                    updateAllStats(key)
                    if (key.isUsed) {
                        updateUsedStats(key)
                    } else {
                        updateNotUsedStats(key)
                    }
                } else if (key2 != null) {
                    keys["USED"]!!.removeAll {
                        it.keyId == body.keyId
                    }
                    updateUsedStats(key2)
                    updateAllStats(key2)

                } else if (key3 != null) {
                    keys["NOT_USED"]!!.removeAll {
                        it.keyId == body.keyId
                    }
                    updateNotUsedStats(key3)
                    updateAllStats(key3)

                }
                withContext(Dispatchers.Main) {
                    showToast(app.applicationContext, "deleted!!!")
                    indicators["updating"]!!.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                when (e) {
                    is retrofit2.HttpException -> {
                        val gson = Gson()
                        val adapter = gson.getAdapter(DefaultNetworkResponse::class.java)
                        val err = e.response()?.errorBody()
                        val body = adapter.fromJson(err?.string())
                        withContext(Dispatchers.Main) {
                            showToast(app.applicationContext, body.message ?: "an error occurred")
                        }
                    }
                    is java.net.SocketException -> {
                        withContext(Dispatchers.Main) {
                            showToast(app.applicationContext, "connection timeout")
                        }
                    }
                    else -> {
                        withContext(Dispatchers.Main) {
                            showToast(app.applicationContext, "an error occurred")
                        }
                    }
                }
            }
        }
    }

    fun createVendor(name: String, phone: String) {
        val body = CreateVendorRequestBody(name, phone, admin)
        indicators["updatingVendorList"]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.createVendor(body)
                vendors.add(res.vendor)
                withContext(Dispatchers.Main) {
                    indicators["updatingVendorList"]!!.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators["updatingVendorList"]!!.value = false
                }
            }
        }
    }

    fun deleteVendor(index: Int) {
        indicators["updatingVendorList"]!!.value = true
        networkScope.launch {
            try {
                val vendor = vendors[index]
                if (vendor.name == "ADMIN" && index <=0) {
                    showToast(app.applicationContext, "cannot delete admin")
                    return@launch
                }
                val body = DefaultRequestBody(vendor.vendorId, admin)
                val res = Network.apiService.deleteVendor(body)
                vendors.removeAt(index)
                withContext(Dispatchers.Main) {
                    indicators["updatingVendorList"]!!.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators["updatingVendorList"]!!.value = false

                }
            }
        }
    }

    fun fetchVendors() {
        indicators["loadingVendors"]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.getVendors(admin)
                if (res.vendors.isNotEmpty()) {
                    vendors.addAll(res.vendors)
                    println(vendors)
                }
                withContext(Dispatchers.Main) {
                    indicators["loadingVendors"]!!.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators["loadingVendors"]!!.value = false
                }
            }
        }
    }

    fun vendorKeys(type: String, vendorId: String) {
        indicators["loadingkey$type$vendorId"]!!.value = true
        val query = HashMap<String, String>()
        query["adminId"] = admin
        query["page"] = pages["${vendorId}${type}page"]!!.toString()
        query["type"] = type
        query["vendorId"] = vendorId
        networkScope.launch {
            try {
                val res = Network.apiService.getVendorKeys(query)
                val index = keys["${vendorId}${type}keys"]!!.indexOfFirst {
                    it.isLoading
                }
                if (index >= 0) keys["${vendorId}${type}keys"]!!.removeAt(index)
                val itemsToAdd = listFilter(res.keys, "${vendorId}${type}keys")
                keys["${vendorId}${type}keys"]!!.addAll(itemsToAdd)
                if ((pages["${vendorId}${type}page"]!! + 1) * Constants.SERVER_LIMIT <= keys["${vendorId}${type}keys"]!!.size) {
                    pages["${vendorId}${type}page"] = pages["${vendorId}${type}page"]!! + 1
                }
                withContext(Dispatchers.Main) {
                    indicators["loadingkey$type$vendorId"]!!.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators["loadingkey$type$vendorId"]!!.value = false
                }
            }
        }
    }

    var vendorStats: VendorKeyStats? = null
    fun vendorKeyStats(vendorId: String) {
        indicators["loading$vendorId"]!!.value = true
        println(vendorId)
        networkScope.launch {
            try {
                val res = Network.apiService.getVendorStats(admin,vendorId)
                val stats = VendorKeyStats(
                    res.noOfUsedKeys,
                    res.noOfKeysGenerated,
                    res.totalcostOfKeys,
                    res.totalcostOfUsedKeys,
                    res.noOfUnUsedKeys,
                    res.totalcostOfUnUsedKeys
                )
                vendorStats = stats
                withContext(Dispatchers.Main) {
                    indicators["loading$vendorId"]!!.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators["loading$vendorId"]!!.value = false
                }
                when (e) {
                    is retrofit2.HttpException -> {
                        val gson = Gson()
                    }
                }
            }
        }
    }

    fun generateKeys(body: GenerateKeysRequestBody) {
        indicators["generating"]!!.value = true
        networkScope.launch {
            try {
                val res = Network.apiService.generateKeys(body)
                if (res.data.isNotEmpty()) {
                    keys["ALL"]!!.addAll(res.data)
                    keys["NOT_USED"]!!.addAll(res.data)
                }
                withContext(Dispatchers.Main) {
                    val totalcost = (body.worth * body.nkeys) + _keyStats.value?.costOfAllKeys!!
                    val costofUnUsed = totalcost - _keyStats.value!!.costOfUsedKeys
                    val totalKeys = _keyStats.value!!.nTotalKeys + body.nkeys
                    val totalUnUsed = _keyStats.value!!.nNotUsedKeys + body.nkeys
                    _keyStats.value = _keyStats.value?.copy(
                        costOfAllKeys = totalcost,
                        costOfUnUsedKeys = costofUnUsed,
                        nTotalKeys = totalKeys,
                        nNotUsedKeys = totalUnUsed
                    )

                    indicators["generating"]!!.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    indicators["generating"]!!.value = false
                }
            }
        }
    }

    private suspend fun listFilter(newList: List<LisenseKey>, type: String): List<LisenseKey> {
        return withContext(Dispatchers.IO) {
            val itemsToAdd = mutableListOf<LisenseKey>()
            newList.forEach {
                val alreadyAdded = keys[type]!!.contains(it)
                if (!alreadyAdded) {
                    itemsToAdd.add(it)
                }
            }
            itemsToAdd
        }
    }

    private suspend fun updateUsedStats(key: LisenseKey) {
        val totalkeysbefore = _keyStats.value!!.nUsedKeys
        val allCostbefore = _keyStats.value!!.costOfUsedKeys
        withContext(Dispatchers.Main) {
            _keyStats.value = _keyStats.value!!.copy(
                nUsedKeys = totalkeysbefore - 1,
                costOfUsedKeys = allCostbefore - key.price.toLong()
            )
        }
    }

    private suspend fun updateNotUsedStats(key: LisenseKey) {

        val totalkeysbefore = _keyStats.value!!.nNotUsedKeys
        val allCostbefore = _keyStats.value!!.costOfUnUsedKeys
        withContext(Dispatchers.Main) {
            _keyStats.value = _keyStats.value!!.copy(
                nNotUsedKeys = totalkeysbefore - 1,
                costOfUnUsedKeys = allCostbefore - key.price.toLong()
            )
        }
    }

    private suspend fun updateAllStats(key: LisenseKey) {
        //reflect change in all lists
        val totalkeysbeforeAll = _keyStats.value!!.nTotalKeys
        val allCostbeforeAll = _keyStats.value!!.costOfAllKeys
        withContext(Dispatchers.Main) {
            _keyStats.value = _keyStats.value!!.copy(
                nTotalKeys = totalkeysbeforeAll - 1,
                costOfAllKeys = allCostbeforeAll - key.price.toLong()
            )
        }
    }

    private suspend fun removeLoader(type: String) {
        withContext(Dispatchers.IO) {
            val index = keys[type]!!.indexOfFirst {
                it.isLoading
            }
            if (index >= 0) keys[type]!!.removeAt(index)
        }
    }
}
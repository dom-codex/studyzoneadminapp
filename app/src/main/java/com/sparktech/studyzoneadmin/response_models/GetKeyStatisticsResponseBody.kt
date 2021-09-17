package com.sparktech.studyzoneadmin.response_models

data class GetKeyStatisticsResponseBody(
    val code:Int,
    val message:String,
    val nTotalKeys:Long,
    val nUsedKeys:Long,
    val nNotUsedKeys:Long,
    val costOfAllKeys:Long,
    val costOfUsedKeys:Long,
    val costOfUnUsedKeys: Long,)

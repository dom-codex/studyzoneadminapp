package com.sparktech.studyzoneadmin.response_models

data class GetVendorStatsResponseBody(
    val noOfUsedKeys: String,
    val noOfKeysGenerated: String,
    val totalcostOfKeys: String,
    val totalcostOfUsedKeys: String,
    val noOfUnUsedKeys: String,
    val totalcostOfUnUsedKeys: String,
    val code:Int,
    val message:String
)

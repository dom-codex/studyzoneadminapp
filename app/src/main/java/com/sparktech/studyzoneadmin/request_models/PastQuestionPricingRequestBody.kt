package com.sparktech.studyzoneadmin.request_models

import com.google.gson.annotations.SerializedName

data class PastQuestionPricingRequestBody(
    val adminId:String,
    @SerializedName("sid")
    val school: String,
    @SerializedName("fid")
    val faculty: String,
    @SerializedName("did")
    val department: String,
    @SerializedName("lid")
    val level: String,
    val semester: String,
    val pricing:Long
)

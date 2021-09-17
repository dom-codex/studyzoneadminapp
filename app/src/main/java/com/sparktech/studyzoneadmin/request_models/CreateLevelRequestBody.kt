package com.sparktech.studyzoneadmin.request_models

import com.google.gson.annotations.SerializedName

data class CreateLevelRequestBody(
    @SerializedName("uid")
    val admin:String,
    val sid:String,
    val fid:String,
    val did:String,
    val level:String
)

package com.sparktech.studyzoneadmin.models

import com.google.gson.annotations.SerializedName

data class SchoolDetails(
    @SerializedName("fid")
    val hash:String,
    val name:String,
    val createdAt:String,
    @SerializedName("children")
    val departments:String
)

package com.sparktech.studyzoneadmin.models

import com.google.gson.annotations.SerializedName

data class Download(
    val title:String,
    val endYear:String,
    val startYear:String,
    val level:String,
    val semester:String,
    @SerializedName("name")
    val dept:String,
    @SerializedName("nameAbbr")
    val sch:String,
    @SerializedName("slug")
    val hash:String
)

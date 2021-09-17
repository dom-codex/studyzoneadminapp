package com.sparktech.studyzoneadmin.models

import com.google.gson.annotations.SerializedName

data class PastQuestion(
    val title:String,
    val startYear:String,
    val endYear:String,
    @SerializedName("pid")
    val hash:String,
    val createdAt:String,
    val isLoading:Boolean = false
)

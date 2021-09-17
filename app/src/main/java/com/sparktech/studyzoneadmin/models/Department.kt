package com.sparktech.studyzoneadmin.models

import com.google.gson.annotations.SerializedName

data class Department(
    val name:String,
    @SerializedName("did")
    val hash:String,
    val createdAt:String,
    val isLoading:Boolean = false
)

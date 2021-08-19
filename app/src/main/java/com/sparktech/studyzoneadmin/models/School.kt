package com.sparktech.studyzoneadmin.models

import com.google.gson.annotations.SerializedName

data class School(
        val name:String,
        val nameAbbr:String,
        val icon:String,
        val type:String,
        @SerializedName("sid")
        val schHash:String,
        val createdAt:String
)

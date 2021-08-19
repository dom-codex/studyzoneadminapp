package com.sparktech.studyzoneadmin.models

import com.google.gson.annotations.SerializedName

data class LisenseKey(
        val key:String?,
        @SerializedName("forWhom")
        val vendor:String?,
        @SerializedName("worth")
        val price:Float,
        val isUsed:Boolean,
        val usedBy:String?,
        val createdAt:String
)

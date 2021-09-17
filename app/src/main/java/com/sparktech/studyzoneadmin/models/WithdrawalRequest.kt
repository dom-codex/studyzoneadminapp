package com.sparktech.studyzoneadmin.models

import com.google.gson.annotations.SerializedName

data class WithdrawalRequest(
    val requesteeName: String?,
    val requestedBy: String,
    val amount: Int,
    val requesteeEmail: String?,
    val createdAt:String,
    @SerializedName("wid")
    val hash:String,
    val status:String,
    val videoLink:String?,
    val isLoading:Boolean= false
)

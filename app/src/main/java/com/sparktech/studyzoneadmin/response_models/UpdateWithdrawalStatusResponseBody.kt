package com.sparktech.studyzoneadmin.response_models

data class UpdateWithdrawalStatusResponseBody(
    val withdrawalId: String,
    val status: String,
    val message:String?,
    val code:Int
)
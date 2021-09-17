package com.sparktech.studyzoneadmin.request_models

data class UpdateWithdrawalStatusRequestBody(
    val withdrawalHash: String,
    val status: String,
    val adminId: String
)
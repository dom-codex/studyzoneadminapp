package com.sparktech.studyzoneadmin.request_models

data class GenerateKeysRequestBody(
    val adminId: String,
    val nkeys: Int,
    val whom: String,
    val worth: Long
)

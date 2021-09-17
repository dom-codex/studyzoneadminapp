package com.sparktech.studyzoneadmin.response_models

data class CreatePastQuestionResponseBody(
    val code: Int,
    val message: String?,
    val data: PastQuestionData
)

data class PastQuestionData(
    val title: String,
    val start: String,
    val end: String,
    val pid: String,
    val createdAt: String
)
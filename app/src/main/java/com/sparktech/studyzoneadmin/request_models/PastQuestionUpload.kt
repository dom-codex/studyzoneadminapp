package com.sparktech.studyzoneadmin.request_models

data class PastQuestionUpload(
    val title: String,
    val start: String,
    val end: String,
    val sid: String,
    val fid: String,
    val did: String,
    val lid:String,
    val semester:String,
    val uid:String
)
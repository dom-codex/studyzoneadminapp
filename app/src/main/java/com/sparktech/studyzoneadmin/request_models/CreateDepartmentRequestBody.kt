package com.sparktech.studyzoneadmin.request_models

data class CreateDepartmentRequestBody(
    val name:String,
    val sid:String,
    val fid:String,
    val uid:String
)
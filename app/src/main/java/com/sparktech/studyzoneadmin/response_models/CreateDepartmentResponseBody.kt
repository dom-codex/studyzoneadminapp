package com.sparktech.studyzoneadmin.response_models

import com.sparktech.studyzoneadmin.models.Department

data class CreateDepartmentResponseBody(val code: Int, val message: String?, val data: Department)

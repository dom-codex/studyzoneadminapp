package com.sparktech.studyzoneadmin.response_models

import com.sparktech.studyzoneadmin.models.SchoolDetails

data class CreateFacultyResponseBody(val code:Int,val message:String?, val data:SchoolDetails)
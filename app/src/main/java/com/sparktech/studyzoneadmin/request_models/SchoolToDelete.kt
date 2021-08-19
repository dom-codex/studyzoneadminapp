package com.sparktech.studyzoneadmin.request_models

import com.sparktech.studyzoneadmin.models.School

data class SchoolToDelete(val email:String,val adminId:String,val school: School)
package com.sparktech.studyzoneadmin.response_models

data class NewSchoolResponse(val data:NewSchool)
data class NewSchool(val name:String,
                     val type:String,
                     val nameAbbr:String,
                     val sid:String,
                     val icon:String,
                     val createdAt:String
)
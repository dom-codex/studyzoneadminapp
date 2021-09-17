package com.sparktech.studyzoneadmin.response_models

import com.sparktech.studyzoneadmin.models.Level

data class CreateLevelResponseBody(val code:Int,val message:String?,val data: Level)

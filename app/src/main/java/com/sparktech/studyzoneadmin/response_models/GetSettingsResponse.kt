package com.sparktech.studyzoneadmin.response_models

import com.sparktech.studyzoneadmin.models.Settings

data class GetSettingsResponse(val code:Int,val message:String,val settings:List<Settings>)
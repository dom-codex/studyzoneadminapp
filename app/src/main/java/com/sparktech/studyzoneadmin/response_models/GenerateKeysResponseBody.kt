package com.sparktech.studyzoneadmin.response_models

import com.sparktech.studyzoneadmin.models.LisenseKey
import com.sparktech.studyzoneadmin.models.Vendor

data class GenerateKeysResponseBody(val data:List<LisenseKey>, val code:Int, val message:String?)

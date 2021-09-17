package com.sparktech.studyzoneadmin.response_models

import com.sparktech.studyzoneadmin.models.LisenseKey

data class GetLisenseKeyResponseBody(val keys: List<LisenseKey>, val code: Int, val message: String)

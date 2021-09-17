package com.sparktech.studyzoneadmin.request_models

import com.sparktech.studyzoneadmin.models.Vendor

data class GetVendorsResponseBody(val code: Int, val message: String, val vendors: List<Vendor>)

package com.sparktech.studyzoneadmin.models

data class Vendor(
    val name: String,
    val phone: String,
    val vendorId: String,
    val isLoading: Boolean = false
)

package com.sparktech.studyzoneadmin.request_models

data class ToggleUserStatus(val adminId: String, val user: String, val block: Boolean)

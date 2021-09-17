package com.sparktech.studyzoneadmin.models

data class Referral(
    val name: String,
    val phone: String,
    val email: String,
    val createdAt: String,
    val isLoading: Boolean = false
)
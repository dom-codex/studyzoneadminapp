package com.sparktech.studyzoneadmin.models

data class User(
    val name: String,
    val email: String,
    val phone: String,
    val uid: String,
    val bank: String?,
    val accountNo: String?,
    val isActivated: Boolean,
    val isLoggedIn: Boolean,
    val noOfReferral: Int,
    val totalEarned: Int,
    val transactions: Int,
    val isBlocked:Boolean,
    val isLoading:Boolean = false
)
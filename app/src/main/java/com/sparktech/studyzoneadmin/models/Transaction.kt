package com.sparktech.studyzoneadmin.models

data class Transaction(
        val title:String,
        val UserEmail:String,
        val userRef:String,
        val transactionRef:String?,
        val amount:Int,
        val paymentMethod:String,
        val semester:String,
        val key:String,
        val userTxId:String,
        val createdAt:String
        )
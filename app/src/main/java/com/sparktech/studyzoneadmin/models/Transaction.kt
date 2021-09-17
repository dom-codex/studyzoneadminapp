package com.sparktech.studyzoneadmin.models

data class Transaction(
        val title:String,
        val userEmail:String,
        val userRef:String,
        val transactionRef:String?,
        val amount:Int,
        val paymentMethod:String,
        val semester:String,
        val key:String?,
        val userTxId:String,
        val createdAt:String,
        val school:TransactionSchoolInfo?,
        val faculty: TransactionFacultyInfo?,
        val department:TransactionDeptInfo?,
        val level:TransactionLevelInfo?,
        val isLoading:Boolean = false
        )
data class TransactionSchoolInfo(val name:String,val sid:String)
data class TransactionFacultyInfo(val name:String,val fid:String)
data class TransactionDeptInfo(val name:String,val did:String)
data class TransactionLevelInfo(val level:String,val lid:String)
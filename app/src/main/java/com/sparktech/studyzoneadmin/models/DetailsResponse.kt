package com.sparktech.studyzoneadmin.models

data class DetailsResponse(
        val transactions:List<Transaction>,
        val allKeys:List<LisenseKey>,
        val notUsedKeys:List<LisenseKey>,
        val usedKeys:List<LisenseKey>
)

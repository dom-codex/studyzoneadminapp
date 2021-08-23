package com.sparktech.studyzoneadmin.response_models

import com.sparktech.studyzoneadmin.models.Transaction

data class GetTransactionResponse(val transactions:List<Transaction>)
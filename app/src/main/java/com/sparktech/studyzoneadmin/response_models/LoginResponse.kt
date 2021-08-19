package com.sparktech.studyzoneadmin.response_models

data class LoginResponse(val code:Int,
                         val message:String,
                         val role:String,
                         val email:String,
                         val adminId:String,
                         val loggedIn:Boolean
)

package com.sparktech.studyzoneadmin.helpers

import android.content.SharedPreferences
import android.widget.Toast
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail

fun validateAdminEmail(email:String?):Boolean{
    if(email.isNullOrEmpty()){
        return false
    }
    return email.validEmail()
}
fun validateAdminPassword(password:String?):Boolean{
    if(password.isNullOrEmpty()){
        return false
    }
    return password.minLength(4)
}
fun saveAdminLoginData(sp:SharedPreferences,email: String,loggedIn:Boolean,role:String,adminId:String){
    sp.edit().apply {
        putString("email",email)
        putBoolean("loggedIn",loggedIn)
        putString("role",role)
        putString("adminId",adminId)
        apply()
    }

}
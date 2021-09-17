package com.sparktech.studyzoneadmin.helpers

import android.os.Bundle

class BundleExtractor {
    companion object {
        fun extractRequestDataFromBundle(bundle: Bundle): HashMap<String, String> {
            val schHash = bundle.getString("schHash", "")
            val facultyHash = bundle.getString("facultyHash", "")
            val semester = bundle.getString("semester", "")
            val did = bundle.getString("did", "")
            val lid = bundle.getString("lid", "")
            val queryOptions = HashMap<String, String>()
            queryOptions.put("sch", schHash)
            queryOptions.put("fid", facultyHash)
            queryOptions.put("did", did)
            queryOptions.put("lid", lid)
            queryOptions.put("semester", semester)
            return queryOptions
        }

        fun getFacultyHash(bundle: Bundle): String {
            return bundle.getString("fid")!!
        }

        fun getDepartmentHash(bundle: Bundle): String {
            return bundle.getString("did")!!
        }

        fun getLevelHash(bundle: Bundle): String {
            return bundle.getString("lid", "")!!
        }

        fun getLevelSemester(bundle: Bundle): String {
            return bundle.getString("semester", "")!!
        }
        fun getLevelAndSemester(bundle: Bundle):HashMap<String,String>{
            val semester = getLevelSemester(bundle)
            val lid = getLevelHash(bundle)
            val option = HashMap<String,String>()
            option["semester"] = semester
            option["lid"] = lid
            return option
        }
        fun getUsersType(bundle: Bundle):String{
            return bundle.getString("type","")!!
        }
        fun getUserHash(bundle:Bundle):String{
            return bundle.getString("user")!!
        }
    }
}
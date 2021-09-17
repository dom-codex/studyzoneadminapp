package com.sparktech.studyzoneadmin.helpers

import android.os.Bundle
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.models.Categories

object Categories {
    val categories = listOf(
        Categories(1, "Universities", R.drawable.ic_baseline_school_24),
        Categories(2, "Polytechnics", R.drawable.ic_baseline_school_24),
        Categories(3, "Students", R.drawable.ic_baseline_school_24),
        Categories(4, "Transactions", R.drawable.ic_baseline_school_24),
        Categories(9,"LisenseKeys",R.drawable.ic_baseline_vpn_key_24),
        Categories(10,"Vendors",R.drawable.ic_baseline_person_pin_24),
        Categories(5, "Notifications", R.drawable.ic_baseline_school_24),
        Categories(9, "Announcements", R.drawable.ic_baseline_school_24),
        Categories(6, "Withdrawal Requests", R.drawable.ic_baseline_school_24),
        Categories(7, "Support", R.drawable.ic_baseline_supervised_user_circle_24),
        Categories(8, "Settings", R.drawable.ic_baseline_supervised_user_circle_24),
    )
}

fun getSchName(bundle: Bundle): String {
    return bundle.getString("schName")!!
}

fun getSchHash(bundle: Bundle): String {
    return bundle.getString("schHash")!!
}

fun getSchoolType(bundle: Bundle): String {
    println(bundle.getString("schoolType")!!)
    return bundle.getString("schoolType")!!
}
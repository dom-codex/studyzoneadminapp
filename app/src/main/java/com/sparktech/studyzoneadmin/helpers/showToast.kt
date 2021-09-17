package com.sparktech.studyzoneadmin.helpers

import android.content.Context
import android.widget.Toast

fun showToast(c: Context, msg: String) {
    Toast.makeText(c, msg, Toast.LENGTH_LONG).show()
}
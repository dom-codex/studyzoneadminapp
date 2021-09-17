package com.sparktech.studyzoneadmin.helpers

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

class MediaUtils {
    companion object {
        fun getPastQuestionInfo(context: Context, uri: Uri): HashMap<String, String>? {
            val data = context.contentResolver.query(uri, null, null, null, null)?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                it.moveToFirst()
                val name = it.getString(nameIndex)
                val size = it.getLong(sizeIndex)
                val dataValues = HashMap<String, String>()
                dataValues.put("name", name)
                dataValues.put("size", size.toString())
                dataValues
            }
            return data
        }
    }
}
